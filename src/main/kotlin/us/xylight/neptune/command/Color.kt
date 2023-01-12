package us.xylight.neptune.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.utils.FileUpload
import us.xylight.neptune.util.EmbedUtil
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import javax.imageio.ImageIO

object Color : Command {
    override val name = "color"
    override val description = "View a color and convert it."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "color", "The color to view in hex.", true)
    )
    override val subcommands: List<Subcommand> = listOf()
    override val permission: Permission? = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val image = BufferedImage(50, 50, 1)
        val graphics = image.createGraphics()

        var colorOp = interaction.getOption("color")!!.asString

        if (!colorOp.contains("#")) {
            colorOp = StringBuilder(colorOp).insert(0, '#').toString()
        }

        val color = kotlin.runCatching { Color.decode(colorOp) }.getOrElse {
            interaction.replyEmbeds(EmbedUtil.simpleEmbed("Error", "That color is invalid. Write it in hex (#FFFFFF)", 0xff0f0f).build()).queue()
            return
        }

        graphics.color = color

        graphics.fillRect(0, 0, 50, 50)

        val os = ByteArrayOutputStream()

        ImageIO.write(image, "png", os)

        val upload = FileUpload.fromData(os.toByteArray(), "color.png")

        val hsv = FloatArray(3)
        Color.RGBtoHSB(color.red, color.green, color.blue, hsv)

        val embed = EmbedBuilder().setImage("attachment://color.png").setColor(color)
            .addField("Hex", colorOp.uppercase(), true)
            .addField("RGB", "${color.red}, ${color.blue}, ${color.green}", true)
            .addField("HSV", "${String.format("%.3f", hsv[0])}, ${String.format("%.3f", hsv[1])}, ${String.format("%.3f", hsv[2])}", true)

        interaction.replyEmbeds(embed.build())
            .setFiles(upload)
            .queue()
    }
}