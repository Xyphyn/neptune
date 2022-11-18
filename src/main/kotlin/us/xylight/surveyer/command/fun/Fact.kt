package us.xylight.surveyer.command.`fun`

import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.EmbedBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import okhttp3.Request
import us.xylight.surveyer.command.Subcommand
import us.xylight.surveyer.config.Config
import us.xylight.surveyer.event.Interaction
import us.xylight.surveyer.handler.CommandHandler
import java.util.Timer
import java.util.TimerTask

class Fact : Subcommand {
    override val name = "fact"
    override val description = "Fetches a random fact."
    override val options: List<OptionData> = listOf()
    private val client = CommandHandler.httpClient

    @Serializable
    private data class Fact(val text: String, @SerialName("source_url") val source: String)

    private val json = Json { ignoreUnknownKeys = true }

    private fun fetchFact(): Fact {
        val request = Request.Builder()
            .url("https://uselessfacts.jsph.pl/random.json?language=en")
            .build()

        val response = client.newCall(request).execute()
        val text = response.body?.string()

        val fact = json.decodeFromString<Fact>(text!!)

        response.body?.close()
        response.close()

        return fact
    }

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.deferReply().queue()

        var fact = fetchFact()
        val embed: EmbedBuilder = EmbedBuilder().setTitle("Random Fact").setDescription(fact.text).setFooter("uselessfacts.jsph.pl").setColor(Config.accent)

        val another = Button.of(ButtonStyle.PRIMARY, "fun:fact:refresh", "Another!")

        interaction.hook.sendMessage("").setEmbeds(embed.build()).setActionRow(another).queue()

        Interaction.subscribe(another.id!!) lambda@ {
            btnInter ->

            btnInter.deferEdit().queue()

            if (btnInter.user != interaction.user) {
                btnInter.reply("That button is not yours.").setEphemeral(true).queue()
                return@lambda false
            }

            fact = fetchFact()
            embed.setDescription(fact.text)

            interaction.hook.editOriginal("").setEmbeds(embed.build()).queue()

            return@lambda false
        }
    }
}