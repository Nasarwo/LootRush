package net.dagger.randomitemminigame.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesTest {

    @Test
    void returnsLocalizedStrings() {
        String ru = Messages.getString(LanguageService.Language.RU, Messages.MessageKey.GAME_NOT_STARTED);
        String en = Messages.getString(LanguageService.Language.EN, Messages.MessageKey.GAME_NOT_STARTED);
        String ua = Messages.getString(LanguageService.Language.UK, Messages.MessageKey.GAME_NOT_STARTED);

        assertTrue(ru.contains("не запущена") || ru.contains("не запущено"));
        assertTrue(en.toLowerCase().contains("not started"));
        assertTrue(ua.toLowerCase().contains("не запущено"));
    }

    @Test
    void returnsLocalizedBanlistStrings() {
        String ru = Messages.getString(LanguageService.Language.RU, Messages.MessageKey.BANLIST_EMPTY);
        String en = Messages.getString(LanguageService.Language.EN, Messages.MessageKey.BANLIST_EMPTY);
        String ua = Messages.getString(LanguageService.Language.UK, Messages.MessageKey.BANLIST_EMPTY);

        assertTrue(ru.toLowerCase().contains("список"));
        assertTrue(en.toLowerCase().contains("banlist"));
        assertTrue(ua.toLowerCase().contains("список"));
    }
}
