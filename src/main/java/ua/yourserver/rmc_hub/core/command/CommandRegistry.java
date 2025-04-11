package ua.yourserver.rmc_hub.core.command;

import ua.yourserver.rmc_hub.Rmc_hub;
import ua.yourserver.rmc_hub.core.command.spawncommand.SpawnCommand;

public class CommandRegistry {
    private final Rmc_hub plugin;

    public CommandRegistry(Rmc_hub plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getCommand("setspawn").setExecutor(new SpawnCommand(plugin));
    }
}
