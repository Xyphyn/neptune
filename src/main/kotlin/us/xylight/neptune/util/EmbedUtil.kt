package us.xylight.neptune.util

import net.dv8tion.jda.api.EmbedBuilder
import us.xylight.neptune.config.Config

object EmbedUtil {
    fun simpleEmbed(title: String, desc: String): EmbedBuilder {
        return EmbedBuilder().setTitle(title).setDescription(desc).setColor(Config.conf.misc.accent)
    }

    fun simpleEmbed(title: String, desc: String, color: Int): EmbedBuilder {
        return EmbedBuilder().setTitle(title).setDescription(desc).setColor(color)
    }

}