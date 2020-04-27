package me.oriharel.machinery.message

open class Text(val text: String?) {

    override fun toString(): String {
        return "Text{" +
                "text='" + text + '\'' +
                '}'
    }

}