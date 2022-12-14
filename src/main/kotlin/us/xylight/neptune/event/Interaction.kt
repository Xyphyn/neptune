package us.xylight.neptune.event

import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import us.xylight.neptune.command.CommandHandler
import us.xylight.neptune.command.roles.Roles
import us.xylight.neptune.config.Config
import us.xylight.neptune.util.EmbedUtil
import java.util.*
import java.util.concurrent.TimeUnit

class Interaction(val jda: JDA, commandHandler: CommandHandler) {
    companion object {
        private val timer = Timer()

        private val buttonListeners: MutableMap<String, suspend (interaction: ButtonInteractionEvent) -> Boolean> = mutableMapOf()

        fun subscribe(buttonId: String, onChange: suspend (interaction: ButtonInteractionEvent) -> Boolean) {
            buttonListeners[buttonId] = onChange

            val task: TimerTask = object : TimerTask() {
                override fun run() {
                    buttonListeners.remove(buttonId)
                }
            }

            timer.schedule(task, TimeUnit.MINUTES.toMillis(10))
        }

        fun unSubscribe(buttonId: String) {
            buttonListeners.remove(buttonId)
        }

        fun unSubscribe(button: Button, message: Message) {
            val disabledButtons: MutableList<Button> = mutableListOf()
            val actionRow: ActionRow? = message.actionRows.find { actionRow -> actionRow.buttons.contains(button) }

            actionRow?.buttons?.forEachIndexed {
                    index, btn  -> disabledButtons.add(index, btn.asDisabled())
            }

            message.editMessageComponents(ActionRow.of(disabledButtons)).queue()
        }
    }
    init {
        jda.listener<SlashCommandInteractionEvent> {
            val command = commandHandler.commandFromName(it.name)
            if (command?.permission != null) {
                if (it.member?.hasPermission(command.permission) != true) {
                    it.reply("").setEmbeds(
                        EmbedUtil.simpleEmbed(
                            "Missing Permission",
                            "${Config.uacIcon} You do not have the required permissions to execute that command.",
                            0xff0f0f
                        ).addField(
                            "Required",
                            "`${command.permission!!.name}`",
                            false
                        ).build()
                    ).queue()
                    return@listener
                }
            }
            try {
                command?.execute(it)
            } catch (exception: Exception) {
                if (it.user.id != Config.logUser) {
                    it.channel.sendMessage("").setEmbeds(
                        EmbedUtil.simpleEmbed(
                            "Error",
                            "${Config.errorIcon} An error occurred while executing the command. This has been logged.",
                            0xff1f1f
                        ).build()
                    ).queue()
                } else {
                    it.channel.sendMessage("").setEmbeds(
                        EmbedUtil.simpleEmbed(
                            "Error",
                            "${Config.errorIcon} A `${exception::class.simpleName}` occured while executing the command!",
                            0xff0f0f
                        ).addField(
                            "Message",
                            exception.message.toString(),
                            false
                        ).build()
                    ).queue()

                    exception.printStackTrace()
                }
            }
        }

        jda.listener<ButtonInteractionEvent> {
            val delete = buttonListeners[it.button.id]?.invoke(it)
            if (delete == true) {
                buttonListeners.remove(it.button.id)
                it.editButton(it.button.asDisabled()).queue()
            }
        }

        jda.listener<StringSelectInteractionEvent> {
            if (it.componentId.split("svy:roles:menu:").size < 2) return@listener

            (commandHandler.commandFromName("roles") as Roles).onSelect(it)
        }
    }
}