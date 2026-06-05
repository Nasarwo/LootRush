package net.dagger.randomitemminigame.service;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandServiceTest {

    private CommandSender mockSender() {
        return Mockito.mock(CommandSender.class, Mockito.withSettings().lenient());
    }

    @Test
    void dispatchesKnownSubcommands() {
        LanguageService languageService = new LanguageService();
        AtomicInteger startCalled = new AtomicInteger();
        AtomicInteger stopCalled = new AtomicInteger();
        AtomicInteger statusCalled = new AtomicInteger();
        AtomicInteger skipCalled = new AtomicInteger();
        AtomicInteger debugCalled = new AtomicInteger();
        AtomicInteger roleCalled = new AtomicInteger();
        AtomicInteger langCalled = new AtomicInteger();
        AtomicInteger banlistCalled = new AtomicInteger();

        CommandService service = new CommandService(
                languageService,
                sender -> startCalled.incrementAndGet(),
                sender -> stopCalled.incrementAndGet(),
                sender -> statusCalled.incrementAndGet(),
                sender -> skipCalled.incrementAndGet(),
                sender -> debugCalled.incrementAndGet(),
                args -> roleCalled.incrementAndGet(),
                args -> langCalled.incrementAndGet(),
                args -> banlistCalled.incrementAndGet(),
                java.util.List::of
        );

        CommandSender sender = mockSender();
        Command command = Mockito.mock(Command.class);

        assertTrue(service.execute(sender, command, "lr", new String[]{"start"}));
        assertTrue(service.execute(sender, command, "lr", new String[]{"stop"}));
        assertTrue(service.execute(sender, command, "lr", new String[]{"status"}));
        assertTrue(service.execute(sender, command, "lr", new String[]{"skip"}));
        assertTrue(service.execute(sender, command, "lr", new String[]{"debug"}));
        assertTrue(service.execute(sender, command, "lr", new String[]{"role", "player"}));
        assertTrue(service.execute(sender, command, "lr", new String[]{"lang", "ru"}));
        assertTrue(service.execute(sender, command, "lr", new String[]{"banlist"}));

        assertEquals(1, startCalled.get());
        assertEquals(1, stopCalled.get());
        assertEquals(1, statusCalled.get());
        assertEquals(1, skipCalled.get());
        assertEquals(1, debugCalled.get());
        assertEquals(1, roleCalled.get());
        assertEquals(1, langCalled.get());
        assertEquals(1, banlistCalled.get());
    }

    @Test
    void tabCompleteReturnsCoreSubcommands() {
        LanguageService languageService = new LanguageService();
        CommandService service = new CommandService(
                languageService,
                sender -> {},
                sender -> {},
                sender -> {},
                sender -> {},
                sender -> {},
                args -> {},
                args -> {},
                args -> {},
                java.util.List::of
        );

        CommandSender sender = mockSender();
        Command command = Mockito.mock(Command.class);
        List<String> result = service.onTabComplete(sender, command, "lr", new String[]{"s"});

        assertTrue(result.contains("start"));
        assertTrue(result.contains("stop"));
    }

}
