package net.dagger.randomitemminigame.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.dagger.randomitemminigame.game.Role;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class RoleService {
	private final Map<UUID, Role> playerRoles = new HashMap<>();

	public Role getRole(Player player) {
		return playerRoles.getOrDefault(player.getUniqueId(), Role.PLAYER);
	}

	public void setRole(Player player, Role role) {
		if (role == Role.PLAYER) {
			playerRoles.remove(player.getUniqueId());
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage(Component.text("Теперь вы участвуете в мини-игре.", NamedTextColor.GREEN));
		} else {
			playerRoles.put(player.getUniqueId(), Role.SPECTATOR);
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage(Component.text("Вы перешли в режим наблюдателя.", NamedTextColor.AQUA));
		}
	}

	public void prepareSpectators(Collection<? extends Player> spectators) {
		for (Player spectator : spectators) {
			spectator.setGameMode(GameMode.SPECTATOR);
			spectator.sendMessage(Component.text("Вы наблюдаете за раундом как зритель.", NamedTextColor.GRAY));
		}
	}
}
