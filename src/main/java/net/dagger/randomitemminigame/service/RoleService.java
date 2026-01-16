package net.dagger.randomitemminigame.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.dagger.randomitemminigame.game.Role;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class RoleService {
	private final LanguageService languageService;
	private final Map<UUID, Role> playerRoles = new HashMap<>();

	public RoleService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public Role getRole(Player player) {
		return playerRoles.getOrDefault(player.getUniqueId(), Role.PLAYER);
	}

	public void setRole(Player player, Role role) {
		LanguageService.Language lang = languageService.getLanguage(player);
		if (role == Role.PLAYER) {
			playerRoles.remove(player.getUniqueId());
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage(Messages.get(lang, Messages.MessageKey.NOW_PARTICIPATING));
		} else {
			playerRoles.put(player.getUniqueId(), Role.SPECTATOR);
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage(Messages.get(lang, Messages.MessageKey.NOW_SPECTATOR));
		}
	}

	public void prepareSpectators(Collection<? extends Player> spectators) {
		for (Player spectator : spectators) {
			spectator.setGameMode(GameMode.SPECTATOR);
			LanguageService.Language lang = languageService.getLanguage(spectator);
			spectator.sendMessage(Messages.get(lang, Messages.MessageKey.SPECTATING_ROUND));
		}
	}
}
