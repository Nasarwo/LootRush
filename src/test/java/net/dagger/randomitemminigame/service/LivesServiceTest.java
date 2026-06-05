package net.dagger.randomitemminigame.service;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LivesServiceTest {

    private Player mockPlayer(UUID id) {
        Player player = Mockito.mock(Player.class, Mockito.withSettings().lenient());
        Mockito.when(player.getUniqueId()).thenReturn(id);
        return player;
    }

    @Test
    void initializesAndDecreasesLives() {
        LivesService service = new LivesService();
        Player player = mockPlayer(UUID.randomUUID());

        service.initializeLives(List.of(player));
        assertEquals(5, service.getLives(player));
        assertTrue(service.hasLives(player));

        for (int i = 0; i < 5; i++) {
            service.decreaseLives(player);
        }
        assertEquals(0, service.getLives(player));
        assertFalse(service.hasLives(player));
    }

    @Test
    void unknownPlayerDefaultsToMaxLives() {
        LivesService service = new LivesService();
        Player player = mockPlayer(UUID.randomUUID());

        assertEquals(5, service.getLives(player));
    }
}
