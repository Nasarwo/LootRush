package net.dagger.lootrush.service;

import net.dagger.lootrush.game.Role;
import net.minecraft.server.level.ServerPlayer;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleServiceTest {

    private ServerPlayer player(UUID id) {
        return new ServerPlayer(id);
    }

    @Test
    void defaultRoleIsPlayerWhenNotSet() {
        LanguageService languageService = new LanguageService();
        RoleService roleService = new RoleService(languageService);
        ServerPlayer player = player(UUID.randomUUID());

        assertEquals(Role.PLAYER, roleService.getRole(player));
    }
}
