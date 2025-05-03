package ua.yourserver.rmc_hub.listener.absurdfeatures;

import org.bukkit.*;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ua.yourserver.rmc_hub.Main;

import java.util.*;

public class DuckParadeListener implements Listener {
    private final Main plugin;
    private final List<Chicken> ducks = new ArrayList<>();
    private final HashMap<UUID, Long> knockbackCooldowns = new HashMap<>();
    private static final Location START_POINT = new Location(Bukkit.getWorlds().get(0), -14, 99, -38);
    private static final Location END_POINT = new Location(Bukkit.getWorlds().get(0), -10, 100, 41);
    private static final long PARADE_TIME = 6000;
    private static final double PROXIMITY_DISTANCE = 1.5;
    private static final long KNOCKBACK_COOLDOWN = 2000;
    private boolean paradeActive = false;
    private static final String[] ABSURD_NAMES = {
            "Космічна Квоква", "Пан Гусак", "Курка-Ніндзя", "Кряква-Дискотека",
            "Галактичний Кудкудак", "Містер Пір'їнко", "Курочка-Ракета", "Квок-Квок Маг",
            "Леді Гребінець", "Капітан Клювокрил", "Курка-Шпигунка", "Кряква-Самурай",
            "Професор Пух", "Курка-Привид", "Генерал Квок", "Квоква-Рокзірка",
            "Курка-Казка", "Сер Пташка", "Кряква-Чарівниця", "Курка-Галактика"
    };
    private final Random random = new Random();

    public DuckParadeListener(Main plugin) {
        this.plugin = plugin;
        startParadeScheduler();
        startProximityChecker();
    }

    private void startParadeScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = START_POINT.getWorld();
                if (world == null) {
                    Bukkit.getLogger().warning("Світ не знайдено для Параду Курочок!");
                    return;
                }

                long time = world.getTime();
                if (time >= PARADE_TIME && time < PARADE_TIME + 20 && !paradeActive) {
                    spawnDuckParade();
                } else if (time >= PARADE_TIME + 1200 && paradeActive) {
                    despawnDuckParade();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startProximityChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!paradeActive || ducks.isEmpty()) {
                    return;
                }

                long currentTime = System.currentTimeMillis();
                for (Chicken duck : ducks) {
                    if (!duck.isValid()) {
                        continue;
                    }

                    World world = duck.getWorld();
                    for (Player player : world.getPlayers()) {
                        if (player.getLocation().distanceSquared(duck.getLocation()) <= PROXIMITY_DISTANCE * PROXIMITY_DISTANCE) {
                            UUID playerId = player.getUniqueId();
                            Long lastKnockback = knockbackCooldowns.get(playerId);
                            if (lastKnockback == null || currentTime - lastKnockback >= KNOCKBACK_COOLDOWN) {
                                knockbackCooldowns.put(playerId, currentTime);

                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CHICKEN_HURT, 1.0f, 1.0f);

                                Vector direction = player.getLocation().toVector().subtract(duck.getLocation().toVector());
                                if (direction.lengthSquared() > 0.01) {
                                    direction.normalize().multiply(5);
                                    direction.setY(0.5); // Add vertical force
                                    player.setVelocity(direction);
                                }

                                player.sendMessage(ChatColor.RED + "Ой! Ти занадто наблизився до " + duck.getCustomName() + "!");
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    private void spawnDuckParade() {
        paradeActive = true;
        World world = START_POINT.getWorld();
        ducks.clear();

        for (int i = 0; i < 20; i++) {
            double t = (double) i / 19; // Normalize i to [0,1]
            double x = START_POINT.getX() + t * (END_POINT.getX() - START_POINT.getX());
            double y = START_POINT.getY() + t * (END_POINT.getY() - START_POINT.getY());
            double z = START_POINT.getZ() + t * (END_POINT.getZ() - START_POINT.getZ());
            Location spawnLoc = new Location(world, x, y, z);

            if (!world.getBlockAt(spawnLoc.getBlockX(), spawnLoc.getBlockY() - 1, spawnLoc.getBlockZ()).getType().isSolid()) {
                spawnLoc.setY(world.getHighestBlockYAt(spawnLoc.getBlockX(), spawnLoc.getBlockZ()) + 1);
            }

            Chicken duck = world.spawn(spawnLoc, Chicken.class);
            String absurdName = ABSURD_NAMES[random.nextInt(ABSURD_NAMES.length)];
            duck.setCustomName(ChatColor.YELLOW + absurdName);
            duck.setCustomNameVisible(true);
            duck.setAI(true);
            duck.setInvulnerable(true);
            ducks.add(duck);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!duck.isValid() || !paradeActive) {
                        cancel();
                        return;
                    }
                    Vector direction = END_POINT.toVector().subtract(duck.getLocation().toVector());
                    if (direction.lengthSquared() > 0.01) {
                        direction.normalize().multiply(0.1);
                        duck.setVelocity(direction);
                    } else {
                        duck.setVelocity(new Vector(0, 0, 0));
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 2L); // Оновлення кожні 2 тіки
        }

        // Анонс - що забіг курочок почався
        Bukkit.broadcastMessage(ChatColor.GOLD + "Парад Курочок розпочався! Дайте дорогу квоквам!");
    }

    private void despawnDuckParade() {
        paradeActive = false;
        for (Chicken duck : ducks) {
            if (duck.isValid()) {
                duck.remove();
            }
        }
        ducks.clear();
        knockbackCooldowns.clear();
        Bukkit.broadcastMessage(ChatColor.GOLD + "Парад Курочок закінчився!");
    }

    @EventHandler
    public void onDuckDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Chicken) || !ducks.contains(event.getEntity())) {
            return;
        }
        event.setCancelled(true);
    }
}