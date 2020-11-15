package com.lukedeklein.spigot.command.api.example;

import com.lukedeklein.spigot.command.api.BaseCommand;
import com.lukedeklein.spigot.command.api.annotation.Command;
import com.lukedeklein.spigot.command.api.annotation.Default;
import com.lukedeklein.spigot.command.api.annotation.Description;
import com.lukedeklein.spigot.command.api.annotation.Optional;
import com.lukedeklein.spigot.command.api.annotation.Permission;
import com.lukedeklein.spigot.command.api.annotation.Usage;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("gm")
@Description("Sets game mode of player.")
@Permission("zephyr.chat")
@Usage("/message <player> <message>")
public class GameModeCommand extends BaseCommand {

    @Default
    private void setGameMode(CommandSender sender, GameMode gameMode, @Optional Player player) {
        (player == null && sender instanceof Player ? (Player) sender : player).setGameMode(gameMode);
    }
}