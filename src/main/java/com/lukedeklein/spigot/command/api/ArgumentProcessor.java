package com.lukedeklein.spigot.command.api;

import com.lukedeklein.spigot.command.api.error.CommandArgumentErrorHandler;

import java.util.List;

import org.bukkit.command.CommandSender;

/**
 * Represents a interface meant for processing arguments to types which can be registered in the {@link ArgumentRegister}.
 *
 * @param <T> The type the argument is that must be processed.
 */
public interface ArgumentProcessor<T> extends CommandArgumentErrorHandler {

    T process(String argument);

    List<String> tabComplete(CommandSender sender, String argument);

}