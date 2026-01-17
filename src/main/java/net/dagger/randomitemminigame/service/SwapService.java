package net.dagger.randomitemminigame.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
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
	private final int firstSwapDelayTicks;
	private static final int SWAP_COUNTDOWN_SECONDS = 10;

	private final JavaPlugin plugin;
	private final LanguageService languageService;
	private final Supplier<Boolean> canSwapSupplier;
	private final Supplier<List<Player>> participantsSupplier;
	private final Consumer<Component> participantBroadcast;
	private final Random random = new Random();
	private BukkitRunnable task;

	public SwapService(JavaPlugin plugin,
			LanguageService languageService,
			Supplier<Boolean> canSwapSupplier,
			Supplier<List<Player>> participantsSupplier,
			Consumer<Component> participantBroadcast,
			int swapIntervalSeconds) {
		this.plugin = plugin;
		this.languageService = languageService;
		this.canSwapSupplier = canSwapSupplier;
		this.participantsSupplier = participantsSupplier;
		this.participantBroadcast = participantBroadcast;

		this.swapIntervalTicks = swapIntervalSeconds * 20;
		// Initial delay is interval minus countdown duration
		this.firstSwapDelayTicks = (swapIntervalSeconds - SWAP_COUNTDOWN_SECONDS) * 20;
	}

	public void start() {
		stop();
		task = new BukkitRunnable() {
			private int ticksUntilSwap = firstSwapDelayTicks;
			private int countdown = -1;

			@Override
			public void run() {
				if (!canSwapSupplier.get()) {
					return;
				}

				List<Player> participants = participantsSupplier.get();
				if (participants.size() < 2) {
					// Reset timer if not enough players, but keep counting if game is active
					ticksUntilSwap = firstSwapDelayTicks;
					countdown = -1;
					return;
				}

				if (countdown >= 0) {
					LanguageService.Language defaultLang = languageService.getDefaultLanguage();
					if (countdown == SWAP_COUNTDOWN_SECONDS) {
						participantBroadcast.accept(Messages.get(defaultLang, Messages.MessageKey.SWAP_IN_SECONDS, countdown));
					} else if (countdown > 0) {
						participantBroadcast.accept(Messages.get(defaultLang, Messages.MessageKey.SWAP_IN_SECONDS_SHORT, countdown));
					} else {
						performSwap(participants);
						countdown = -1;
						// Next swap in exactly interval minus countdown duration
						ticksUntilSwap = swapIntervalTicks - (SWAP_COUNTDOWN_SECONDS * 20);
						return;
					}
					countdown--;
					return;
				}

				ticksUntilSwap -= 20;

				int secondsUntilSwap = ticksUntilSwap / 20;
				LanguageService.Language defaultLang = languageService.getDefaultLanguage();

				// +10 seconds because countdown starts when ticksUntilSwap hits 0
				int realTimeUntilSwap = secondsUntilSwap + SWAP_COUNTDOWN_SECONDS;

				if (realTimeUntilSwap == 60) {
					participantBroadcast.accept(Messages.get(defaultLang, Messages.MessageKey.SWAP_IN_MINUTE));
				} else if (realTimeUntilSwap == 30) {
					participantBroadcast.accept(Messages.get(defaultLang, Messages.MessageKey.SWAP_IN_30_SECONDS));
				}

				if (ticksUntilSwap <= 0) {
					countdown = SWAP_COUNTDOWN_SECONDS;
					// Don't broadcast here, countdown block handles it
				}
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
			// currentPlayer.getInventory().clear();
			// currentPlayer.getInventory().setArmorContents(new org.bukkit.inventory.ItemStack[] { null, null, null, null });
			// currentPlayer.getInventory().setItemInOffHand(null);
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
