package net.dagger.randomitemminigame.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

		for (Player player : Bukkit.getOnlinePlayers()) {
			createPlayerScoreboard(player, playerLives);
		}
	}

	private void createPlayerScoreboard(Player viewer, Map<UUID, Integer> allLives) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		LanguageService.Language playerLang = languageService.getLanguage(viewer);
		Objective objective = scoreboard.registerNewObjective("lives", Criteria.DUMMY,
				Messages.get(playerLang, Messages.MessageKey.LIVES));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		playerScoreboards.put(viewer.getUniqueId(), scoreboard);
		playerObjectives.put(viewer.getUniqueId(), objective);

		for (Map.Entry<UUID, Integer> entry : allLives.entrySet()) {
			Player target = Bukkit.getPlayer(entry.getKey());
			if (target != null) {
				objective.getScore(target.getName()).setScore(entry.getValue());
			}
		}

		viewer.setScoreboard(scoreboard);
	}

	public void updatePlayerLives(Player targetPlayer, int lives) {
		String targetName = targetPlayer.getName();

		for (Objective objective : playerObjectives.values()) {
			objective.getScore(targetName).setScore(lives);
		}
	}

	public void updateAllPlayersLives(Map<UUID, Integer> playerLives) {
		for (Map.Entry<UUID, Integer> entry : playerLives.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			if (player != null) {
				updatePlayerLives(player, entry.getValue());
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
			lastTimerKeys.remove(playerId);
		}
	}

	public void removePlayer(Player player) {
		UUID playerId = player.getUniqueId();

		Scoreboard scoreboard = playerScoreboards.remove(playerId);
		Objective objective = playerObjectives.remove(playerId);
		lastTimerKeys.remove(playerId);

		if (scoreboard != null && objective != null) {
			objective.unregister();
		}

		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

		for (Objective obj : playerObjectives.values()) {
			obj.getScoreboard().resetScores(player.getName());
		}
	}

	public void clear() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			UUID playerId = player.getUniqueId();
			if (playerScoreboards.containsKey(playerId)) {
				player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			}
		}

		for (Objective objective : playerObjectives.values()) {
			if (objective != null) {
				objective.unregister();
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

	public void addViewer(Player viewer, Map<UUID, Integer> currentLives) {
		if (playerScoreboards.containsKey(viewer.getUniqueId())) {
			return;
		}
		createPlayerScoreboard(viewer, currentLives);
	}
}
