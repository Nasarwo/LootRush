package net.dagger.randomitemminigame.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardService {
	private final LanguageService languageService;
	private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();
	private final Map<UUID, Objective> playerObjectives = new HashMap<>();
	private final Map<UUID, String> lastTimerKeys = new HashMap<>();

	public ScoreboardService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public void createScoreboard(Map<UUID, Integer> playerLives) {
		clear();

		for (UUID playerId : playerLives.keySet()) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null && player.isOnline()) {
				createPlayerScoreboard(player, playerLives.get(playerId));
			}
		}
	}

	private void createPlayerScoreboard(Player player, int lives) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		LanguageService.Language playerLang = languageService.getLanguage(player);
		Objective objective = scoreboard.registerNewObjective("lives", Criteria.DUMMY,
				Messages.get(playerLang, Messages.MessageKey.LIVES));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		playerScoreboards.put(player.getUniqueId(), scoreboard);
		playerObjectives.put(player.getUniqueId(), objective);

		updatePlayerLives(player, lives);
	}

	public void updatePlayerLives(Player player, int lives) {
		UUID playerId = player.getUniqueId();
		Objective objective = playerObjectives.get(playerId);
		if (objective == null) {
			return;
		}

		String playerName = player.getName();
		objective.getScore(playerName).setScore(lives);
		Scoreboard scoreboard = playerScoreboards.get(playerId);
		if (scoreboard != null) {
			player.setScoreboard(scoreboard);
		}
	}

	public void updateAllPlayersLives(Map<UUID, Integer> playerLives) {
		for (UUID playerId : playerLives.keySet()) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null && player.isOnline()) {
				int lives = playerLives.getOrDefault(playerId, LivesService.getMaxLives());
				updatePlayerLives(player, lives);
			}
		}
	}

	public void updateTimer(String timeString) {
		for (UUID playerId : playerScoreboards.keySet()) {
			Player player = Bukkit.getPlayer(playerId);
			if (player == null || !player.isOnline()) {
				continue;
			}

			Objective objective = playerObjectives.get(playerId);
			Scoreboard scoreboard = playerScoreboards.get(playerId);
			if (objective == null || scoreboard == null) {
				continue;
			}

			String lastTimerKey = lastTimerKeys.get(playerId);
			if (lastTimerKey != null) {
				scoreboard.resetScores(lastTimerKey);
			}

			LanguageService.Language playerLang = languageService.getLanguage(player);
			Component timerComponent = Component.text()
					.append(Messages.get(playerLang, Messages.MessageKey.TIME))
					.append(Component.text(timeString, NamedTextColor.YELLOW))
					.build();
			String timerKey = LegacyComponentSerializer.legacySection().serialize(timerComponent);
			lastTimerKeys.put(playerId, timerKey);
			objective.getScore(timerKey).setScore(999);

			player.setScoreboard(scoreboard);
		}
	}

	public void removePlayer(Player player) {
		UUID playerId = player.getUniqueId();
		Scoreboard scoreboard = playerScoreboards.remove(playerId);
		Objective objective = playerObjectives.remove(playerId);
		lastTimerKeys.remove(playerId);

		if (scoreboard != null && objective != null) {
			scoreboard.resetScores(player.getName());
			String timerKey = lastTimerKeys.get(playerId);
			if (timerKey != null) {
				scoreboard.resetScores(timerKey);
			}
			objective.unregister();
		}

		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	public void clear() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			UUID playerId = player.getUniqueId();
			if (playerScoreboards.containsKey(playerId)) {
				removePlayer(player);
			}
		}

		for (UUID playerId : new HashMap<>(playerScoreboards).keySet()) {
			Player player = Bukkit.getPlayer(playerId);
			if (player == null || !player.isOnline()) {
				Scoreboard scoreboard = playerScoreboards.remove(playerId);
				Objective objective = playerObjectives.remove(playerId);
				lastTimerKeys.remove(playerId);
				if (scoreboard != null && objective != null) {
					objective.unregister();
				}
			}
		}

		playerScoreboards.clear();
		playerObjectives.clear();
		lastTimerKeys.clear();
	}

	public Scoreboard getScoreboard() {
		if (!playerScoreboards.isEmpty()) {
			return playerScoreboards.values().iterator().next();
		}
		return null;
	}
}
