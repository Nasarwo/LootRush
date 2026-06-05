package net.dagger.randomitemminigame.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WinCountService {
	private static final int TAB_LEADERBOARD_TOP = 5;
	private static final String WINS_SECTION = "wins";

	private final JavaPlugin plugin;
	private final LanguageService languageService;
	private final File winsFile;
	private final Map<UUID, Integer> winsByUuid = new ConcurrentHashMap<>();

	public WinCountService(JavaPlugin plugin, LanguageService languageService) {
		this.plugin = plugin;
		this.languageService = languageService;
		this.winsFile = new File(plugin.getDataFolder(), "wins.yml");
		load();
	}

	public void load() {
		winsByUuid.clear();
		if (!winsFile.exists()) {
			return;
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(winsFile);
		var section = config.getConfigurationSection(WINS_SECTION);
		if (section == null) {
			return;
		}
		for (String key : section.getKeys(false)) {
			try {
				UUID uuid = UUID.fromString(key);
				int wins = section.getInt(key, 0);
				if (wins > 0) {
					winsByUuid.put(uuid, wins);
				}
			} catch (IllegalArgumentException ignored) {
			}
		}
	}

	public void save() {
		plugin.getDataFolder().mkdirs();
		YamlConfiguration config = new YamlConfiguration();
		for (Map.Entry<UUID, Integer> e : winsByUuid.entrySet()) {
			config.set(WINS_SECTION + "." + e.getKey().toString(), e.getValue());
		}
		try {
			config.save(winsFile);
		} catch (IOException ex) {
			plugin.getLogger().warning("Не удалось сохранить wins.yml: " + ex.getMessage());
		}
	}

	public int getWins(UUID playerId) {
		return winsByUuid.getOrDefault(playerId, 0);
	}

	public int getWins(Player player) {
		return getWins(player.getUniqueId());
	}

	public void addWin(Player winner) {
		UUID id = winner.getUniqueId();
		winsByUuid.merge(id, 1, (a, b) -> a + b);
		save();
	}

	public List<Entry> getTop(int limit) {
		return winsByUuid.entrySet().stream()
				.sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
				.limit(limit)
				.map(e -> new Entry(e.getKey(), e.getValue()))
				.toList();
	}

	public void updateTabListForAll() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			LanguageService.Language lang = languageService.getLanguage(player);
			Component header = buildTabHeader();
			Component footer = buildTabFooter(lang);
			player.sendPlayerListHeaderAndFooter(header, footer);
		}
	}

	private Component buildTabHeader() {
		return Component.text("Loot Rush", NamedTextColor.GOLD);
	}

	private Component buildTabFooter(LanguageService.Language lang) {
		List<Entry> top = getTop(TAB_LEADERBOARD_TOP);
		if (top.isEmpty()) {
			return Messages.get(lang, Messages.MessageKey.LEADERBOARD_EMPTY);
		}
		Component title = Messages.get(lang, Messages.MessageKey.LEADERBOARD_TITLE);
		List<Component> lines = new ArrayList<>();
		lines.add(title);
		int place = 1;
		for (Entry e : top) {
			String name = getPlayerName(e.uuid());
			Component line = Messages.get(lang, Messages.MessageKey.LEADERBOARD_LINE, place, name, e.wins());
			lines.add(line);
			place++;
		}
		return joinNewlines(lines);
	}

	private String getPlayerName(UUID uuid) {
		OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
		if (off.getName() != null) {
			return off.getName();
		}
		return uuid.toString().substring(0, 8);
	}

	private static Component joinNewlines(List<Component> components) {
		if (components.isEmpty()) {
			return Component.empty();
		}
		Component result = components.get(0);
		for (int i = 1; i < components.size(); i++) {
			result = result.append(Component.newline()).append(components.get(i));
		}
		return result;
	}

	public record Entry(UUID uuid, int wins) {}
}
