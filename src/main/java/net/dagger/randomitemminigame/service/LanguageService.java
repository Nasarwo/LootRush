package net.dagger.randomitemminigame.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class LanguageService {
	public enum Language {
		RU("ru"),
		EN("en"),
		UK("uk");

		private final String code;

		Language(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		public static Language fromCode(String code) {
			if (code == null) {
				return RU;
			}
			String lowerCode = code.toLowerCase(Locale.ROOT);
			if ("en".equals(lowerCode) || "english".equals(lowerCode)) {
				return EN;
			}
			if ("uk".equals(lowerCode) || "ukrainian".equals(lowerCode) || "ua".equals(lowerCode)) {
				return UK;
			}
			return RU;
		}
	}

	private final Map<UUID, Language> playerLanguages = new HashMap<>();
	private Language defaultLanguage = Language.RU;

	public Language getLanguage(Player player) {
		return playerLanguages.getOrDefault(player.getUniqueId(), defaultLanguage);
	}

	public Language getLanguage(UUID playerId) {
		return playerLanguages.getOrDefault(playerId, defaultLanguage);
	}

	public void setLanguage(Player player, Language language) {
		playerLanguages.put(player.getUniqueId(), language);
	}

	public void setLanguage(UUID playerId, Language language) {
		playerLanguages.put(playerId, language);
	}

	public void removePlayer(Player player) {
		playerLanguages.remove(player.getUniqueId());
	}

	public void clear() {
		playerLanguages.clear();
	}

	public Language getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(Language language) {
		this.defaultLanguage = language;
	}
}
