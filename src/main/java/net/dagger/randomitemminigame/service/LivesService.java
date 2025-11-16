package net.dagger.randomitemminigame.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class LivesService {
	private static final int MAX_LIVES = 5;
	private final Map<UUID, Integer> playerLives = new HashMap<>();

	public void initializeLives(List<Player> participants) {
		playerLives.clear();
		for (Player player : participants) {
			playerLives.put(player.getUniqueId(), MAX_LIVES);
		}
	}

	public int getLives(Player player) {
		return playerLives.getOrDefault(player.getUniqueId(), MAX_LIVES);
	}

	public int decreaseLives(Player player) {
		int lives = getLives(player);
		lives--;
		playerLives.put(player.getUniqueId(), lives);
		return lives;
	}

	public boolean hasLives(Player player) {
		return getLives(player) > 0;
	}

	public void removePlayer(Player player) {
		playerLives.remove(player.getUniqueId());
	}

	public void clear() {
		playerLives.clear();
	}

	public Map<UUID, Integer> getAllLives() {
		return new HashMap<>(playerLives);
	}

	public static int getMaxLives() {
		return MAX_LIVES;
	}
}
