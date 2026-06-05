package net.dagger.lootrush;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LootRush implements ModInitializer {
	public static final String MOD_ID = "lootrush";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private LootRushGameManager gameManager;
	private MinecraftServer server;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			this.server = server;
			initGameManager();
			gameManager.setServer(server);
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			if (gameManager != null) {
				gameManager.onServerStopped();
			}
			this.server = null;
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			initGameManager();
			gameManager.registerCommands(dispatcher);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (gameManager != null) {
				gameManager.onServerTick();
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (gameManager != null) {
				gameManager.onPlayerJoin(handler.getPlayer());
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (gameManager != null) {
				gameManager.onPlayerQuit(handler.getPlayer());
			}
		});

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			if (gameManager != null) {
				gameManager.onPlayerRespawn(oldPlayer, newPlayer, alive);
			}
		});

		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (!(player instanceof ServerPlayer serverPlayer) || gameManager == null) {
				return InteractionResult.PASS;
			}
			Item item = player.getItemInHand(hand).getItem();
			gameManager.onPotentialTargetItemInteraction(serverPlayer, item);
			return InteractionResult.PASS;
		});

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (!(player instanceof ServerPlayer serverPlayer) || gameManager == null) {
				return true;
			}
			return gameManager.canBreakBlock(serverPlayer);
		});
	}

	private void initGameManager() {
		if (gameManager != null) {
			return;
		}
		gameManager = new LootRushGameManager(server);
	}
}