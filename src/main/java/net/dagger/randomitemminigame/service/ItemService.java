package net.dagger.randomitemminigame.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;

public class ItemService {

	private final List<Material> itemPool;
	private final Random random = new Random();

	public ItemService(List<String> bannedItems) {
		Set<Material> bannedMaterials = new HashSet<>();
		List<Pattern> bannedPatterns = new ArrayList<>();

		for (String item : bannedItems) {
			if (item.startsWith("REGEX:")) {
				try {
					bannedPatterns.add(Pattern.compile(item.substring(6)));
				} catch (Exception e) {
					Bukkit.getLogger().warning("Invalid regex pattern in banned-items: " + item);
				}
			} else {
				try {
					bannedMaterials.add(Material.valueOf(item.toUpperCase()));
				} catch (IllegalArgumentException e) {
					Bukkit.getLogger().warning("Invalid material name in banned-items: " + item);
				}
			}
		}

		this.itemPool = Collections.unmodifiableList(
				Arrays.stream(Material.values())
						.filter(material -> isSurvivalObtainable(material, bannedMaterials, bannedPatterns))
						.collect(Collectors.toList())
		);

		Bukkit.getLogger().info("Item pool size: " + itemPool.size());
	}

	public Material pickRandomItem() {
		if (itemPool.isEmpty()) {
			return Material.DIRT;
		}
		return itemPool.get(random.nextInt(itemPool.size()));
	}

	public boolean isItemInPool(Material material) {
		return itemPool.contains(material);
	}

	public int getPoolSize() {
		return itemPool.size();
	}

	private boolean isSurvivalObtainable(Material material, Set<Material> bannedMaterials, List<Pattern> bannedPatterns) {
		if (!material.isItem()) {
			return false;
		}

		if (bannedMaterials.contains(material)) {
			return false;
		}

		String name = material.name();
		for (Pattern pattern : bannedPatterns) {
			if (pattern.matcher(name).matches()) {
				return false;
			}
		}

		return true;
	}
}
