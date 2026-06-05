package net.dagger.lootrush.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesTest {

    @Test
    void returnsLocalizedStatusStrings() {
        String ru = Messages.getString(LanguageService.Language.RU, Messages.MessageKey.GAME_NOT_STARTED);
        String en = Messages.getString(LanguageService.Language.EN, Messages.MessageKey.GAME_NOT_STARTED);
        String ua = Messages.getString(LanguageService.Language.UK, Messages.MessageKey.GAME_NOT_STARTED);

        assertTrue(ru.contains("не запущена") || ru.contains("не запущено"));
        assertTrue(en.toLowerCase().contains("not started"));
        assertTrue(ua.toLowerCase().contains("не запущено"));
    }

    @Test
    void returnsLocalizedRoleStrings() {
        String ru = Messages.getString(LanguageService.Language.RU, Messages.MessageKey.NOW_SPECTATOR);
        String en = Messages.getString(LanguageService.Language.EN, Messages.MessageKey.NOW_SPECTATOR);
        String ua = Messages.getString(LanguageService.Language.UK, Messages.MessageKey.NOW_SPECTATOR);

        assertTrue(ru.toLowerCase().contains("наблюд"));
        assertTrue(en.toLowerCase().contains("spectator"));
        assertTrue(ua.toLowerCase().contains("спостерігач"));
    }
}
