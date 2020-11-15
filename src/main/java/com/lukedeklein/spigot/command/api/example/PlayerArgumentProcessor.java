package com.lukedeklein.spigot.command.api.example;

import com.lukedeklein.spigot.command.api.ArgumentProcessor;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;

/**
 * {@inheritDoc}
 */
public class PlayerArgumentProcessor implements ArgumentProcessor<Player> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Player process(String argument) {
        return Bukkit.getPlayerExact(argument);
    }

    @Override
    public String argumentError(String argument) {
        return RED + "The player " + GRAY + argument + RED + " is not online or does not exists!";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String argument) {
        return Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .collect(Collectors.toList());
    }
}