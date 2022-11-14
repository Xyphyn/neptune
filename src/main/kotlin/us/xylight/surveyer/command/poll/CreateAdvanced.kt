package us.xylight.surveyer.command.poll

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.surveyer.command.Subcommand

class CreateAdvanced : Subcommand {
    override val name = "createadvanced"
    override val description = "Up to 5 options. Separate questions with /"
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "question", "The question to ask.", true),
        OptionData(OptionType.STRING, "choices", "Separate choices with /.", true)
    )

    override fun execute(interaction: SlashCommandInteractionEvent) {
        val question = interaction.getOption("question")!!.asString
        val choicesString = interaction.getOption("choices")!!.asString
        val choices: List<String> = choicesString.split("/").subList(0, 5)

        Poll.createPollMessage(Poll.nums, question, choices, interaction)
    }
}