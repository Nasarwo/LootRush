package net.dagger.randomitemminigame.service;

import net.dagger.randomitemminigame.game.Role;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleAndWinServiceTest {

    private Player mockPlayer() {
        Player player = Mockito.mock(Player.class, Mockito.withSettings().lenient());
        Mockito.when(player.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
        return player;
    }

    @Test
    void roleServiceSwitchesGameModeAndRole() {
        LanguageService languageService = new LanguageService();
        RoleService roleService = new RoleService(languageService);

        Player player = mockPlayer();

        roleService.setRole(player, Role.SPECTATOR);
        Mockito.verify(player).setGameMode(GameMode.SPECTATOR);
        assertEquals(Role.SPECTATOR, roleService.getRole(player));

        roleService.setRole(player, Role.PLAYER);
        Mockito.verify(player).setGameMode(GameMode.SURVIVAL);
        assertEquals(Role.PLAYER, roleService.getRole(player));
    }

    @Test
    void winServiceChecksAndRemovesTargetItems() {
        LanguageService languageService = new LanguageService();
        RoleService roleService = new RoleService(languageService);
        WinService winService = new WinService(roleService);

        Player player = mockPlayer();
        PlayerInventory inventory = Mockito.mock(PlayerInventory.class);
        Mockito.when(player.getInventory()).thenReturn(inventory);
        Mockito.when(inventory.contains(Material.DIAMOND)).thenReturn(true);

        assertTrue(winService.hasTargetItem(player, Material.DIAMOND));
        assertFalse(winService.hasTargetItem(player, null));

        winService.removeTargetItemFromPlayers(java.util.List.of(player), Material.DIAMOND);
        Mockito.verify(inventory).remove(Material.DIAMOND);
    }
}
