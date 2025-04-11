package ua.yourserver.rmc_hub.core.manager;

import ua.yourserver.rmc_hub.Rmc_hub;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class SpawnManager {
    private final Rmc_hub plugin;
    private Location spawnLocation;

    public SpawnManager(Rmc_hub plugin) {
        this.plugin = plugin;
        loadSpawnLocation();
    }

    public void setSpawnLocation(Location location) {
        this.spawnLocation = location;
        saveSpawnLocation();
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    private void loadSpawnLocation() {
        FileConfiguration config = plugin.getConfig();
        if (config.contains("spawn")) {
            double x = config.getDouble("spawn.x");
            double y = config.getDouble("spawn.y");
            double z = config.getDouble("spawn.z");
            float yaw = (float) config.getDouble("spawn.yaw");
            float pitch = (float) config.getDouble("spawn.pitch");
            String worldName = config.getString("spawn.world");
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                spawnLocation = new Location(world, x, y, z, yaw, pitch);
            } else {
                plugin.getLogger().warning("Світ не знайдено: " + worldName);
            }
        } else {
            plugin.getLogger().warning("Місце спавну не знайдено в конфігурації.");
        }
    }

    private void saveSpawnLocation() {
        if (spawnLocation != null) {
            FileConfiguration config = plugin.getConfig();
            config.set("spawn.x", spawnLocation.getX());
            config.set("spawn.y", spawnLocation.getY());
            config.set("spawn.z", spawnLocation.getZ());
            config.set("spawn.yaw", spawnLocation.getYaw());
            config.set("spawn.pitch", spawnLocation.getPitch());
            config.set("spawn.world", spawnLocation.getWorld().getName());
            plugin.saveConfig();
        }
    }
}
