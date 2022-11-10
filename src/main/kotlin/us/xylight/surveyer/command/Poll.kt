package us.xylight.surveyer.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.util.EmbedUtil
import java.util.*

class Poll : Command {
    override val name = "poll"
    override val description = "Creates a poll/survey."
    override val options: List<OptionData> = Collections.emptyList()
    override val subcommands: List<SubcommandData> = listOf(
        SubcommandData("create", "Creates a poll/survey")
            .addOptions(
                OptionData(OptionType.STRING, "question", "The question to ask.", true),
                OptionData(OptionType.STRING, "choice1", "The first option.", true),
                OptionData(OptionType.STRING, "choice2", "The second option.", true)
            ),
        SubcommandData("createadvanced", "Creates a poll. Use / to separate your questions.")
            .addOptions(
                OptionData(OptionType.STRING, "question", "The question to ask.", true),
                OptionData(OptionType.STRING, "choices", "Separate choices with /.", true)
            )
    )
    
    private val nums: List<String> = listOf(
        "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣"
    )

    override fun execute(interaction: SlashCommandInteractionEvent) {
        when (interaction.subcommandName) {
            "create" -> {
                val question = interaction.getOption("question")!!.asString
                val choices: List<String> =
                    listOfNotNull(interaction.getOption("choice1")!!.asString, interaction.getOption("choice2")!!.asString)
                
                createMessage(nums, question, choices, interaction)
            }
            
            "createadvanced" -> {
                val question = interaction.getOption("question")!!.asString
                val choicesString = interaction.getOption("choices")!!.asString
                val choices: List<String> = choicesString.split("/").subList(0, 5)

                createMessage(nums, question, choices, interaction)
            }
        }
    }
    
    private fun createMessage(nums: List<String>, question: String, choices: List<String>, interaction: SlashCommandInteractionEvent) {
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
}