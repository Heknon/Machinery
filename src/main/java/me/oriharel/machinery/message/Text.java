package me.oriharel.machinery.message;


public class Text {

    protected final String text;

    public Text(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Text{" +
                "text='" + text + '\'' +
                '}';
    }
}
