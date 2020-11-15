package com.lukedeklein.spigot.command.api.error;

public interface CommandErrorHandler {

    String unknownErrorMessage();

    String argumentErrorMessage(String argument);

    String syntaxErrorMessage(int from, String label, String[] arguments);

}
