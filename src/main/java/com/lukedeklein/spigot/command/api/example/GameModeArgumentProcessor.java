package com.lukedeklein.spigot.command.api.example;

import com.lukedeklein.spigot.command.api.ArgumentProcessor;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

public class GameModeArgumentProcessor implements ArgumentProcessor<GameMode> {

    private static final List<String> TAB_COMPLETE = List.of("0", "1", "2", "3", "survival", "creative", "adventure", "spectator");

    @Override
    public GameMode process(String argument) {
        switch (argument) {
            case "0":
                return GameMode.SURVIVAL;
            case "1":
                return GameMode.CREATIVE;
            case "2":
                return GameMode.ADVENTURE;
            case "3":
                return GameMode.SPECTATOR;
        }

        try {
            return GameMode.valueOf(argument.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String argument) {
        return TAB_COMPLETE;
    }

    @Override
    public String argumentError(String argument) {
        return RED + "The game mode " + GRAY + argument + RED + " does not exists!";
    }
}
