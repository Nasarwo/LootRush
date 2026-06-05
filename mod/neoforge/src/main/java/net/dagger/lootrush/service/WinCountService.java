package net.dagger.lootrush.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WinCountService {
    private static final int TAB_LEADERBOARD_TOP = 5;
    private static final String WINS_FILE = "wins.json";
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, Integer>>() {}.getType();

    private final LanguageService languageService;
    private MinecraftServer server;
    private final Map<UUID, Integer> winsByUuid = new ConcurrentHashMap<>();

    public WinCountService(LanguageService languageService) {
        this.languageService = languageService;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        if (server != null) {
            load();
        }
    }

    private Path getWinsPath() {
        if (server == null) return null;
        return server.getWorldPath(LevelResource.ROOT).resolve("lootrush").resolve(WINS_FILE);
    }

    public void load() {
        winsByUuid.clear();
        Path path = getWinsPath();
        if (path == null || !Files.exists(path)) return;
        try {
            String json = Files.readString(path);
            Map<String, Integer> raw = GSON.fromJson(json, MAP_TYPE);
            if (raw != null) {
                for (Map.Entry<String, Integer> e : raw.entrySet()) {
                    try {
                        UUID uuid = UUID.fromString(e.getKey());
                        int wins = e.getValue() != null ? e.getValue() : 0;
                        if (wins > 0) winsByUuid.put(uuid, wins);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        } catch (IOException ignored) {}
    }

    public void save() {
        Path path = getWinsPath();
        if (path == null) return;
        try {
            Files.createDirectories(path.getParent());
            Map<String, Integer> raw = new java.util.HashMap<>();
            for (Map.Entry<UUID, Integer> e : winsByUuid.entrySet()) {
                raw.put(e.getKey().toString(), e.getValue());
            }
            Files.writeString(path, GSON.toJson(raw));
        } catch (IOException ignored) {}
    }

    public int getWins(UUID playerId) {
        return winsByUuid.getOrDefault(playerId, 0);
    }

    public int getWins(ServerPlayer player) {
        return getWins(player.getUUID());
    }

    public void addWin(ServerPlayer winner) {
        UUID id = winner.getUUID();
        winsByUuid.merge(id, 1, (a, b) -> a + b);
        save();
    }

    public List<Entry> getTop(int limit) {
        return winsByUuid.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new Entry(e.getKey(), e.getValue()))
                .toList();
    }

    public void updateTabListForAll() {
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            LanguageService.Language lang = languageService.getLanguage(player);
            Component header = buildTabHeader();
            Component footer = buildTabFooter(lang);
            player.connection.send(new ClientboundTabListPacket(header, footer));
        }
    }

    private static Component buildTabHeader() {
        return Component.literal("Loot Rush").withStyle(ChatFormatting.GOLD);
    }

    private Component buildTabFooter(LanguageService.Language lang) {
        List<Entry> top = getTop(TAB_LEADERBOARD_TOP);
        if (top.isEmpty()) {
            return Messages.get(lang, Messages.MessageKey.LEADERBOARD_EMPTY);
        }
        Component title = Messages.get(lang, Messages.MessageKey.LEADERBOARD_TITLE);
        List<Component> lines = new ArrayList<>();
        lines.add(title);
        int place = 1;
        for (Entry e : top) {
            String name = getPlayerName(e.uuid());
            Component line = Messages.get(lang, Messages.MessageKey.LEADERBOARD_LINE, place, name, e.wins());
            lines.add(line);
            place++;
        }
        return joinNewlines(lines);
    }

    private String getPlayerName(UUID uuid) {
        if (server != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) return player.getName().getString();
        }
        return uuid.toString().substring(0, 8);
    }

    private static Component joinNewlines(List<Component> components) {
        if (components.isEmpty()) return Component.empty();
        MutableComponent result = Component.empty();
        for (int i = 0; i < components.size(); i++) {
            if (i > 0) result.append(Component.literal("\n"));
            result.append(components.get(i));
        }
        return result;
    }

    public record Entry(UUID uuid, int wins) {}
}
