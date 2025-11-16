package net.dagger.randomitemminigame.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;

public class ItemService {
	private static final Set<Material> BLACKLIST = EnumSet.of(
			Material.STRUCTURE_BLOCK,
			Material.JIGSAW,
			Material.COMMAND_BLOCK,
			Material.REPEATING_COMMAND_BLOCK,
			Material.CHAIN_COMMAND_BLOCK,
			Material.BARRIER,
			Material.DEBUG_STICK,
			Material.KNOWLEDGE_BOOK,
			Material.LIGHT,
			Material.BUNDLE,
			Material.BEDROCK,
			Material.BUDDING_AMETHYST,
			Material.COMMAND_BLOCK_MINECART,
			Material.SPAWNER,
			Material.REINFORCED_DEEPSLATE,
			Material.STRUCTURE_VOID,
			Material.END_PORTAL_FRAME,
			Material.END_PORTAL,
			Material.END_GATEWAY,
			Material.NETHER_PORTAL,
			Material.SUSPICIOUS_SAND,
			Material.SUSPICIOUS_GRAVEL,
			// Лёд и связанные блоки (требуют Silk Touch)
			Material.ICE,
			Material.PACKED_ICE,
			Material.BLUE_ICE,
			Material.FROSTED_ICE,
			// Коконы пчёл (требуют Silk Touch)
			Material.BEE_NEST,
			// Большие аметистовые друзы (требуют Silk Touch)
			Material.LARGE_AMETHYST_BUD,
			Material.MEDIUM_AMETHYST_BUD,
			Material.SMALL_AMETHYST_BUD,
			Material.AMETHYST_CLUSTER,
			// Грибные блоки (требуют Silk Touch)
			Material.BROWN_MUSHROOM_BLOCK,
			Material.RED_MUSHROOM_BLOCK,
			Material.MUSHROOM_STEM,
			// Трава и мицелий (требуют Silk Touch)
			Material.GRASS_BLOCK,
			Material.MYCELIUM,
			Material.PODZOL,
			// Снежные блоки (требуют Silk Touch)
			Material.SNOW_BLOCK,
			// Коралловые блоки (требуют Silk Touch)
			Material.TUBE_CORAL_BLOCK,
			Material.BRAIN_CORAL_BLOCK,
			Material.BUBBLE_CORAL_BLOCK,
			Material.FIRE_CORAL_BLOCK,
			Material.HORN_CORAL_BLOCK,
			Material.DEAD_TUBE_CORAL_BLOCK,
			Material.DEAD_BRAIN_CORAL_BLOCK,
			Material.DEAD_BUBBLE_CORAL_BLOCK,
			Material.DEAD_FIRE_CORAL_BLOCK,
			Material.DEAD_HORN_CORAL_BLOCK
	);

	private static final List<Material> ITEM_POOL = Collections.unmodifiableList(
			Arrays.stream(Material.values())
					.filter(ItemService::isSurvivalObtainable)
					.collect(Collectors.toList())
	);

	private final Random random = new Random();

	public Material pickRandomItem() {
		return ITEM_POOL.get(random.nextInt(ITEM_POOL.size()));
	}

	public boolean isItemInPool(Material material) {
		return ITEM_POOL.contains(material);
	}

	public int getPoolSize() {
		return ITEM_POOL.size();
	}

	private static boolean isSurvivalObtainable(Material material) {
		if (!material.isItem()) {
			return false;
		}

		if (BLACKLIST.contains(material)) {
			return false;
		}

		String name = material.name();

		// Исключаем предметы, которые невозможно получить в выживании
		if (name.startsWith("MUSIC_DISC")
				|| name.startsWith("DISC_FRAGMENT")
				|| name.endsWith("_SPAWN_EGG")
				|| name.endsWith("_ORE")
				|| name.startsWith("INFESTED_")
				|| name.contains("PORTAL")
				|| name.contains("COMMAND_BLOCK")
				|| name.contains("STRUCTURE")
				|| name.contains("BARRIER")
				|| name.contains("DEBUG_STICK")
				|| name.contains("REINFORCED_DEEPSLATE")
				|| name.contains("BUDDING_AMETHYST")
				|| name.contains("SPAWNER")
				|| name.contains("SUSPICIOUS")
				|| name.contains("SHERD")
				// Блоки, требующие Silk Touch
				|| name.contains("CORAL_BLOCK")
				|| name.equals("ICE")
				|| name.equals("PACKED_ICE")
				|| name.equals("BLUE_ICE")
				|| name.equals("FROSTED_ICE")
				|| name.equals("GRASS_BLOCK")
				|| name.equals("MYCELIUM")
				|| name.equals("PODZOL")
				|| name.equals("SNOW_BLOCK")
				|| name.equals("BEE_NEST")
				|| name.contains("MUSHROOM_BLOCK")
				|| name.contains("MUSHROOM_STEM")
				|| name.contains("AMETHYST_BUD")
				|| name.equals("AMETHYST_CLUSTER")
				// Технические блоки
				|| name.contains("CHAIN_COMMAND_BLOCK")
				|| name.contains("REPEATING_COMMAND_BLOCK")
				|| name.equals("JIGSAW")
				|| name.equals("STRUCTURE_VOID")
				|| name.equals("LIGHT")
				|| name.equals("BUNDLE")
				|| name.equals("KNOWLEDGE_BOOK")
				|| name.equals("DEBUG_STICK")
				|| name.equals("BARRIER")
				|| name.equals("BEDROCK")
				|| name.equals("END_PORTAL_FRAME")
				|| name.equals("END_PORTAL")
				|| name.equals("END_GATEWAY")
				|| name.equals("NETHER_PORTAL")
				|| name.equals("SPAWNER")
				|| name.equals("COMMAND_BLOCK_MINECART")) {
			return false;
		}

		return true;
	}
}
