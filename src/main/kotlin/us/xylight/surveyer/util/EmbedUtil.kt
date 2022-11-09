package us.xylight.surveyer.util

import net.dv8tion.jda.api.EmbedBuilder
import us.xylight.surveyer.config.Config

class EmbedUtil {
    companion object {
        fun simpleEmbed(title: String, desc: String): EmbedBuilder {
            return EmbedBuilder().setTitle(title).setDescription(desc).setColor(Config.accent)
        }
    }
}