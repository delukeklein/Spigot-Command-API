package com.lukedeklein.spigot.command.api;

import java.util.List;

import org.bukkit.command.CommandSender;

class Argument <T> implements ArgumentProcessor<T> {

    private final ArgumentProcessor<T> processor;

    private final boolean isOptional;

    public Argument(ArgumentProcessor<T> processor, boolean isOptional) {
        this.processor = processor;
        this.isOptional = isOptional;
    }

    boolean isOptional() {
        return isOptional;
    }

    @Override
    public T process(String argument) {
        return processor.process(argument);
    }

    @Override
    public String argumentError(String argument) {
        return processor.argumentError(argument);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String argument) {
        return processor.tabComplete(sender, argument);
    }
}
