package net.dagger.randomitemminigame.service;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageServiceTest {

    @Test
    void fromCodeRecognizesExpectedAliases() {
        assertEquals(LanguageService.Language.EN, LanguageService.Language.fromCode("en"));
        assertEquals(LanguageService.Language.EN, LanguageService.Language.fromCode("English"));
        assertEquals(LanguageService.Language.UK, LanguageService.Language.fromCode("ua"));
        assertEquals(LanguageService.Language.UK, LanguageService.Language.fromCode("Ukrainian"));
        assertEquals(LanguageService.Language.RU, LanguageService.Language.fromCode("ru"));
        assertEquals(LanguageService.Language.RU, LanguageService.Language.fromCode("unknown"));
        assertEquals(LanguageService.Language.RU, LanguageService.Language.fromCode(null));
    }

    @Test
    void storesAndResolvesLanguageByUuid() {
        LanguageService service = new LanguageService();
        UUID id = UUID.randomUUID();

        assertEquals(LanguageService.Language.RU, service.getLanguage(id));
        service.setLanguage(id, LanguageService.Language.EN);
        assertEquals(LanguageService.Language.EN, service.getLanguage(id));

        service.clear();
        assertEquals(LanguageService.Language.RU, service.getLanguage(id));
    }
}
