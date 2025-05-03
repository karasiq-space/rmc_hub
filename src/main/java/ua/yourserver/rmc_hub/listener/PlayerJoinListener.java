package ua.yourserver.rmc_hub.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import ua.yourserver.rmc_hub.Main;

public class PlayerJoinListener implements Listener {
    private final Main plugin;

    public PlayerJoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Телепортуємо гравця, якщо спавн-локація існує
        if (plugin.getSpawnManager().getSpawnLocation() != null) {
            player.teleport(plugin.getSpawnManager().getSpawnLocation());
        }
    }
}