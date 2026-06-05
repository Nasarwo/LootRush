package net.minecraft.network.chat;

import net.minecraft.ChatFormatting;

public class Component {
    protected final String text;
    protected final ChatFormatting style;

    protected Component(String text, ChatFormatting style) {
        this.text = text;
        this.style = style;
    }

    public static MutableComponent literal(String text) {
        return new MutableComponent(text, null);
    }

    public MutableComponent withStyle(ChatFormatting style) {
        return new MutableComponent(this.text, style);
    }

    @Override
    public String toString() {
        return text;
    }
}
