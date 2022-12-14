package us.xylight.neptune.command.poll

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Subcommand

class Create : Subcommand {
    override val name = "create"
    override val description = "Creates a poll."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "question", "The question to ask.", true),
        OptionData(OptionType.STRING, "choice1", "The first option.", true),
        OptionData(OptionType.STRING, "choice2", "The second option.", true)
    )

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val question = interaction.getOption("question")!!.asString
        val choices: List<String> =
            listOfNotNull(interaction.getOption("choice1")!!.asString, interaction.getOption("choice2")!!.asString)

        Poll.createPollMessage(Poll.nums, question, choices, interaction)
    }

}