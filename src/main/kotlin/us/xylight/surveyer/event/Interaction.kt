package us.xylight.surveyer.event

import dev.minn.jda.ktx.events.listener
import kotlinx.coroutines.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import us.xylight.surveyer.command.ComponentCommand
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.handler.CommandHandler
import us.xylight.surveyer.util.EmbedUtil
import java.util.Base64
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class Interaction(val jda: JDA, commandHandler: CommandHandler) {
    companion object {

        private val buttonListeners: MutableMap<String, suspend (interaction: ButtonInteractionEvent) -> Boolean> = mutableMapOf()

        suspend fun subscribe(buttonId: String, onChange: suspend (interaction: ButtonInteractionEvent) -> Boolean) {
            buttonListeners[buttonId] = onChange

            CoroutineScope(Dispatchers.Default).launch {
                delay(TimeUnit.DAYS.toMillis(10))
                buttonListeners.remove(buttonId)
            }
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
                }
            }
        }

        jda.listener<ButtonInteractionEvent> {
//            for (command in commandHandler.commandClasses) {
//                if (command !is ComponentCommand) continue
//                if (command.subcommands.isNotEmpty()) {
//                    command.onButtonClick(it)
//                    continue
//                }
//                if (!command.handles.contains(it.button)) continue
//                command.onButtonClick(it)
//
//            }

            val delete = buttonListeners[it.button.id]?.invoke(it)
            if (delete == true) {
                buttonListeners.remove(it.button.id)
                it.editButton(it.button.asDisabled()).queue()
            }
        }
    }
}