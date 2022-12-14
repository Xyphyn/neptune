package us.xylight.neptune.command.convert

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.util.EmbedUtil
import java.text.DecimalFormat

class Length : Subcommand {
    /**
     * An enum to represent temperature units.
     * @param from Converts from meters to the enum's unit.
     * @param to Converts from the enum's unit to meters.
     * @param abbr The abbreviation to use for the temperature. e.g. M for meters
     */
    enum class DistanceUnit(val from: (meters: Float) -> Float, val to: (length: Float) -> Float, val abbr: String) {
        METERS({ it }, { it }, "m"),
        KILOMETERS({ meters -> meters / 1000 }, { length -> length * 1000 }, "km"),
        CENTIMETERS({ meters -> meters * 100 }, { length -> length / 100 }, "cm"),
        INCHES({ meters -> meters * 39.37F }, { length -> length / 39.37F }, "in"),
        FEET({ meters -> meters * 3.281F }, { length -> length / 3.281F }, "ft"),
        YARDS({ meters -> meters * 1.094F }, { length -> length / 1.094F }, "yd"),
        MILES({ meters -> meters / 1609F }, { length -> length * 1609F }, "mi");

        companion object {
            fun convert(from: DistanceUnit, to: DistanceUnit, num: Float): Float {
                val inMeters = from.to(num)
                return to.from(inMeters)
            }
        }
    }

    private val unitChoices = DistanceUnit.values().map { unit ->
        Command.Choice(unit.name.lowercase().capitalize(), unit.name)
    }

    override val name = "length"
    override val description = "Converts length units."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "from", "The unit to convert from.", true).addChoices(unitChoices),
        OptionData(OptionType.STRING, "to", "The unit to convert to.", true).addChoices(unitChoices),
        OptionData(OptionType.NUMBER, "num", "The number to convert.", true)

    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val from = DistanceUnit.values()
            .find { interaction.getOption("from")!!.asString.lowercase() == it.name.lowercase() }!!

        val to = DistanceUnit.values()
            .find { interaction.getOption("to")!!.asString.lowercase() == it.name.lowercase() }!!

        val num = interaction.getOption("num")!!.asDouble

        val converted = DistanceUnit.convert(from, to, num.toFloat())

        val dec = DecimalFormat("0.#")

        interaction.reply("").setEmbeds(
            EmbedUtil.simpleEmbed("Conversion", "${dec.format(num)}${from.abbr} = ${dec.format(converted)}${to.abbr}").build()
        ).queue()
    }
}