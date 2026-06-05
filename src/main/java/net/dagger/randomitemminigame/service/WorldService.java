package net.dagger.randomitemminigame.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

@SuppressWarnings("removal")
public class WorldService {
	private static final Logger LOGGER = Bukkit.getLogger();

	private void applyCommonWorldRules(World world, boolean daylightCycle, boolean weatherCycle) {
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, daylightCycle);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, weatherCycle);
		if (!daylightCycle) {
			try {
				world.setTime(0);
			} catch (IllegalArgumentException ignored) {
				LOGGER.fine("Skipping setTime(0) for world '" + world.getName() + "' because it has no world clock");
			}
		}
	}

	public void setWorldStateForLoading() {
		for (World world : Bukkit.getWorlds()) {
			world.setDifficulty(Difficulty.PEACEFUL);
			applyCommonWorldRules(world, false, false);
		}
	}

	public void setSafetyBorder() {
		for (World world : Bukkit.getWorlds()) {
			world.getWorldBorder().setCenter(world.getSpawnLocation());
			world.getWorldBorder().setSize(96);
		}
	}

	public void resetBorder() {
		for (World world : Bukkit.getWorlds()) {
			world.getWorldBorder().reset();
		}
	}

	public void setWorldStateActive() {
		for (World world : Bukkit.getWorlds()) {
			world.setDifficulty(Difficulty.NORMAL);
			applyCommonWorldRules(world, true, true);
		}
	}

	public void setWorldStateAfterGame() {
		for (World world : Bukkit.getWorlds()) {
			world.setDifficulty(Difficulty.PEACEFUL);
			applyCommonWorldRules(world, true, false);
		}
		resetAdvancements();
	}

	private void resetAdvancements() {
		List<Advancement> advancements = new ArrayList<>();
		Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
		while (iterator.hasNext()) {
			advancements.add(iterator.next());
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			for (Advancement advancement : advancements) {
				AdvancementProgress progress = player.getAdvancementProgress(advancement);
				for (String criterion : new ArrayList<>(progress.getAwardedCriteria())) {
					progress.revokeCriteria(criterion);
				}
			}
		}
	}
}
