package us.xylight.neptune.command.`fun`

import dev.minn.jda.ktx.interactions.components.button
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
import us.xylight.neptune.command.CommandHandler
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.event.Interaction
import kotlin.time.Duration

object Fact : Subcommand {
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
        val text = response.body?.string()?.replace('`', '\'')

        val fact = json.decodeFromString<Fact>(text!!)

        response.body?.close()
        response.close()

        return fact
    }

    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        interaction.deferReply().queue()

        val embed: EmbedBuilder = EmbedBuilder().setTitle("Random Fact").setDescription(fetchFact().text).setFooter("uselessfacts.jsph.pl").setColor(Config.conf.misc.accent)


        val btn = interaction.jda.button(ButtonStyle.PRIMARY, "Another!", expiration = Duration.parse("10m"), user = interaction.user) {
                button ->

            button.deferEdit().queue()

            val fact = fetchFact()
            embed.setDescription(fact.text)

            interaction.hook.editOriginal("").setEmbeds(embed.build()).queue()
        }

        interaction.hook.sendMessage("").setEmbeds(embed.build()).setActionRow(btn).queue()
    }
}