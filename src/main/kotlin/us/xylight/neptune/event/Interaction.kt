package us.xylight.neptune.event

import dev.minn.jda.ktx.events.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import us.xylight.neptune.LogLevel
import us.xylight.neptune.Logger
import us.xylight.neptune.command.CommandHandler
import us.xylight.neptune.command.RatelimitedCommand
import us.xylight.neptune.command.roles.Roles
import us.xylight.neptune.config.Config
import us.xylight.neptune.util.EmbedUtil
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor

class Interaction(val jda: JDA, commandHandler: CommandHandler) {
    private val cooldowns = mutableMapOf<Long, Long>()

    init {
        jda.listener<SlashCommandInteractionEvent> {
            val command = commandHandler.commandFromName(it.name)
            if (command?.permission != null) {
                if (it.member?.hasPermission(command.permission) != true) {
                    it.reply("").setEmbeds(
                        EmbedUtil.simpleEmbed(
                            "Missing Permission",
                            "${Config.conf.emoji.uac} You do not have the required permissions to execute that command.",
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
                if (command is RatelimitedCommand) {
                    val cooldown: Long? = cooldowns[it.user.idLong]

                    if (cooldown != null) {
                        if (cooldown > System.currentTimeMillis()) {
                            it.replyEmbeds(
                                EmbedUtil.simpleEmbed(
                                    "Cooldown",
                                    "${Config.conf.emoji.warning} That command is on cooldown. You may only use this command every ${
                                        (command.cooldown / 1000)
                                    } seconds.",
                                    0xff0f0f
                                ).build()
                            ).setEphemeral(true).queue()

                            return@listener
                        }
                    }

                    cooldowns[it.user.idLong] = System.currentTimeMillis() + command.cooldown
                }

                command?.execute(it)
            } catch (exception: Exception) {
                if (it.user.id != Config.conf.user.logging.toString()) {
                    it.channel.sendMessage("").setEmbeds(
                        EmbedUtil.simpleEmbed(
                            "Error",
                            "${Config.conf.emoji.error} An error occurred while executing the command. This has been logged.",
                            0xff1f1f
                        ).build()
                    ).queue()

                    Logger.log("Error in ${it.guild?.name} - Command: ${it.name} - Subcommand: ${it.subcommandName ?: "None"}", LogLevel.ERROR)
                    exception.printStackTrace()

                } else {
                    it.channel.sendMessage("").setEmbeds(
                        EmbedUtil.simpleEmbed(
                            "Error",
                            "${Config.conf.emoji.error} A `${exception::class.simpleName}` occured while executing the command!",
                            0xff0f0f
                        ).addField(
                            "Message",
                            exception.message.toString(),
                            false
                        ).build()
                    ).queue()

                    Logger.log("Error in ${it.guild?.name} - Command: ${it.name} - Subcommand: ${it.subcommandName ?: "None"}", LogLevel.ERROR)
                    exception.printStackTrace()
                }
            }
        }

        jda.listener<StringSelectInteractionEvent> {

            Logger.log("Role selection in ${it.guild?.name} - ${it.values}", LogLevel.VERBOSE)
            if (it.componentId.split("svy:roles:menu:").size < 2) return@listener

            Roles.onSelect(it)
        }
    }
}