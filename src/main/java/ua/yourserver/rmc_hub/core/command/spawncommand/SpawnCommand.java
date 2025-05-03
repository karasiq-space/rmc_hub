package ua.yourserver.rmc_hub.core.command.spawncommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.yourserver.rmc_hub.Main;

public class SpawnCommand implements CommandExecutor {
    private final Main plugin;

    public SpawnCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Цю команду можуть використовувати лише гравці!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (!player.hasPermission("rcore.setspawn")) {
                player.sendMessage("У вас немає дозволу встановлювати точку спавну!");
                return true;
            }
            plugin.getSpawnManager().setSpawnLocation(player.getLocation());
            player.sendMessage("Місце спавну встановлено!");
            return true;
        }

        return false;
    }
}