package net.dagger.randomitemminigame.service;

import java.util.Map;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardService {
	private Scoreboard gameScoreboard;
	private Objective livesObjective;
	private String lastTimerKey = null;

	public void createScoreboard(Map<UUID, Integer> playerLives) {
		gameScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		livesObjective = gameScoreboard.registerNewObjective("lives", Criteria.DUMMY, Component.text("Жизни", NamedTextColor.RED));
		livesObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

		// Устанавливаем scoreboard всем участникам и обновляем их жизни
		for (UUID playerId : playerLives.keySet()) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null && player.isOnline()) {
				player.setScoreboard(gameScoreboard);
				updatePlayerLives(player, playerLives.get(playerId));
			}
		}
	}

	public void updatePlayerLives(Player player, int lives) {
		if (livesObjective == null || gameScoreboard == null) {
			return;
		}

		String playerName = player.getName();
		livesObjective.getScore(playerName).setScore(lives);
		player.setScoreboard(gameScoreboard);
	}

	public void updateAllPlayersLives(Map<UUID, Integer> playerLives) {
		if (livesObjective == null || gameScoreboard == null) {
			return;
		}

		for (UUID playerId : playerLives.keySet()) {
			Player player = Bukkit.getPlayer(playerId);
			if (player != null && player.isOnline()) {
				int lives = playerLives.getOrDefault(playerId, LivesService.getMaxLives());
				updatePlayerLives(player, lives);
			}
		}
	}

	public void updateTimer(String timeString) {
		if (livesObjective == null || gameScoreboard == null) {
			return;
		}

		// Удаляем старый таймер, если он был
		if (lastTimerKey != null) {
			gameScoreboard.resetScores(lastTimerKey);
		}

		// Используем специальный ключ для таймера
		lastTimerKey = "§6Время: §e" + timeString;
		livesObjective.getScore(lastTimerKey).setScore(999); // Высокий score, чтобы таймер был вверху

		// Обновляем scoreboard для всех участников
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getScoreboard().equals(gameScoreboard)) {
				player.setScoreboard(gameScoreboard);
			}
		}
	}

	public void removePlayer(Player player) {
		if (gameScoreboard != null && livesObjective != null) {
			gameScoreboard.resetScores(player.getName());
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
	}

	public void clear() {
		if (gameScoreboard != null) {
			// Удаляем таймер, если он был
			if (lastTimerKey != null) {
				gameScoreboard.resetScores(lastTimerKey);
				lastTimerKey = null;
			}
			// Удаляем scoreboard у всех игроков
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getScoreboard().equals(gameScoreboard)) {
					player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
				}
			}
			// Очищаем объекты
			if (livesObjective != null) {
				livesObjective.unregister();
				livesObjective = null;
			}
			gameScoreboard = null;
		}
	}

	public Scoreboard getScoreboard() {
		return gameScoreboard;
	}
}
