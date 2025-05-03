package ua.yourserver.rmc_hub.core.command;

import ua.yourserver.rmc_hub.Main;
import ua.yourserver.rmc_hub.core.command.rickrollommands.RickrollCommands;
import ua.yourserver.rmc_hub.core.command.spawncommand.SpawnCommand;

public class CommandRegistry {
    private final Main plugin;

    public CommandRegistry(Main plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getCommand("setspawn").setExecutor(new SpawnCommand(plugin));
        plugin.getCommand("rickroll").setExecutor(new RickrollCommands(plugin));
    }
}
