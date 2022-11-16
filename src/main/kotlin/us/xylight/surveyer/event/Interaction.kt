package us.xylight.surveyer.event

import dev.minn.jda.ktx.events.listener
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.handler.CommandHandler
import us.xylight.surveyer.util.EmbedUtil
import java.util.Base64

class Interaction(val jda: JDA, commandHandler: CommandHandler) {
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
}