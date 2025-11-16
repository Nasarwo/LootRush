package net.dagger.randomitemminigame.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.dagger.randomitemminigame.game.Role;

public class WinService {
	private final RoleService roleService;

	public WinService(RoleService roleService) {
		this.roleService = roleService;
	}

	public boolean hasTargetItem(Player player, Material targetItem) {
		return targetItem != null && player.getInventory().contains(targetItem);
	}

	public void removeTargetItemFromPlayers(Collection<? extends Player> players, Material targetItem) {
		for (Player player : players) {
			player.getInventory().remove(targetItem);
		}
	}

	public List<Player> getAlivePlayers() {
		return Bukkit.getOnlinePlayers().stream()
				.filter(p -> roleService.getRole(p) == Role.PLAYER && !p.isDead())
				.collect(Collectors.toList());
	}
}
