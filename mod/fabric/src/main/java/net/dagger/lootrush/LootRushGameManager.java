package net.dagger.lootrush;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class LootRushGameManager {
    private static final int MAX_LIVES = 5;
    private static final int COUNTDOWN_SECONDS = 10;
    private static final int SWAP_SECONDS = 300;
    private static final int SCATTER_MIN = 10000;
    private static final int SCATTER_MAX = 100000;

    private final Set<String> bannedExact = new HashSet<>();
    private final List<String> bannedRegex = new ArrayList<>();
    private final Map<UUID, Role> roles = new HashMap<>();
    private final Map<UUID, Integer> lives = new HashMap<>();
    private final Map<UUID, String> langs = new HashMap<>();
    private final Map<UUID, Integer> wins = new HashMap<>();

    private MinecraftServer server;
    private GameState state = GameState.IDLE;
    private Item targetItem;
    private int countdownLeft = 0;
    private long lastSecondTick = 0L;
    private long gameStartMs = 0L;

    public LootRushGameManager(MinecraftServer server) {
        this.server = server;
        initDefaultBans();
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public void onServerStopped() {
        state = GameState.IDLE;
        targetItem = null;
        lives.clear();
    }

    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        registerRoot(dispatcher, "lootrush");
        registerRoot(dispatcher, "lr");
    }

    private void registerRoot(CommandDispatcher<CommandSourceStack> dispatcher, String root) {
        dispatcher.register(Commands.literal(root)
                .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                .executes(ctx -> usage(ctx.getSource()))
                .then(Commands.literal("start").executes(ctx -> start(ctx.getSource())))
                .then(Commands.literal("stop").executes(ctx -> stop(ctx.getSource())))
                .then(Commands.literal("status").executes(ctx -> status(ctx.getSource())))
                .then(Commands.literal("skip").executes(ctx -> skip(ctx.getSource())))
                .then(Commands.literal("lang")
                        .then(Commands.argument("language", StringArgumentType.word())
                                .executes(ctx -> lang(ctx.getSource(), StringArgumentType.getString(ctx, "language")))))
                .then(Commands.literal("role")
                        .then(Commands.argument("role", StringArgumentType.word())
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(ctx -> role(ctx.getSource(), StringArgumentType.getString(ctx, "role"), EntityArgument.getPlayers(ctx, "targets"))))))
        );
    }

    public void onServerTick() {
        if (server == null) return;
        long now = System.currentTimeMillis();

        if (state == GameState.COUNTDOWN && now - lastSecondTick >= 1000L) {
            lastSecondTick = now;
            if (countdownLeft <= 0) {
                state = GameState.ACTIVE;
                gameStartMs = now;
                broadcast(tr("start_good_luck") + formatItem(targetItem));
            } else {
                broadcast(tr("start_in") + " " + countdownLeft + "...");
                countdownLeft--;
            }
        }

        if (state == GameState.ACTIVE && now - lastSecondTick >= 1000L) {
            lastSecondTick = now;
            checkLastPlayerStanding();
            long elapsedSec = (now - gameStartMs) / 1000L;
            if (elapsedSec > 0 && elapsedSec % SWAP_SECONDS == 0) {
                performSwap();
            }
        }
    }

    public void onPlayerJoin(ServerPlayer player) {
        roles.putIfAbsent(player.getUUID(), Role.PLAYER);
        langs.putIfAbsent(player.getUUID(), "ru");
        send(player, tr(player, "welcome"));
        updateWinsTab(player);
    }

    public void onPlayerQuit(ServerPlayer player) {
        if (state == GameState.ACTIVE || state == GameState.COUNTDOWN) {
            checkLastPlayerStanding();
        }
    }

    public void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        UUID oldId = oldPlayer.getUUID();
        UUID newId = newPlayer.getUUID();
        if (!oldId.equals(newId)) {
            roles.put(newId, roles.getOrDefault(oldId, Role.PLAYER));
            lives.put(newId, lives.getOrDefault(oldId, MAX_LIVES));
            langs.put(newId, langs.getOrDefault(oldId, "ru"));
        }
        if (state == GameState.ACTIVE && roleOf(newPlayer) == Role.PLAYER) {
            int left = lives.merge(newPlayer.getUUID(), -1, Integer::sum);
            if (left <= 0) {
                roles.put(newPlayer.getUUID(), Role.SPECTATOR);
                newPlayer.setGameMode(GameType.SPECTATOR);
                send(newPlayer, tr(newPlayer, "no_lives"));
            } else {
                send(newPlayer, tr(newPlayer, "lives_left") + " " + left + "/" + MAX_LIVES);
            }
            checkLastPlayerStanding();
        }
    }

    public void onPotentialTargetItemInteraction(ServerPlayer player, Item usedItem) {
        if (state != GameState.ACTIVE || targetItem == null || roleOf(player) != Role.PLAYER) return;
        if (Objects.equals(usedItem, targetItem) && hasTargetItem(player)) {
            endWithWinner(player, true);
        }
    }

    public boolean canBreakBlock(ServerPlayer player) {
        return !(state == GameState.COUNTDOWN && roleOf(player) == Role.PLAYER);
    }

    private int usage(CommandSourceStack source) {
        source.sendFailure(Component.literal("/lootrush <start|stop|status|skip|role|lang>"));
        return 0;
    }

    private int start(CommandSourceStack source) {
        if (server == null) return 0;
        if (state != GameState.IDLE) {
            source.sendFailure(Component.literal(tr("already_running")));
            return 0;
        }
        List<ServerPlayer> online = server.getPlayerList().getPlayers();
        List<ServerPlayer> participants = online.stream()
                .filter(p -> roleOf(p) == Role.PLAYER)
                .collect(Collectors.toList());
        if (participants.isEmpty()) {
            source.sendFailure(Component.literal(tr("no_players")));
            return 0;
        }

        targetItem = pickRandomItem();
        if (targetItem == null) {
            source.sendFailure(Component.literal("No valid target item available"));
            return 0;
        }

        state = GameState.COUNTDOWN;
        countdownLeft = COUNTDOWN_SECONDS;
        lastSecondTick = System.currentTimeMillis();
        lives.clear();

        try {
            prepareWorldStateForLoading();
        } catch (Exception e) {
            LootRush.LOGGER.atLevel(org.slf4j.event.Level.WARN).log("Failed to apply loading world state, continuing with defaults", e);
        }

        for (ServerPlayer player : online) {
            if (roleOf(player) == Role.SPECTATOR) {
                player.setGameMode(GameType.SPECTATOR);
            } else {
                player.setGameMode(GameType.SURVIVAL);
                lives.put(player.getUUID(), MAX_LIVES);
            }
            clearInventory(player);
            scatterTeleport(player);
        }

        broadcast(tr("target_item") + " " + formatItem(targetItem));
        playSoundAll(SoundEvents.ENDER_DRAGON_GROWL, 1.0f, 1.0f);
        return 1;
    }

    private int stop(CommandSourceStack source) {
        if (state == GameState.IDLE) {
            source.sendFailure(Component.literal(tr("already_stopped")));
            return 0;
        }
        stopInternal(true);
        broadcast(tr("stopped"));
        return 1;
    }

    private int status(CommandSourceStack source) {
        if (state == GameState.IDLE) {
            source.sendSuccess(() -> Component.literal(tr("status_idle")), false);
        } else if (state == GameState.COUNTDOWN) {
            source.sendSuccess(() -> Component.literal(tr("status_countdown") + " " + formatItem(targetItem)), false);
        } else {
            source.sendSuccess(() -> Component.literal(tr("status_active") + " " + formatItem(targetItem)), false);
        }
        return 1;
    }

    private int skip(CommandSourceStack source) {
        if (state == GameState.IDLE) {
            source.sendFailure(Component.literal(tr("game_not_active")));
            return 0;
        }
        targetItem = pickRandomItem();
        if (targetItem == null) {
            source.sendFailure(Component.literal("Failed to pick a new item"));
            return 0;
        }
        broadcast(tr("item_skipped") + " " + formatItem(targetItem));
        return 1;
    }

    private int lang(CommandSourceStack source, String langCode) {
        String lc = langCode.toLowerCase(Locale.ROOT);
        if (!lc.equals("ru") && !lc.equals("en") && !lc.equals("ua")) {
            source.sendFailure(Component.literal(tr("lang_unknown")));
            return 0;
        }
        if (source.getEntity() instanceof ServerPlayer player) {
            langs.put(player.getUUID(), lc);
            source.sendSuccess(() -> Component.literal(tr(player, "lang_set") + " " + lc), false);
            updateWinsTab(player);
            return 1;
        }
        source.sendFailure(Component.literal("Only players can set personal language"));
        return 0;
    }

    private int role(CommandSourceStack source, String roleName, Collection<ServerPlayer> targets) {
        Role newRole;
        if ("player".equalsIgnoreCase(roleName)) {
            newRole = Role.PLAYER;
        } else if ("spectator".equalsIgnoreCase(roleName)) {
            newRole = Role.SPECTATOR;
        } else {
            source.sendFailure(Component.literal(tr("role_unknown")));
            return 0;
        }
        for (ServerPlayer player : targets) {
            roles.put(player.getUUID(), newRole);
            player.setGameMode(newRole == Role.SPECTATOR ? GameType.SPECTATOR : GameType.SURVIVAL);
            send(player, tr(player, "role_set") + " " + roleName.toLowerCase(Locale.ROOT));
        }
        source.sendSuccess(() -> Component.literal("Updated " + targets.size() + " player(s) role to " + roleName), false);
        return targets.size();
    }

    private void stopInternal(boolean clearInventories) {
        state = GameState.IDLE;
        targetItem = null;
        countdownLeft = 0;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            roles.put(player.getUUID(), Role.PLAYER);
            player.setGameMode(GameType.SURVIVAL);
            if (clearInventories) clearInventory(player);
            ServerLevel overworld = server.overworld();
            BlockPos spawn = overworld.getSharedSpawnPos();
            player.teleportTo(overworld, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, player.getYRot(), player.getXRot());
        }
        lives.clear();
    }

    private void prepareWorldStateForLoading() {
        for (ServerLevel level : server.getAllLevels()) {
            level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DAYLIGHT).set(false, server);
            level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_WEATHER_CYCLE).set(false, server);
            try {
                level.setDayTime(0L);
            } catch (Exception ignored) {
            }
        }
    }

    private void checkLastPlayerStanding() {
        List<ServerPlayer> alive = server.getPlayerList().getPlayers().stream()
                .filter(p -> roleOf(p) == Role.PLAYER)
                .filter(p -> lives.getOrDefault(p.getUUID(), 0) > 0)
                .collect(Collectors.toList());
        if (state == GameState.ACTIVE && alive.size() == 1) {
            endWithWinner(alive.get(0), false);
        }
    }

    private void endWithWinner(ServerPlayer winner, boolean withItem) {
        wins.merge(winner.getUUID(), 1, Integer::sum);
        String msg = withItem
                ? tr("winner_item") + " " + winner.getName().getString() + " (" + formatItem(targetItem) + ")"
                : tr("winner_last") + " " + winner.getName().getString();
        broadcast(msg);
        updateWinsTabAll();
        playSoundAll(SoundEvents.WITHER_DEATH, 1.0f, 1.0f);
        stopInternal(false);
    }

    private void performSwap() {
        List<ServerPlayer> active = server.getPlayerList().getPlayers().stream()
                .filter(p -> roleOf(p) == Role.PLAYER)
                .filter(p -> lives.getOrDefault(p.getUUID(), 0) > 0)
                .collect(Collectors.toList());
        if (active.size() < 2) return;
        List<BlockPos> positions = active.stream().map(ServerPlayer::blockPosition).collect(Collectors.toList());
        for (int i = 0; i < active.size(); i++) {
            ServerPlayer player = active.get(i);
            BlockPos dest = positions.get((i + 1) % positions.size());
            player.teleportTo((ServerLevel) player.level(), dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5, player.getYRot(), player.getXRot());
        }
        broadcast(tr("swap_done"));
    }

    private void scatterTeleport(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        int range = Math.max(1, SCATTER_MAX - SCATTER_MIN);
        int x = SCATTER_MIN + player.getRandom().nextInt(range);
        int z = SCATTER_MIN + player.getRandom().nextInt(range);
        if (player.getRandom().nextBoolean()) x = -x;
        if (player.getRandom().nextBoolean()) z = -z;
        int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        player.teleportTo(level, x + 0.5, y + 1, z + 0.5, player.getYRot(), player.getXRot());
    }

    private Item pickRandomItem() {
        List<Item> pool = BuiltInRegistries.ITEM.stream()
                .filter(item -> !item.equals(net.minecraft.world.item.Items.AIR))
                .filter(this::isAllowed)
                .collect(Collectors.toCollection(ArrayList::new));
        if (pool.isEmpty()) return null;
        pool.sort(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()));
        return pool.get((int) (System.nanoTime() % pool.size()));
    }

    private boolean isAllowed(Item item) {
        String key = BuiltInRegistries.ITEM.getKey(item).toString().toLowerCase(Locale.ROOT);
        if (bannedExact.contains(key)) return false;
        for (String regex : bannedRegex) {
            if (key.matches(regex)) return false;
        }
        return true;
    }

    private boolean hasTargetItem(ServerPlayer player) {
        if (targetItem == null) return false;
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && stack.is(targetItem)) return true;
        }
        return false;
    }

    private Role roleOf(ServerPlayer player) {
        return roles.getOrDefault(player.getUUID(), Role.PLAYER);
    }

    private void clearInventory(ServerPlayer player) {
        player.getInventory().clearContent();
    }

    private void playSoundAll(net.minecraft.sounds.SoundEvent event, float volume, float pitch) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.playNotifySound(event, SoundSource.MASTER, volume, pitch);
        }
    }

    private void send(ServerPlayer player, String msg) {
        player.sendSystemMessage(Component.literal(msg));
    }

    private void broadcast(String msg) {
        Component message = Component.literal(msg);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(message);
        }
        server.sendSystemMessage(message);
    }

    private String formatItem(Item item) {
        if (item == null) return "?";
        return Component.translatable(item.getDescriptionId()).getString();
    }

    private void initDefaultBans() {
        bannedExact.add("minecraft:bedrock");
        bannedExact.add("minecraft:barrier");
        bannedExact.add("minecraft:command_block");
        bannedExact.add("minecraft:repeating_command_block");
        bannedExact.add("minecraft:chain_command_block");
        bannedRegex.add(".*_spawn_egg");
    }

    private String tr(String key) {
        return switch (key) {
            case "already_running" -> "Игра уже запущена";
            case "no_players" -> "Нет игроков с ролью player";
            case "start_in" -> "Старт через";
            case "start_good_luck" -> "Удачи! Ваша цель:";
            case "target_item" -> "Нужно добыть:";
            case "already_stopped" -> "Игра уже остановлена";
            case "stopped" -> "Игра остановлена администратором";
            case "status_idle" -> "Игра не запущена";
            case "status_countdown" -> "Идет отсчет. Цель:";
            case "status_active" -> "Игра активна. Цель:";
            case "game_not_active" -> "Игра неактивна";
            case "item_skipped" -> "Предмет пропущен. Новая цель:";
            case "lang_unknown" -> "Неизвестный язык";
            case "role_unknown" -> "Неизвестная роль";
            case "winner_item" -> "Победитель раунда:";
            case "winner_last" -> "Последний выживший:";
            case "swap_done" -> "Свап позиций выполнен";
            default -> key;
        };
    }

    private String tr(ServerPlayer player, String key) {
        String lang = langs.getOrDefault(player.getUUID(), "ru");
        if ("en".equals(lang)) {
            return switch (key) {
                case "welcome" -> "LootRush loaded.";
                case "no_lives" -> "No lives left. You are now spectator.";
                case "lives_left" -> "Lives left:";
                case "lang_set" -> "Language set to";
                case "role_set" -> "Your role is now";
                default -> tr(key);
            };
        }
        if ("ua".equals(lang)) {
            return switch (key) {
                case "welcome" -> "LootRush завантажено.";
                case "no_lives" -> "Життя закінчилися. Ви тепер спостерігач.";
                case "lives_left" -> "Залишилось життів:";
                case "lang_set" -> "Мову змінено на";
                case "role_set" -> "Ваша роль тепер";
                default -> tr(key);
            };
        }
        return switch (key) {
            case "welcome" -> "LootRush загружен.";
            case "no_lives" -> "Жизни закончились. Вы теперь наблюдатель.";
            case "lives_left" -> "Осталось жизней:";
            case "lang_set" -> "Язык изменен на";
            case "role_set" -> "Ваша роль теперь";
            default -> tr(key);
        };
    }

    private void updateWinsTab(ServerPlayer player) {
        List<Map.Entry<UUID, Integer>> top = wins.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(5)
                .toList();
        Component header = Component.literal("Loot Rush").withStyle(ChatFormatting.GOLD);
        if (top.isEmpty()) {
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundTabListPacket(
                    header,
                    Component.literal("Побед пока нет").withStyle(ChatFormatting.GRAY)
            ));
            return;
        }
        Component footer = Component.literal("=== Победители ===").withStyle(ChatFormatting.YELLOW);
        int pos = 1;
        for (Map.Entry<UUID, Integer> e : top) {
            String name = e.getKey().toString().substring(0, 8);
            ServerPlayer online = server.getPlayerList().getPlayer(e.getKey());
            if (online != null) name = online.getName().getString();
            footer = footer.append(Component.literal("\n" + pos + ". " + name + " - " + e.getValue()).withStyle(ChatFormatting.WHITE));
            pos++;
        }
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundTabListPacket(header, footer));
    }

    private void updateWinsTabAll() {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            updateWinsTab(player);
        }
    }

    private enum GameState {
        IDLE,
        COUNTDOWN,
        ACTIVE
    }

    private enum Role {
        PLAYER,
        SPECTATOR
    }
}
