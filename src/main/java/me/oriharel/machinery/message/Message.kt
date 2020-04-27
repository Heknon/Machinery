package me.oriharel.machinery.message

import me.oriharel.machinery.Machinery
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

/**
 * Used to abstract logic behind sending and formatting messages with placeholders or color formatting
 */
class Message : Text {
    private var recipients: List<Player?>
    private var placeholders: MutableList<Placeholder?>?

    constructor(text: String, recipients: List<Player?>, placeholders: MutableList<Placeholder?>?) : super(text) {
        this.recipients = recipients
        this.placeholders = placeholders
    }

    constructor(text: String, recipient: Player?, vararg placeholder: Placeholder?) : super(text) {
        recipients = listOf(recipient)
        placeholders = Arrays.asList(*placeholder)
    }

    constructor(text: String, vararg placeholder: Placeholder?) : super(text) {
        recipients = Collections.EMPTY_LIST
        placeholders = Arrays.asList(*placeholder)
    }

    constructor(configName: String, configRoute: String, recipient: Player?, vararg placeholders: Placeholder?) : super(handleConfigCache(configName, configRoute)) {
        recipients = listOf(recipient)
        this.placeholders = Arrays.asList(*placeholders)
    }

    constructor(configName: String, configRoute: String, recipient: Player?, placeholders: MutableList<Placeholder?>?) : super(handleConfigCache(configName, configRoute)) {
        recipients = listOf(recipient)
        this.placeholders = placeholders
    }

    constructor(text: String, recipients: List<Player?>) : super(text) {
        this.recipients = recipients
        placeholders = ArrayList()
    }

    /**
     * Replace a placeholder that is already in the list of this messages placeholders. Used mostly for replacing already committed values.
     * Will still add the replacement placeholder if if the placeholder was not found
     * @param placeholder the placeholder string to find
     * @param replacement replacement
     * @return this
     */
    fun replacePlaceholder(placeholder: String?, replacement: Placeholder?): Message {
        placeholders!!.removeIf { p: Placeholder? -> p.getPlaceholder().equals(placeholder, ignoreCase = true) }
        placeholders!!.add(replacement)
        return this
    }

    fun send(): Message {
        val textWithAppliedPlaceholders = ChatColor.translateAlternateColorCodes('&', applyPlaceholders(text).text)
        if (textWithAppliedPlaceholders.equals("disabled", ignoreCase = true)) return this
        for (recipient in recipients) {
            recipient!!.sendMessage(textWithAppliedPlaceholders)
        }
        return this
    }

    val appliedText: String
        get() = ChatColor.translateAlternateColorCodes('&', applyPlaceholders(text).text)

    private fun applyPlaceholders(text: String): Text {
        var txt = text
        for (placeholder in placeholders!!) {
            txt = applyPlaceholder(txt, placeholder)
        }
        return Text(txt)
    }

    private fun applyPlaceholder(text: String, placeholder: Placeholder?): String {
        return text.replace(placeholder.getPlaceholder(), placeholder.getReplacement())
    }

    companion object {
        private val CONFIG_MESSAGE_CACHE: MutableMap<String, String?> = HashMap()
        private fun handleConfigCache(configName: String, routeName: String): String {
            val cacheKey = "$configName|$routeName"
            val cacheValue = CONFIG_MESSAGE_CACHE.getOrDefault(cacheKey, null)
            if (cacheValue == null) { // not present in cache
                val value: String = Machinery.Companion.getInstance().getFileManager().getConfig(configName).get().getString(routeName)
                CONFIG_MESSAGE_CACHE[cacheKey] = value
                return value
            }
            return cacheValue
        }
    }
}