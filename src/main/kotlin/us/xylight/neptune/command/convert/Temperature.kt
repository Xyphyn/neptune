package us.xylight.neptune.command.convert

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.util.EmbedUtil
import java.text.DecimalFormat

object Temperature : Subcommand {
    /**
     * An enum to represent temperature units.
     * @param from Converts from Kelvin to the enum's unit.
     * @param to Converts from the enum's unit to Kelvin.
     * @param abbr The abbreviation to use for the temperature. e.g. F for fahrenheit
     */
    enum class TemperatureUnit(val from: (kelvin: Float) -> Float, val to: (temperature: Float) -> Float, val abbr: String) {
        KELVIN({ it }, { it }, "°K"),
        CELSIUS({ kelvin -> kelvin - 273.15F }, { celsius -> celsius + 273.15F }, "°C"),
        FAHRENHEIT({ kelvin -> (kelvin - 273.15F) * (9F / 5F) + 32 }, { fahrenheit -> (fahrenheit - 32F) * (5F / 9F) + 273.15F }, "°F");

        companion object {
            fun convert(from: TemperatureUnit, to: TemperatureUnit, num: Float): Float {
                val inKelvin = from.to(num)
                return to.from(inKelvin)
            }
        }
    }

    private val unitChoices = TemperatureUnit.values().map { unit ->
        Command.Choice(unit.name.lowercase().capitalize(), unit.name)
    }

    override val name = "temperature"
    override val description = "Converts temperature units."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "from", "The unit to convert from.", true).addChoices(unitChoices),
        OptionData(OptionType.STRING, "to", "The unit to convert to.", true).addChoices(unitChoices),
        OptionData(OptionType.NUMBER, "num", "The number to convert.", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val from = TemperatureUnit.values()
            .find { interaction.getOption("from")!!.asString.lowercase() == it.name.lowercase() }!!

        val to = TemperatureUnit.values()
            .find { interaction.getOption("to")!!.asString.lowercase() == it.name.lowercase() }!!

        val num = interaction.getOption("num")!!.asDouble

        val converted = TemperatureUnit.convert(from, to, num.toFloat())

        val dec = DecimalFormat("0.#")

        interaction.reply("").setEmbeds(
            EmbedUtil.simpleEmbed("Conversion", "${dec.format(num)}${from.abbr} = ${dec.format(converted)}${to.abbr}").build()
        ).queue()
    }

}