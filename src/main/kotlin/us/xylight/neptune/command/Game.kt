package us.xylight.neptune.command

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.Command.Choice as OptionChoice
import us.xylight.neptune.games.GameType
import us.xylight.neptune.util.EmbedUtil
import java.util.Collections

class Game : Command {
    override val name = "game"
    override val description = "Creates a game"
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "game", "test", true).addChoices(
            OptionChoice("First Reply", "FirstMessage")
        ),
        OptionData(OptionType.INTEGER, "seconds", "How long should the game last? (In seconds)", true)
    )
    override val subcommands: List<Subcommand> = Collections.emptyList()
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val sec = interaction.getOption("seconds")!!
        val game = interaction.getOption("game")!!
        val gameType = GameType.gameTypeFromString(game.asString) ?: return

        gameType.createGame((sec.asInt * 1000).toLong(), interaction.channel)

        interaction.reply("").setEmbeds(EmbedUtil.simpleEmbed("Created", "Game was created.").build()).setEphemeral(true).queue()
    }

}