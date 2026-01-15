package net.dagger.randomitemminigame.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandService implements TabCompleter {
	private final Consumer<CommandSender> handleStart;
	private final Consumer<CommandSender> handleStop;
	private final Consumer<CommandSender> handleStatus;
	private final Consumer<CommandSender> handleCancel;
	private final Consumer<CommandSender> handleSkip;
	private final Consumer<CommandSenderAndArgs> handleRole;

	public CommandService(
			Consumer<CommandSender> handleStart,
			Consumer<CommandSender> handleStop,
			Consumer<CommandSender> handleStatus,
			Consumer<CommandSender> handleCancel,
			Consumer<CommandSender> handleSkip,
			Consumer<CommandSenderAndArgs> handleRole) {
		this.handleStart = handleStart;
		this.handleStop = handleStop;
		this.handleStatus = handleStatus;
		this.handleCancel = handleCancel;
		this.handleSkip = handleSkip;
		this.handleRole = handleRole;
	}

	public boolean execute(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(Component.text()
					.append(Component.text("Использование: /", NamedTextColor.YELLOW))
					.append(Component.text(label, NamedTextColor.GOLD))
					.append(Component.text(" <start|stop|status|role|cancel|skip>", NamedTextColor.YELLOW))
					.build());
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
		default:
			sender.sendMessage(Component.text("Неизвестная подкоманда. Используйте: start, stop, status, role, cancel или skip.", NamedTextColor.RED));
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return Arrays.asList("start", "stop", "status", "role", "cancel", "skip").stream()
					.filter(option -> option.startsWith(args[0].toLowerCase(Locale.ROOT)))
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
