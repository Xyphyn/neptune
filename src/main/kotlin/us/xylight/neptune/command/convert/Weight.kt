package us.xylight.neptune.command.convert

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.util.EmbedUtil
import java.text.DecimalFormat

object Weight : Subcommand {
    /**
     * An enum to represent temperature units.
     * @param from Converts from grams to the enum's unit.
     * @param to Converts from the enum's unit to grams.
     * @param abbr The abbreviation to use for the temperature. e.g. `g` for grams
     */
    enum class WeightUnit(val from: (grams: Float) -> Float, val to: (weight: Float) -> Float, val abbr: String) {
        GRAMS({ it }, { it }, "g"),
        KILOGRAMS({ grams -> grams / 1_000F }, { weight -> weight * 1_000F }, "kg"),
        MILLIGRAM({ grams -> grams * 1_000F }, { weight -> weight / 1_000F }, "mg"),
        OUNCES({ grams -> grams / 28.35F }, { weight -> weight * 28.35F }, "oz"),
        POUNDS({ grams -> grams / 453.6F }, { weight -> weight * 453.6F }, "lb"),
        USATON({ grams -> grams / 907_200F }, { weight -> weight * 907_200 }, "tn"),
        TON({ grams -> grams / 0.000001F }, { weight -> weight * 1_000_000 }, "t");

        companion object {
            fun convert(from: WeightUnit, to: WeightUnit, num: Float): Float {
                val inGrams = from.to(num)
                return to.from(inGrams)
            }
        }
    }

    private val unitChoices = WeightUnit.values().map { unit ->
        Command.Choice(unit.name.lowercase().capitalize(), unit.name)
    }

    override val name = "weight"
    override val description = "Converts weight/mass units."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "from", "The unit to convert from.", true).addChoices(unitChoices),
        OptionData(OptionType.STRING, "to", "The unit to convert to.", true).addChoices(unitChoices),
        OptionData(OptionType.NUMBER, "num", "The number to convert.", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val from = WeightUnit.values()
            .find { interaction.getOption("from")!!.asString.lowercase() == it.name.lowercase() }!!

        val to = WeightUnit.values()
            .find { interaction.getOption("to")!!.asString.lowercase() == it.name.lowercase() }!!

        val num = interaction.getOption("num")!!.asDouble

        val converted = WeightUnit.convert(from, to, num.toFloat())

        val dec = DecimalFormat("0.#")

        interaction.reply("").setEmbeds(
            EmbedUtil.simpleEmbed("Conversion", "${dec.format(num)}${from.abbr} = ${dec.format(converted)}${to.abbr}").build()
        ).queue()
    }

}