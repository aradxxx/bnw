package im.bnw.android.presentation.core.markwon

import im.bnw.android.BuildConfig
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CoreProps
import org.commonmark.node.Link
import java.util.regex.Matcher
import java.util.regex.Pattern

private const val POST_LINK_REGEX = "#\\S+"
private const val USER_NAME_REGEX = "@[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"

object BnwLinkifyPlugin : AbstractMarkwonPlugin() {
    override fun configure(registry: MarkwonPlugin.Registry) {
        registry.require(CorePlugin::class.java) {
            it.addOnTextAddedListener(BnwLinkListener)
        }
    }
}

object BnwLinkListener : CorePlugin.OnTextAddedListener {
    private val pattern = Pattern.compile("(($POST_LINK_REGEX)|($USER_NAME_REGEX))", Pattern.MULTILINE)

    override fun onTextAdded(visitor: MarkwonVisitor, text: String, start: Int) {
        val matcher: Matcher = pattern.matcher(text)

        var value: String
        var url: String?
        var index: Int

        while (matcher.find()) {
            value = matcher.group(1) ?: continue

            // detect which one it is
            url = if ('#' == value[0]) {
                createPostLink(value.substring(1))
            } else {
                createUserLink(value.substring(1))
            }

            // it's important to use `start` value (represents start-index of `text` in the visitor)
            index = start + matcher.start()
            setLink(visitor, url, index, index + value.length)
        }
    }

    private fun createPostLink(id: String): String {
        return "${BuildConfig.LOCAL_LINK}${BuildConfig.POST_PATH_SEGMENT}/$id"
    }

    private fun createUserLink(user: String): String {
        return "${BuildConfig.LOCAL_LINK}${BuildConfig.USER_PATH_SEGMENT}/$user"
    }

    private fun setLink(visitor: MarkwonVisitor, destination: String, start: Int, end: Int) {
        // use default handlers for links
        val configuration = visitor.configuration()
        val renderProps = visitor.renderProps()
        CoreProps.LINK_DESTINATION[renderProps] = destination
        SpannableBuilder.setSpans(
            visitor.builder(),
            configuration.spansFactory().require(Link::class.java).getSpans(configuration, renderProps),
            start,
            end
        )
    }
}
