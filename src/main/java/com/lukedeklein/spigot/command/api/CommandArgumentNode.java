package com.lukedeklein.spigot.command.api;

import org.bukkit.command.CommandSender;

class CommandArgumentNode extends CommandNode {

    CommandArgumentNode(CommandNodeInfo commandNodeInfo) {
        super(commandNodeInfo);
    }

    static CommandArgumentNode dummyFrom(int parentIndex, CommandNode commandNode) {
        var dummyCommandNode = new CommandArgumentNode(new CommandNodeInfo(parentIndex, commandNode));

        dummyCommandNode.append(commandNode);

        return dummyCommandNode;
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) {
        if (args.length <= index) {
            sender.sendMessage(unknownErrorMessage());
            sender.sendMessage(syntaxErrorMessage(index, label, args));
        } else if (hasChildren()) {
            sender.sendMessage(argumentErrorMessage(args[index]));
            sender.sendMessage(syntaxErrorMessage(index, label, args));
        }

        return true;
    }
}