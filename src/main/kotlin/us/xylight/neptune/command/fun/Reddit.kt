package us.xylight.neptune.command.`fun`

import dev.minn.jda.ktx.interactions.components.button
import dev.minn.jda.ktx.messages.Embed
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import okhttp3.Request
import us.xylight.neptune.LogLevel
import us.xylight.neptune.Logger
import us.xylight.neptune.command.CommandHandler
import us.xylight.neptune.command.Subcommand
import us.xylight.neptune.config.Config
import us.xylight.neptune.database.dataclass.Listing
import us.xylight.neptune.util.ButtonUtil
import us.xylight.neptune.util.EmbedUtil
import kotlin.time.Duration

object Reddit : Subcommand {
    enum class ListingType(val urlName: String) {
        NEW("new"),
        HOT("hot"),
        RISING("rising"),
        TOP("top"),
        CONTROVERSIAL("controversial")
    }

    enum class ListingDuration(val urlName: String) {
        HOUR("hour"),
        TODAY("day"),
        WEEK("week"),
        MONTH("month"),
        YEAR("year"),
        ALL("all")
    }

    override val name = "reddit"
    override val description = "Fetches posts from reddit."
    override val options: List<OptionData> = listOf(
        OptionData(OptionType.STRING, "subreddit", "What subreddit? (Default r/memes)", false),
        OptionData(OptionType.STRING, "type", "What listing type to show?", false).addChoices(
            ListingType.values().map {
                type -> Command.Choice(type.name, type.urlName)
            }
        ),
        OptionData(OptionType.STRING, "duration", "What listing duration to show?", false).addChoices(
            ListingDuration.values().map {
                type -> Command.Choice(type.name, type.urlName)
            }
        )
    )
    private val client = CommandHandler.httpClient


    override suspend fun execute(interaction: SlashCommandInteractionEvent) {
        val type = interaction.getOption("type")?.asString ?: ListingType.HOT.urlName
        val duration = interaction.getOption("duration")?.asString ?: ListingDuration.TODAY.urlName

        val subreddit = interaction.getOption("subreddit")?.asString ?: "memes"
        val url = "https://reddit.com/r/${subreddit}/${type}.json?limit=100&t=${duration}"
        interaction.deferReply().queue()

        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()

        val jsonObj = Json {
            ignoreUnknownKeys = true
        }

        val body = response.body
        val str = response.body?.string()!!
        val listing = runCatching { jsonObj.decodeFromString<Listing>(str) }.getOrElse {
            interaction.hook.sendMessage("").setEmbeds(
                EmbedUtil.simpleEmbed(
                    "Error",
                    "${Config.conf.emoji.error} There was an error fetching posts. It's likely that the subreddit is private, or does not exist.",
                    Config.conf.misc.error
                ).build()
            ).queue()
            return
        }
        val posts = listing.subreddit.posts

        if (posts.isEmpty()) {
            interaction.hook.sendMessageEmbeds(
                Embed {
                    title = "Error"
                    description = "${Config.conf.emoji.error} That subreddit has no posts."
                    color = Config.conf.misc.error
                }
            ).queue()
            return
        }

        var index = 0

        fun skip(i: Int, backwards: Boolean = false): Int {
            var j = i
            var skipped = 0

            while (posts[j].data.stickied || posts[j].data.isNsfw || posts[j].data.pinned || posts[j].data.isVideo) {
                if (backwards) --j else ++j
                if (i > (posts.size - 1)) break
                if (++skipped >= 5) {
                    return -1
                }
            }

            return j
        }

        val minIndex = skip(index)
        index = minIndex

        fun update(index: Int) {
            val embed = EmbedBuilder().setTitle(posts[index].data.title, "https://reddit.com${posts[index].data.url}")
                .setDescription(posts[index].data.text!!).setColor(Config.conf.misc.accent)
                .setFooter("👍 ${posts[index].data.upvotes} 💬 ${posts[index].data.commentCount}")

            if (!(posts[index].data.isVideo)) embed.setImage(posts[index].data.mediaUrl)

            interaction.hook.editOriginal("").setEmbeds(embed.build()).queue()
        }

        fun handleInteraction(buttonInter: ButtonInteractionEvent, backwards: Boolean): Boolean {
            if (backwards) --index else ++index
            if (index > (posts.size - 1) || index < minIndex) {
                if (backwards) ++index else --index
            }

            if (buttonInter.user != interaction.user) {
                buttonInter.reply("That button is not yours.").setEphemeral(true).queue()
                return false
            }

            buttonInter.deferEdit().queue()

            val prevIndex = skip(index, backwards)
            if (prevIndex == -1) {
                interaction.hook.editOriginalEmbeds(
                    Embed {
                        title = "Error"
                        description = "There are too many NSFW posts on that subreddit."
                        color = Config.conf.misc.error
                    }
                ).queue()

                return true
            }

            if (prevIndex > (posts.size - 1) || prevIndex < 0) return true
            index = prevIndex

            update(index)

            return false
        }

        val next = interaction.jda.button(
            ButtonStyle.PRIMARY,
            "Next",
            expiration = Duration.parse("10m"),
            user = interaction.user
        ) { button ->
            handleInteraction(button, false)
        }
        val back = interaction.jda.button(
            ButtonStyle.PRIMARY,
            "Back",
            expiration = Duration.parse("10m"),
            user = interaction.user
        ) { button ->
            handleInteraction(button, true)
        }

        interaction.hook.editOriginal("").setActionRow(back, next).queue()

        if (index == -1) {

            interaction.hook.editOriginalEmbeds(
                Embed {
                    title = "Error"
                    description = "There are too many NSFW posts on that subreddit."
                    color = Config.conf.misc.error
                }
            ).queue()
            return
        }
        update(index)

        body?.close()
    }
}
