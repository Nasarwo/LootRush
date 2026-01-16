package net.dagger.randomitemminigame.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandService implements TabCompleter {
	private final LanguageService languageService;
	private final Consumer<CommandSender> handleStart;
	private final Consumer<CommandSender> handleStop;
	private final Consumer<CommandSender> handleStatus;
	private final Consumer<CommandSender> handleCancel;
	private final Consumer<CommandSender> handleSkip;
	private final Consumer<CommandSenderAndArgs> handleRole;
	private final Consumer<CommandSenderAndArgs> handleLang;

	public CommandService(
			LanguageService languageService,
			Consumer<CommandSender> handleStart,
			Consumer<CommandSender> handleStop,
			Consumer<CommandSender> handleStatus,
			Consumer<CommandSender> handleCancel,
			Consumer<CommandSender> handleSkip,
			Consumer<CommandSenderAndArgs> handleRole,
			Consumer<CommandSenderAndArgs> handleLang) {
		this.languageService = languageService;
		this.handleStart = handleStart;
		this.handleStop = handleStop;
		this.handleStatus = handleStatus;
		this.handleCancel = handleCancel;
		this.handleSkip = handleSkip;
		this.handleRole = handleRole;
		this.handleLang = handleLang;
	}

	public boolean execute(CommandSender sender, Command command, String label, String[] args) {
		LanguageService.Language lang = getLanguage(sender);
		if (args.length == 0) {
			sender.sendMessage(Messages.get(lang, Messages.MessageKey.USAGE));
			return true;
		}

		switch (args[0].toLowerCase(Locale.ROOT)) {
		case "start":
			handleStart.accept(sender);
			return true;
		case "stop":
			handleStop.accept(sender);
			return true;
		case "status":
			handleStatus.accept(sender);
			return true;
		case "role":
			handleRole.accept(new CommandSenderAndArgs(sender, args));
			return true;
		case "cancel":
			handleCancel.accept(sender);
			return true;
		case "skip":
			handleSkip.accept(sender);
			return true;
		case "lang":
			handleLang.accept(new CommandSenderAndArgs(sender, args));
			return true;
		default:
			sender.sendMessage(Messages.get(lang, Messages.MessageKey.UNKNOWN_SUBCOMMAND));
			return true;
		}
	}

	private LanguageService.Language getLanguage(CommandSender sender) {
		if (sender instanceof Player player) {
			return languageService.getLanguage(player);
		}
		return languageService.getDefaultLanguage();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return Arrays.asList("start", "stop", "status", "role", "cancel", "skip", "lang").stream()
					.filter(option -> option.startsWith(args[0].toLowerCase(Locale.ROOT)))
					.collect(Collectors.toList());
		}

		if (args.length == 2 && "lang".equalsIgnoreCase(args[0])) {
			return Arrays.asList("ru", "en").stream()
					.filter(option -> option.startsWith(args[1].toLowerCase(Locale.ROOT)))
					.collect(Collectors.toList());
		}

		if (args.length == 2 && "role".equalsIgnoreCase(args[0])) {
			return Arrays.asList("player", "spectator").stream()
					.filter(option -> option.startsWith(args[1].toLowerCase(Locale.ROOT)))
					.collect(Collectors.toList());
		}

		if (args.length == 3 && "role".equalsIgnoreCase(args[0])) {
			List<String> completions = new ArrayList<>();
			String[] selectors = {"@a", "@p", "@r", "@s", "@e[type=player]"};
			for (String selector : selectors) {
				if (selector.toLowerCase(Locale.ROOT).startsWith(args[2].toLowerCase(Locale.ROOT))) {
					completions.add(selector);
				}
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				String name = player.getName();
				if (name.toLowerCase(Locale.ROOT).startsWith(args[2].toLowerCase(Locale.ROOT))) {
					completions.add(name);
				}
			}
			return completions;
		}
		return new ArrayList<>();
	}

	public static class CommandSenderAndArgs {
		public final CommandSender sender;
		public final String[] args;

		public CommandSenderAndArgs(CommandSender sender, String[] args) {
			this.sender = sender;
			this.args = args;
		}
	}
}
