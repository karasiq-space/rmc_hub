package ua.yourserver.rmc_hub;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ua.yourserver.rmc_hub.core.command.CommandRegistry;
import ua.yourserver.rmc_hub.core.manager.SpawnManager;
import ua.yourserver.rmc_hub.listener.PlayerJoinListener;
import ua.yourserver.rmc_hub.listener.absurdfeatures.DuckParadeListener;

public final class Main extends JavaPlugin implements Listener {

    private SpawnManager spawnManager;

    @Override
    public void onEnable() {
        // береження конфігу
        saveDefaultConfig();
        // Ініціалізація для точки спавну
        initializeManagers();

        this.registerListeners();

        // Реєстрація команд через CommandRegistry
        new CommandRegistry(this).registerCommands();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void initializeManagers() {
        spawnManager = new SpawnManager(this);
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    private void registerListeners() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new PlayerJoinListener(this), this);
        manager.registerEvents(new DuckParadeListener(this), this); // Added new listener
    }
}
