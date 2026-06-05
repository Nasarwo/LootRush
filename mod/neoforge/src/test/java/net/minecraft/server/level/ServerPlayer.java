package net.minecraft.server.level;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

import java.util.UUID;

public class ServerPlayer {
    private final UUID uuid;
    private GameType gameType = GameType.SURVIVAL;
    private Component lastMessage;

    public ServerPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean setGameMode(GameType type) {
        this.gameType = type;
        return true;
    }

    public GameType getGameMode() {
        return gameType;
    }

    public void sendSystemMessage(Component component) {
        this.lastMessage = component;
    }

    public Component getLastMessage() {
        return lastMessage;
    }
}
