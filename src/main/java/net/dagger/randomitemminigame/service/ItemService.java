package net.dagger.randomitemminigame.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Material;

public class ItemService {
	private static final Logger LOGGER = Logger.getLogger(ItemService.class.getName());

	private final Set<Material> bannedMaterials = new HashSet<>();
	private final List<Pattern> bannedPatterns = new ArrayList<>();
	private final List<String> bannedItemsRaw = new ArrayList<>();
	private List<Material> itemPool;
	private final Random random = new Random();

	public ItemService(List<String> bannedItems) {
		for (String item : bannedItems) {
			addBannedItem(item);
		}
		rebuildPool();
	}

	public synchronized boolean addBannedItem(String item) {
		if (item == null || item.isBlank()) {
			return false;
		}
		String normalized = normalizeEntry(item);
		if (bannedItemsRaw.contains(normalized)) {
			return false;
		}
		if (normalized.startsWith("REGEX:")) {
			try {
				bannedPatterns.add(Pattern.compile(normalized.substring(6)));
			} catch (Exception e) {
				LOGGER.warning("Invalid regex pattern in banned-items: " + normalized);
				return false;
			}
		} else {
			try {
				bannedMaterials.add(Material.valueOf(normalized));
			} catch (IllegalArgumentException e) {
				LOGGER.warning("Invalid material name in banned-items: " + normalized);
				return false;
			}
		}
		bannedItemsRaw.add(normalized);
		rebuildPool();
		return true;
	}

	public synchronized boolean removeBannedItem(String item) {
		if (item == null || item.isBlank()) {
			return false;
		}
		String normalized = normalizeEntry(item);
		if (!bannedItemsRaw.remove(normalized)) {
			return false;
		}
		if (normalized.startsWith("REGEX:")) {
			String regex = normalized.substring(6);
			bannedPatterns.removeIf(pattern -> pattern.pattern().equals(regex));
		} else {
			try {
				bannedMaterials.remove(Material.valueOf(normalized));
			} catch (IllegalArgumentException ignored) {
			}
		}
		rebuildPool();
		return true;
	}

	public synchronized List<String> getBannedItems() {
		return new ArrayList<>(bannedItemsRaw);
	}

	private String normalizeEntry(String raw) {
		String trimmed = raw.trim();
		if (trimmed.regionMatches(true, 0, "REGEX:", 0, 6)) {
			return "REGEX:" + trimmed.substring(6);
		}
		return trimmed.toUpperCase();
	}

	private synchronized void rebuildPool() {
		this.itemPool = Collections.unmodifiableList(
			Arrays.stream(Material.values())
				.filter(this::isSurvivalObtainable)
				.collect(Collectors.toList())
		);
		LOGGER.info("Item pool size: " + itemPool.size());
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

	private boolean isSurvivalObtainable(Material material) {
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
