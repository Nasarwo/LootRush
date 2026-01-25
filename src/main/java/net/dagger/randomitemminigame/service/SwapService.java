package net.dagger.randomitemminigame.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Supplier;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SwapService {
	private final int swapIntervalTicks;

	private final JavaPlugin plugin;
	private final LanguageService languageService;
	private final Supplier<Boolean> canSwapSupplier;
	private final Supplier<List<Player>> participantsSupplier;
	private final Consumer<Component> participantBroadcast;
	private final DoubleConsumer progressUpdater;
	private final Random random = new Random();
	private BukkitRunnable task;

	public SwapService(JavaPlugin plugin,
			LanguageService languageService,
			Supplier<Boolean> canSwapSupplier,
			Supplier<List<Player>> participantsSupplier,
			Consumer<Component> participantBroadcast,
			DoubleConsumer progressUpdater,
			int swapIntervalSeconds) {
		this.plugin = plugin;
		this.languageService = languageService;
		this.canSwapSupplier = canSwapSupplier;
		this.participantsSupplier = participantsSupplier;
		this.participantBroadcast = participantBroadcast;
		this.progressUpdater = progressUpdater;

		this.swapIntervalTicks = swapIntervalSeconds * 20;
	}

	public void start(long gameStartTime) {
		stop();
		progressUpdater.accept(1.0);
		task = new BukkitRunnable() {
			private long nextSwapTime = gameStartTime + (swapIntervalTicks * 50);

			@Override
			public void run() {
				if (!canSwapSupplier.get()) {
					return;
				}

				List<Player> participants = participantsSupplier.get();
				if (participants.size() < 2) {
					long currentTime = System.currentTimeMillis();
					long elapsed = currentTime - gameStartTime;
					long intervalMs = swapIntervalTicks * 50L;
					nextSwapTime = gameStartTime + ((elapsed / intervalMs) + 1) * intervalMs;
					progressUpdater.accept(1.0);
					return;
				}

				long currentTime = System.currentTimeMillis();
				long msUntilSwap = nextSwapTime - currentTime;

				if (msUntilSwap <= 0) {
					performSwap(participants);
					nextSwapTime += swapIntervalTicks * 50L;
					msUntilSwap = nextSwapTime - currentTime;
				}
				double progress = Math.max(0.0, Math.min(1.0, msUntilSwap / (swapIntervalTicks * 50.0)));
				progressUpdater.accept(progress);
			}
		};
		task.runTaskTimer(plugin, 0L, 20L);
	}

	public void stop() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	private void performSwap(List<Player> participants) {
		LanguageService.Language defaultLang = languageService.getDefaultLanguage();
		participantBroadcast.accept(Messages.get(defaultLang, Messages.MessageKey.PLAYERS_SWAPPING));

		Map<Player, Location> playerLocations = new HashMap<>();
		Map<Player, Location> playerRespawnLocations = new HashMap<>();
		for (Player player : participants) {
			playerLocations.put(player, player.getLocation().clone());
			Location respawnLoc = player.getRespawnLocation();
			if (respawnLoc == null) {
				respawnLoc = player.getLocation().clone();
			}
			playerRespawnLocations.put(player, respawnLoc);
		}

		Collections.shuffle(participants, random);

		for (int i = 0; i < participants.size(); i++) {
			Player currentPlayer = participants.get(i);
			Player targetPlayer = participants.get((i + 1) % participants.size());

			Location targetLocation = playerLocations.get(targetPlayer);
			Location targetRespawnLocation = playerRespawnLocations.get(targetPlayer);

			currentPlayer.teleport(targetLocation);
			currentPlayer.setRespawnLocation(targetRespawnLocation, true);
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				if (currentPlayer.isOnline()) {
					currentPlayer.setInvulnerable(true);
					currentPlayer.setNoDamageTicks(Integer.MAX_VALUE);
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						if (currentPlayer.isOnline()) {
							currentPlayer.setInvulnerable(false);
							currentPlayer.setNoDamageTicks(20);
						}
					}, 500L);
				}
			}, 1L);
			currentPlayer.playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1.0f, 1.0f);
		}
	}
}
