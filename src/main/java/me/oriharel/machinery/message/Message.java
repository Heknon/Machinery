package me.oriharel.machinery.message;

import me.oriharel.machinery.Machinery;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class Message extends Text {

    private static final Map<String, String> CONFIG_MESSAGE_CACHE = new HashMap<>();

    private List<Player> recipients;
    private List<Placeholder> placeholders;

    public Message(String text, List<Player> recipients, List<Placeholder> placeholders) {
        super(text);
        this.recipients = recipients;
        this.placeholders = placeholders;
    }

    public Message(String text, Player recipient, Placeholder... placeholder) {
        super(text);
        this.recipients = Collections.singletonList(recipient);
        this.placeholders = Arrays.asList(placeholder);
    }

    public Message(String configName, String configRoute, Player recipient, Placeholder... placeholders) {
        super(handleConfigCache(configName, configRoute));
        this.recipients = Collections.singletonList(recipient);
        this.placeholders = Arrays.asList(placeholders);
    }

    public Message(String configName, String configRoute, Player recipient, List<Placeholder> placeholders) {
        super(handleConfigCache(configName, configRoute));
        this.recipients = Collections.singletonList(recipient);
        this.placeholders = placeholders;
    }

    public Message(String text, List<Player> recipients) {
        super(text);
        this.recipients = recipients;
        this.placeholders = new ArrayList<>();
    }

    /**
     * Replace a placeholder that is already in the list of this messages placeholders. Used mostly for replacing already committed values.
     * Will still add the replacement placeholder if if the placeholder was not found
     * @param placeholder the placeholder string to find
     * @param replacement replacement
     * @return this
     */
    public Message replacePlaceholder(String placeholder, Placeholder replacement) {
        placeholders.removeIf(p -> p.getPlaceholder().equalsIgnoreCase(placeholder));
        placeholders.add(replacement);
        return this;
    }

    private static String handleConfigCache(String configName, String routeName) {
        String cacheKey = configName + "|" + routeName;
        String cacheValue = CONFIG_MESSAGE_CACHE.getOrDefault(cacheKey, null);

        if (cacheValue == null) { // not present in cache
            String value = Machinery.getInstance().getFileManager().getConfig(configName).get().getString(routeName);
            CONFIG_MESSAGE_CACHE.put(cacheKey, value);
            return value;
        }

        return cacheValue;
    }

    public Message send() {
        String textWithAppliedPlaceholders = ChatColor.translateAlternateColorCodes('&', applyPlaceholders(text).text);
        if (textWithAppliedPlaceholders.equalsIgnoreCase("disabled")) return this;
        for (Player recipient : recipients) {
            recipient.sendMessage(textWithAppliedPlaceholders);
        }
        return this;
    }

    private Text applyPlaceholders(String text) {
        String txt = text;
        for (Placeholder placeholder : placeholders) {
            txt = applyPlaceholder(txt, placeholder);
        }
        return new Text(txt);
    }

    private String applyPlaceholder(String text, Placeholder placeholder) {
        return text.replace(placeholder.getPlaceholder(), placeholder.getReplacement());
    }
}
