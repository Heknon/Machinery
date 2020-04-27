package me.oriharel.machinery.message

import me.oriharel.machinery.utilities.Utils


class Placeholder {
    val placeholder: String
    val replacement: String?

    constructor(placeholder: String, replacement: String?) {
        this.placeholder = placeholder
        this.replacement = replacement
    }

    constructor(placeholder: String, replacement: Int?) {
        this.placeholder = placeholder
        this.replacement = Utils.COMMA_NUMBER_FORMAT.format(replacement?.toLong())
    }

    override fun toString(): String {
        return "Placeholder{" +
                "placeholder='" + placeholder + '\'' +
                ", replacement='" + replacement + '\'' +
                '}'
    }
}