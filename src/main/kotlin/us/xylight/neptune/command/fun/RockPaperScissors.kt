package us.xylight.neptune.command.`fun`

import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.edit
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.util.EmbedUtil
import kotlin.time.Duration

object RockPaperScissors : Subcommand {
    override val name = "rockpaperscissors"
    override val description = "Play rock paper scissors!"
    override val options: List<OptionData> = listOf()

    enum class RPSChoice {
        ROCK,
        PAPER,
        SCISSORS;

        val beats: RPSChoice
            get() = when(this) {
                ROCK -> SCISSORS
                PAPER -> ROCK
                SCISSORS -> PAPER
            }
    }

    enum class Win {
        WIN,
        LOSE,
        TIE
    }

    private fun choose(choice: RPSChoice): Win {
        val cpuChoice = RPSChoice.values().random()

        return when {
            choice.beats == cpuChoice.beats -> Win.TIE
            choice.beats == cpuChoice -> Win.WIN
            cpuChoice.beats == choice -> Win.LOSE

            else -> Win.LOSE
        }
    }

    private fun result(interaction: SlashCommandInteractionEvent, button: ButtonInteractionEvent, win: Win) {
        interaction.hook.retrieveOriginal().queue {
            message ->
            val components = message.buttons
            components.map { button -> button.asDisabled() }
            message.editMessageComponents(ActionRow.of(components)).queue()
        }

        button.replyEmbeds(EmbedUtil.simpleEmbed(
            "Rock Paper Scissors",
            "You ${ when (win) {
                Win.WIN -> "win!"
                Win.TIE -> "tied!"
                Win.LOSE -> "Lose..."
            } }"
        ).build()).queue()
    }

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val rock = interaction.jda.button(
            ButtonStyle.PRIMARY,
            "Rock",
            Emoji.fromUnicode("\uD83E\uDEA8"),
            false,
            Duration.parse("30s"),
            interaction.user
        ) { button ->
            result(interaction, button, choose(RPSChoice.ROCK))
        }

        val paper = interaction.jda.button(
            ButtonStyle.PRIMARY,
            "Paper",
            Emoji.fromUnicode("\uD83D\uDCDC"),
            false,
            Duration.parse("30s"),
            interaction.user
        ) { button ->
            result(interaction, button, choose(RPSChoice.PAPER))
        }

        val scissors = interaction.jda.button(
            ButtonStyle.PRIMARY,
            "Scissors",
            Emoji.fromUnicode("✂️"),
            false,
            Duration.parse("30s"),
            interaction.user
        ) { button ->
            result(interaction, button, choose(RPSChoice.SCISSORS))
        }

        interaction.replyEmbeds(EmbedUtil.simpleEmbed("Rock Paper Scissors", "Choose.").build()).setActionRow(rock, paper, scissors).queue()
    }
}