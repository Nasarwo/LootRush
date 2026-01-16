package net.dagger.randomitemminigame;

import java.util.Objects;

import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class LootRushMod extends JavaPlugin {
	public static JavaPlugin plugin;
	public static Server server;

	private LootRushGameManager gameManager;

	@Override
	public void onEnable() {
		plugin = this;
		server = this.getServer();

		gameManager = new LootRushGameManager(this);
		getServer().getPluginManager().registerEvents(gameManager, this);

		PluginCommand command = Objects.requireNonNull(getCommand("lootrush"),
				"Command lootrush is not defined in plugin.yml");
		command.setExecutor(gameManager);
		command.setTabCompleter(gameManager);
	}

	@Override
	public void onDisable() {
	}
}
