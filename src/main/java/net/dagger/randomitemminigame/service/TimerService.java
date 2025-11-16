package net.dagger.randomitemminigame.service;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.dagger.randomitemminigame.game.GameState;

public class TimerService {
	private final JavaPlugin plugin;
	private final ScoreboardService scoreboardService;
	private BukkitRunnable timerTask;
	private long gameStartTime;
	private GameState currentState;

	public TimerService(JavaPlugin plugin, ScoreboardService scoreboardService) {
		this.plugin = plugin;
		this.scoreboardService = scoreboardService;
	}

	public void start(long startTime, GameState state) {
		cancel();
		this.gameStartTime = startTime;
		this.currentState = state;
		timerTask = new BukkitRunnable() {
			@Override
			public void run() {
				if (currentState != GameState.ACTIVE || scoreboardService.getScoreboard() == null) {
					return;
				}

				// Вычисляем прошедшее время
				long elapsed = System.currentTimeMillis() - gameStartTime;
				long seconds = elapsed / 1000;
				long minutes = seconds / 60;
				seconds = seconds % 60;

				// Форматируем время как ММ:СС
				String timeString = String.format("%02d:%02d", minutes, seconds);

				// Обновляем таймер в scoreboard
				scoreboardService.updateTimer(timeString);
			}
		};
		timerTask.runTaskTimer(plugin, 0L, 20L); // Обновляем каждую секунду
	}

	public void updateState(GameState state) {
		this.currentState = state;
	}

	public void cancel() {
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
	}
}
