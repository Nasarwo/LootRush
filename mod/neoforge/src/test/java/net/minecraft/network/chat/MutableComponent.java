package net.minecraft.network.chat;

import net.minecraft.ChatFormatting;

public class MutableComponent extends Component {
    public MutableComponent(String text, ChatFormatting style) {
        super(text, style);
    }

    @Override
    public MutableComponent withStyle(ChatFormatting style) {
        return new MutableComponent(this.toString(), style);
    }
}
