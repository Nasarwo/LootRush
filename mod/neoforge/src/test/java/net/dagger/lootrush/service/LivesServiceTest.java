package net.dagger.lootrush.service;

import net.minecraft.server.level.ServerPlayer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LivesServiceTest {

    private ServerPlayer player(UUID id) {
        return new ServerPlayer(id);
    }

    @Test
    void initializesAndDecreasesLives() {
        LivesService service = new LivesService();
        ServerPlayer player = player(UUID.randomUUID());

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
        ServerPlayer player = player(UUID.randomUUID());
        assertEquals(5, service.getLives(player));
    }
}
