package us.xylight.neptune.command.poll

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand
import java.util.stream.Stream

object CreateAdvanced : Subcommand {
    override val name = "createadvanced"
    override val description = "Up to 9 options. Separate questions with /"
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "question", "The question to ask.", true),
        OptionData(OptionType.STRING, "choices", "Separate choices with /.", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val question = interaction.getOption("question")!!.asString
        val choicesString = interaction.getOption("choices")!!.asString
        val choices = choicesString.split("/").take(9)

        Poll.createPollMessage(question, choices, interaction)
    }
}