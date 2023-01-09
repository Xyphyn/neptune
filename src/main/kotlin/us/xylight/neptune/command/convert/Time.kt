package us.xylight.neptune.command.convert

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.util.EmbedUtil
import java.text.DecimalFormat

object Time : Subcommand {
    enum class TimeUnit(val from: (seconds: Float) -> Float, val to: (time: Float) -> Float, val abbr: String) {
        SECONDS({ it }, { it }, "s"),
        MILLISECONDS({ seconds -> seconds * 1000F }, { ms -> ms / 1000F }, "ms"),
        MINUTES({ seconds -> seconds / 60F }, { minutes -> minutes * 60F }, "m"),
        HOURS({ seconds -> seconds / 3600F }, { hours -> hours * 3600F }, "h"),
        DAYS({ seconds -> seconds / 86400F }, { days -> days * 86400F }, "d"),
        WEEKS({ seconds -> seconds / 604800F }, { weeks -> weeks * 604800F }, "w"),
        YEARS({ seconds -> seconds / 31536086F }, { years -> years * 31536086F }, "y");

        companion object {
            fun convert(from: TimeUnit, to: TimeUnit, num: Float): Float {
                val inSeconds = from.to(num)
                return to.from(inSeconds)
            }
        }
    }

    private val unitChoices = TimeUnit.values().map { unit ->
        Command.Choice(unit.name.lowercase().capitalize(), unit.name)
    }

    override val name = "time"
    override val description = "Converts between time."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "from", "The unit to convert from.", true).addChoices(unitChoices),
        OptionData(OptionType.STRING, "to", "The unit to convert to.", true).addChoices(unitChoices),
        OptionData(OptionType.NUMBER, "num", "The number to convert.", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val from = TimeUnit.values()
            .find { interaction.getOption("from")!!.asString.lowercase() == it.name.lowercase() }!!

        val to = TimeUnit.values()
            .find { interaction.getOption("to")!!.asString.lowercase() == it.name.lowercase() }!!

        val num = interaction.getOption("num")!!.asDouble

        val converted = TimeUnit.convert(from, to, num.toFloat())

        val dec = DecimalFormat("0.#")

        interaction.reply("").setEmbeds(
            EmbedUtil.simpleEmbed("Conversion", "${dec.format(num)}${from.abbr} = ${dec.format(converted)}${to.abbr}").build()
        ).queue()
    }

}