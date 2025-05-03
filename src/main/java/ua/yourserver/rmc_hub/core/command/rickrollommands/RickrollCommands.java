package ua.yourserver.rmc_hub.core.command.rickrollommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.scheduler.BukkitRunnable;
import ua.yourserver.rmc_hub.Main;

public class RickrollCommands implements CommandExecutor {

    private final Main plugin;

    public RickrollCommands(Main plugin) {
        this.plugin = plugin;
    }

    private static final int[][] NOTES = {
            // Extended "Never Gonna Give You Up" melody (~15 seconds)
            // Format: {tick, note (0-24), instrument (0-7)}
            {0, 6, 0},   // F#3 (We're no strangers...)
            {4, 8, 0},   // G#3
            {8, 4, 0},   // D#3
            {12, 8, 0},  // G#3
            {16, 9, 0},  // A#3
            {20, 11, 0}, // B3
            {22, 9, 0},  // A#3
            {24, 8, 0},  // G#3
            {26, 6, 0},  // F#3
            {28, 8, 0},  // G#3
            {32, 4, 0},  // D#3
            // Continuing the melody (to you...)
            {36, 6, 0},  // F#3
            {40, 8, 0},  // G#3
            {44, 9, 0},  // A#3
            {48, 4, 0},  // D#3
            {52, 8, 0},  // G#3
            {56, 9, 0},  // A#3
            {60, 11, 0}, // B3
            {64, 9, 0},  // A#3
            // (Never gonna give you up...)
            {68, 13, 0}, // C#4
            {72, 11, 0}, // B3
            {76, 9, 0},  // A#3
            {80, 8, 0},  // G#3
            {84, 9, 0},  // A#3
            {88, 11, 0}, // B3
            {92, 13, 0}, // C#4
            {96, 11, 0}, // B3
            // (Never gonna let you down...)
            {100, 9, 0}, // A#3
            {104, 8, 0}, // G#3
            {108, 6, 0}, // F#3
            {112, 8, 0}, // G#3
            {116, 9, 0}, // A#3
            {120, 11, 0},// B3
            {124, 9, 0}, // A#3
            {128, 8, 0}, // G#3
            // Repeat part of the melody
            {132, 6, 0}, // F#3
            {136, 8, 0}, // G#3
            {140, 4, 0}, // D#3
            {144, 8, 0}, // G#3
            {148, 9, 0}, // A#3
            {152, 11, 0},// B3
            {156, 9, 0}, // A#3
            {160, 8, 0}, // G#3
            {164, 6, 0}, // F#3
            {168, 8, 0}, // G#3
            {172, 4, 0}, // D#3
            // Add some variation
            {176, 13, 0},// C#4
            {180, 11, 0},// B3
            {184, 9, 0}, // A#3
            {188, 8, 0}, // G#3
            {192, 9, 0}, // A#3
            {196, 11, 0},// B3
            {200, 13, 0},// C#4
            {204, 11, 0},// B3
            {208, 9, 0}, // A#3
            {212, 8, 0}, // G#3
            {216, 6, 0}, // F#3
            {220, 8, 0}, // G#3
            {224, 9, 0}, // A#3
            {228, 11, 0},// B3
            {232, 9, 0}, // A#3
            {236, 8, 0}, // G#3
            {240, 6, 0}, // F#3
            {244, 8, 0}, // G#3
            {248, 4, 0}  // D#3
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Команда доступна тільки для гравців!");
            return true;
        }

        Player player = (Player) sender;
        World world = player.getWorld();
        Location center = player.getLocation().add(0, 2, 0);

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Block block = world.getBlockAt(center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z);
                block.setType(Material.NOTE_BLOCK);
                NoteBlock noteBlock = (NoteBlock) block.getBlockData();
                noteBlock.setNote(new org.bukkit.Note(0));
                noteBlock.setInstrument(Instrument.PIANO);
                block.setBlockData(noteBlock);
            }
        }

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                for (int[] noteData : NOTES) {
                    if (noteData[0] == tick) {
                        int note = noteData[1];
                        int instrument = noteData[2];
                        Block block = world.getBlockAt(
                                center.getBlockX() + (tick % 5) - 2,
                                center.getBlockY(),
                                center.getBlockZ() + ((tick / 5) % 5) - 2
                        );
                        if (block.getType() == Material.NOTE_BLOCK) {
                            NoteBlock noteBlock = (NoteBlock) block.getBlockData();
                            noteBlock.setNote(new org.bukkit.Note(note));
                            noteBlock.setInstrument(getInstrument(instrument));
                            block.setBlockData(noteBlock);
                            block.getState().update(true);
                            world.playSound(block.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
                        }
                    }
                }
                tick++;
                if (tick > NOTES[NOTES.length - 1][0]) {
                    for (int x = -2; x <= 2; x++) {
                        for (int z = -2; z <= 2; z++) {
                            Block block = world.getBlockAt(center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z);
                            block.setType(Material.AIR);
                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        player.sendMessage(ChatColor.GREEN + "Never gonna give you up!");
        return true;
    }

    private Instrument getInstrument(int id) {
        return switch (id) {
            case 1 -> Instrument.BASS_DRUM;
            case 2 -> Instrument.SNARE_DRUM;
            case 3 -> Instrument.STICKS;
            case 4 -> Instrument.BASS_GUITAR;
            case 5 -> Instrument.FLUTE;
            case 6 -> Instrument.BELL;
            case 7 -> Instrument.GUITAR;
            default -> Instrument.PIANO;
        };
    }
}