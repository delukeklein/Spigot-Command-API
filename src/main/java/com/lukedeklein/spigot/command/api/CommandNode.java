package com.lukedeklein.spigot.command.api;

import com.lukedeklein.spigot.command.api.error.CommandErrorHandler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.util.StringUtil;

class CommandNode extends Command implements CommandErrorHandler {

    protected final int index;

    protected final String[] parents;

    private final CommandErrorHandler errorHandler;

    private final List<CommandNode> children;

    protected CommandNode(CommandNodeInfo commandNodeInfo) {
        super(commandNodeInfo.label, commandNodeInfo.description, commandNodeInfo.usage, commandNodeInfo.aliases);

        this.index = commandNodeInfo.index;
        this.parents = commandNodeInfo.parents;

        this.errorHandler = commandNodeInfo.errorHandler;

        this.children = new ArrayList<>();

        setPermission(commandNodeInfo.permission);
    }

    static CommandNode of(BaseCommand baseCommand, ArgumentRegister argumentRegister, CommandErrorHandler errorHandler) {
        var baseCommandNode = baseCommand.asCommandNode(argumentRegister, errorHandler);
        var subCommandNodes = baseCommand.getSubCommandNodes(argumentRegister, errorHandler);

        for (var subCommandNode : subCommandNodes) {
            baseCommandNode.append(subCommandNode);
        }

        return baseCommandNode;
    }

    @Override
    public final boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length > index) {
            for (var child : children) {
                if (child.checkPermission(sender) && child.hasLabel(args[index])) {
                    return child.execute(sender, label, args);
                }
            }
        }

        return onExecute(sender, label, args);
    }

    @Override
    public final List<String> tabComplete(CommandSender sender, String label, String[] args) {
        var tabComplete = new ArrayList<String>();

        if (args.length > index) {
            for (var child : children) {
                if (!child.checkPermission(sender)) {
                    continue;
                }

                var argument = args[index];

                if (child.hasLabel(argument)) {
                    return child.tabComplete(sender, label, args);
                }

                if (StringUtil.startsWithIgnoreCase(child.getLabel(), argument)) {
                    tabComplete.add(child.getLabel());
                }

                for (var alias : child.getAliases()) {
                    if (StringUtil.startsWithIgnoreCase(alias, argument)) {
                        tabComplete.add(alias);
                    }
                }
            }

            tabComplete.addAll(onTabComplete(sender, label, args));
        }

        return tabComplete;
    }

    @Override
    public final String unknownErrorMessage() {
        return errorHandler.unknownErrorMessage();
    }

    @Override
    public final String argumentErrorMessage(String argument) {
        return errorHandler.argumentErrorMessage(argument);
    }

    @Override
    public final String syntaxErrorMessage(int from, String label, String[] arguments) {
        return errorHandler.syntaxErrorMessage(from, label, arguments);
    }

    protected void append(CommandNode commandNode) {
        if (commandNode instanceof CommandMethodNode) {
            for (var child : children) {
                if (index == child.index && hasLabel(child.getLabel()) && child instanceof CommandArgumentNode) {
                    commandNode.children.addAll(child.children);

                    children.set(children.indexOf(child), commandNode);

                    return;
                }
            }
        }

        if (commandNode.index > index + 1) {
            for (var child : children) {
                boolean isParentNode = child.isParentOf(commandNode);

                if (isParentNode) {
                    child.append(commandNode);

                    return;
                }
            }

            children.add(CommandArgumentNode.dummyFrom(index + 1, commandNode));
        } else {
            children.add(commandNode);
        }
    }

    protected boolean hasChildren() {
        return !children.isEmpty();
    }

    protected boolean checkPermission(Permissible permissible) {
        return getPermission() == null || permissible.hasPermission(getPermission());
    }

    protected boolean isParentOf(CommandNode commandNode) {
        return hasLabel(commandNode.parents[index]);
    }

    protected boolean onExecute(CommandSender sender, String label, String[] args) {
        return true;
    }

    protected List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return new ArrayList<>();
    }

    private boolean hasLabel(String label) {
        if (label.equals(getLabel())) {
            return true;
        }

        for (var alias : getAliases()) {
            if (alias.equals(label)) {
                return true;
            }
        }

        return false;
    }
}