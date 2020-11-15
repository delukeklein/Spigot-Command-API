package com.lukedeklein.spigot.command.api;

import com.lukedeklein.spigot.command.api.annotation.Command;
import com.lukedeklein.spigot.command.api.annotation.Default;
import com.lukedeklein.spigot.command.api.annotation.SubCommand;
import com.lukedeklein.spigot.command.api.error.CommandErrorHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

/**
 * Represents a sort of 'tag' for a class that uses command annotations.
 * <p>
 * Every class using command annotations must implement this.
 */
public abstract class BaseCommand {

    private static void skipDefaultMethodWarning(Command command) {
        Bukkit.getLogger().warning("[Zephyr Network Command API] Skipping default execution of command '"
                + command.value().split(" ")[0]
                + "' because of invalid parameters");
    }

    private static void skipSubCommandWarning(Command command, SubCommand subCommand) {
        Bukkit.getLogger().warning("[Zephyr Network Command API] Skipping command '"
                + command.value().split(" ")[0] + " "
                + subCommand.value()
                + "' because of invalid parameters");
    }

    private static boolean isDefaultMethod(Method method) {
        return !Modifier.isStatic(method.getModifiers())
                && !Modifier.isAbstract(method.getModifiers())
                && method.isAnnotationPresent(Default.class);
    }

    private static boolean isSubCommandMethod(Method method) {
        return !Modifier.isStatic(method.getModifiers())
                && !Modifier.isAbstract(method.getModifiers())
                && method.isAnnotationPresent(SubCommand.class);
    }

    final CommandNode asCommandNode(ArgumentRegister argumentRegister, CommandErrorHandler errorHandler) {
        var commandInfo = new CommandNodeInfo(this, errorHandler);

        for (var method : getClass().getDeclaredMethods()) {
            if (!isDefaultMethod(method)) {
                continue;
            }

            method.setAccessible(true);

            if (argumentRegister.checkMethod(method)) {
                return new CommandMethodNode(commandInfo, this, method, argumentRegister);
            } else {
                skipDefaultMethodWarning(getClass().getAnnotation(Command.class));
            }
        }

        return new CommandArgumentNode(commandInfo);
    }

    final List<CommandNode> getSubCommandNodes(ArgumentRegister argumentRegister, CommandErrorHandler errorHandler) {
        var methods = new ArrayList<CommandNode>();

        for (var method : getClass().getDeclaredMethods()) {
            if (!isSubCommandMethod(method)) {
                continue;
            }

            method.setAccessible(true);

            if (argumentRegister.checkMethod(method)) {
                var commandInfo = new CommandNodeInfo(method, errorHandler);

                methods.add(new CommandMethodNode(commandInfo, this, method, argumentRegister));
            } else {
                skipSubCommandWarning(getClass().getAnnotation(Command.class), method.getAnnotation(SubCommand.class));
            }
        }

        return methods;
    }
}