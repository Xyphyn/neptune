package us.xylight.neptune.command.`fun`

import kotlinx.serialization.*;
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import okhttp3.Request
import us.xylight.neptune.command.ComponentSubcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.dataclass.Listing
import us.xylight.neptune.event.Interaction
import us.xylight.neptune.handler.CommandHandler
import us.xylight.neptune.util.EmbedUtil

class Reddit : ComponentSubcommand {

    override val name = "reddit"
    override val description = "Fetches posts from reddit."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "subreddit", "What subreddit? (Default r/memes)", false)
    )
    private val client = CommandHandler.httpClient


    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val subreddit = interaction.getOption("subreddit")?.asString ?: "memes"
        val url = "https://reddit.com/r/${subreddit}.json?limit=100"

        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()
        interaction.deferReply().queue()

        val jsonObj = Json {
            ignoreUnknownKeys = true
        }

        val body = response.body
        val listing = runCatching { jsonObj.decodeFromString<Listing>(response.body?.string()!!) }.getOrElse {
            interaction.hook.sendMessage("").setEmbeds(
                EmbedUtil.simpleEmbed(
                    "Error",
                    "${Config.errorIcon} There was an error fetching posts. It's likely that the subreddit is private, or does not exist.",
                    0xff1f1f
                ).build()
            ).queue()
            return
        }
        val posts = listing.subreddit.posts

        if (posts.isEmpty()) {
            interaction.hook.sendMessage("").setEmbeds(
                EmbedUtil.simpleEmbed("Error", "${Config.errorIcon} That subreddit has no posts.", 0xff1f1f).build()
            ).queue()
            return
        }

        val next = Button.of(ButtonStyle.PRIMARY, "fun:reddit:next:${interaction.user.id}", "Next")
        val back = Button.of(ButtonStyle.PRIMARY, "fun:reddit:back:${interaction.user.id}", "Back")

        fun update(index: Int) {
            val embed = EmbedBuilder().setTitle(posts[index].data.title, "https://reddit.com${posts[index].data.url}")
                .setDescription(posts[index].data.text!!).setColor(Config.accent)
                .setFooter("👍 ${posts[index].data.upvotes} 💬 ${posts[index].data.commentCount}")

            if (!(posts[index].data.isVideo)) embed.setImage(posts[index].data.mediaUrl)

            interaction.hook.editOriginal("").setEmbeds(embed.build()).setActionRow(back, next).queue()
        }

        var index = 0

        fun skip(i: Int, backwards: Boolean = false): Int {
            var j = i

            while (posts[j].data.stickied || posts[j].data.isNsfw || posts[j].data.pinned || posts[j].data.isVideo) {
                if (backwards) -- j else ++ j
                if (i > (posts.size - 1)) break
            }

            return j
        }

        val minIndex = skip(index)
        index = minIndex

        fun handleInteraction(buttonInter: ButtonInteractionEvent, backwards: Boolean): Boolean {
            if (backwards) -- index else ++ index
            if (index > (posts.size - 1) || index < minIndex) {
                if (backwards) ++ index else -- index
            }

            if (buttonInter.user != interaction.user) {
                buttonInter.reply("That button is not yours.").setEphemeral(true).queue()
                return false
            }

            buttonInter.deferEdit().queue()

            val prevIndex = skip(index, backwards)

            if (prevIndex > (posts.size - 1) || prevIndex < 0) return true
            index = prevIndex

            update(index)

            return false
        }

        Interaction.subscribe(next.id!!) lambda@{ buttonInter ->
            if (buttonInter.button.id!!.split(":")[2] != "next") return@lambda false

            return@lambda handleInteraction(buttonInter, false)
        }

        Interaction.subscribe(back.id!!) lambda@ { buttonInter ->
            if (buttonInter.button.id!!.split(":")[2] != "back") return@lambda false

            return@lambda handleInteraction(buttonInter, true)
        }

        update(index)

        body?.close()
    }
}