package com.lukedeklein.spigot.command.api.example;

import com.lukedeklein.spigot.command.api.error.CommandErrorHandler;

import java.util.Arrays;

import org.bukkit.ChatColor;

public class ErrorHandler implements CommandErrorHandler {

    private static final String UNKNOWN_INVALID_COMMAND = ChatColor.RED + "Unknown or incomplete command, see below for error";
    private static final String INCORRECT_ARGUMENT = ChatColor.RED + "Incorrect argument for command";
    private static final String INVALID_HIGHLIGHT = ChatColor.RED + "" + ChatColor.UNDERLINE;
    private static final String BLANK_SPACE = ChatColor.RESET + " ";
    private static final String HERE_HIGHLIGHT = ChatColor.RED + "" + ChatColor.ITALIC + "<--[HERE]";

    @Override
    public String argumentErrorMessage(String argument) {
        return INCORRECT_ARGUMENT;
    }

    @Override
    public String unknownErrorMessage() {
        return UNKNOWN_INVALID_COMMAND;
    }

    @Override
    public String syntaxErrorMessage(int from, String label, String[] arguments) {
        var error = new StringBuilder();

        error.append(ChatColor.GRAY);

        error.append(label);

        for (var string : Arrays.copyOfRange(arguments, 0, from)) {
            error.append(BLANK_SPACE);
            error.append(ChatColor.GRAY);
            error.append(string);
        }

        for (var string : Arrays.copyOfRange(arguments, from, arguments.length > from ? from + 1 : arguments.length)) {
            error.append(BLANK_SPACE);
            error.append(INVALID_HIGHLIGHT);
            error.append(string);
        }

        error.append(HERE_HIGHLIGHT);

        return error.toString();
    }
}
