package me.oriharel.machinery.message;

import me.oriharel.machinery.utilities.Utils;

public class Placeholder {

    private final String placeholder;
    private final String replacement;

    public Placeholder(String placeholder, String replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }

    public Placeholder(String placeholder, int replacement) {
        this.placeholder = placeholder;
        this.replacement = Utils.COMMA_NUMBER_FORMAT.format(replacement);
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getReplacement() {
        return replacement;
    }

    @Override
    public String toString() {
        return "Placeholder{" +
                "placeholder='" + placeholder + '\'' +
                ", replacement='" + replacement + '\'' +
                '}';
    }
}
