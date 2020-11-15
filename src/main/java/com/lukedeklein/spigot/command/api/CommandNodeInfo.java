package com.lukedeklein.spigot.command.api;

import com.lukedeklein.spigot.command.api.annotation.Aliases;
import com.lukedeklein.spigot.command.api.annotation.Command;
import com.lukedeklein.spigot.command.api.annotation.Description;
import com.lukedeklein.spigot.command.api.annotation.Permission;
import com.lukedeklein.spigot.command.api.annotation.SubCommand;
import com.lukedeklein.spigot.command.api.annotation.Usage;
import com.lukedeklein.spigot.command.api.error.CommandErrorHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

class CommandNodeInfo {

    final int index;

    final CommandErrorHandler errorHandler;

    final String label;
    final String usage;
    final String description;
    final String permission;

    final String[] parents;

    final List<String> aliases;

    CommandNodeInfo(BaseCommand baseCommand, CommandErrorHandler errorHandler) {
        var command = baseCommand.getClass().getAnnotation(Command.class);
        var description = baseCommand.getClass().getAnnotation(Description.class);
        var usage = baseCommand.getClass().getAnnotation(Usage.class);
        var permission = baseCommand.getClass().getAnnotation(Permission.class);
        var aliases = baseCommand.getClass().getAnnotation(Aliases.class);

        this.index = 0;
        this.label = command.value().split(" ")[0];
        this.parents = new String[0];

        this.errorHandler = errorHandler;

        this.description = description == null ? "" : description.value();
        this.usage = usage == null ? "" : usage.value();
        this.permission = permission == null ? null : permission.value();
        this.aliases = aliases == null ? List.of() : List.of(aliases.value());
    }

    CommandNodeInfo(Method method, CommandErrorHandler errorHandler) {
        var command = method.getDeclaringClass().getAnnotation(Command.class);
        var subCommand = method.getAnnotation(SubCommand.class);
        var description = method.getAnnotation(Description.class);
        var usage = method.getAnnotation(Usage.class);
        var permission = method.getAnnotation(Permission.class);
        var aliases = method.getAnnotation(Aliases.class);


        var labels = (command.value() + " " + subCommand.value()).split(" ");

        this.index = labels.length - 1;
        this.errorHandler = errorHandler;

        this.label = labels[labels.length - 1];
        this.description = description == null ? "" : description.value();
        this.usage = usage == null ? "" : usage.value();
        this.permission = permission == null ? null : permission.value();
        this.aliases = aliases == null ? List.of() : List.of(aliases.value());
        this.parents = Arrays.copyOfRange(labels, 0, labels.length - 1);

    }

    CommandNodeInfo(int index, CommandNode commandNode) {
        this.index = index;
        this.errorHandler = commandNode;

        this.label = commandNode.parents[index];
        this.description = commandNode.getDescription();
        this.usage = commandNode.getUsage();
        this.permission = commandNode.getPermission();
        this.aliases = List.of();
        this.parents = Arrays.copyOfRange(commandNode.parents, 0, index);
    }
}