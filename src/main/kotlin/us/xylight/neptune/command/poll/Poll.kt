package us.xylight.neptune.command.poll

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import us.xylight.neptune.command.Command
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.handler.CommandHandler

class Poll : Command {
    override val name = "poll"
    override val description = "Creates a poll/survey."
    override val options: List<OptionData> = emptyList()
    override val subcommands: List<Subcommand> = listOf(
        Create(),
        CreateAdvanced()
    )
    override val permission = null

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        CommandHandler.subcommandFromName(
            subcommands, interaction.subcommandName!!
        )?.execute(interaction)
    }

    companion object {
        fun createPollMessage(
            nums: List<String>,
            question: String,
            choices: List<String>,
            interaction: SlashCommandInteractionEvent
        ) {
            val embed = EmbedBuilder().setTitle(question).setColor(Config.accent)
            choices.forEachIndexed { index, choice ->
                embed.addField(nums[index], choice, false)
            }

            interaction.reply("").setEmbeds(embed.build()).queue()
            interaction.hook.retrieveOriginal().queue { message ->
                choices.forEachIndexed { index, _ ->
                    message.addReaction(Emoji.fromUnicode(nums[index])).queue()
                }
            }
        }


        val nums: List<String> = listOf(
            "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣"
        )
    }
}