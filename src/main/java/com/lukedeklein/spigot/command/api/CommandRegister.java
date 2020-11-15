package com.lukedeklein.spigot.command.api;

import com.lukedeklein.spigot.command.api.annotation.Command;
import com.lukedeklein.spigot.command.api.error.CommandErrorHandler;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

public class CommandRegister {

    private final CommandMap commandMap;

    private final ArgumentRegister argumentRegister;

    private final CommandErrorHandler errorHandler;

    public CommandRegister(ArgumentRegister argumentRegister, CommandErrorHandler errorHandler) {
        this.argumentRegister = argumentRegister;
        this.errorHandler = errorHandler;

        this.commandMap = getBukkitCommandMap();
    }

    public void register(String prefix, BaseCommand baseCommand) {
        if (commandMap == null) {
            return;
        }

        var commandClass = baseCommand.getClass();

        if (commandClass.isAnnotationPresent(Command.class)) {
            commandMap.register(prefix, CommandNode.of(baseCommand, argumentRegister, errorHandler));
        } else {
            Bukkit.getLogger().warning("[Zephyr Network Command API] Could not register '"
                    + commandClass.getName()
                    + "' because it is not annotated with the Command annotation");
        }
    }

    private CommandMap getBukkitCommandMap() {
        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            field.setAccessible(true);

            return (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().severe("[Zephyr Network Command API] Could not acquire Bukkit's Command Map");
            Bukkit.getLogger().severe("[Zephyr Network Command API] Registering commands will not work");

            return null;
        }
    }
}