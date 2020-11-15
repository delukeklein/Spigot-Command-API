package com.lukedeklein.spigot.command.api.example;

import com.lukedeklein.spigot.command.api.ArgumentRegister;
import com.lukedeklein.spigot.command.api.CommandRegister;
import com.lukedeklein.spigot.command.api.error.CommandErrorHandler;

import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        CommandErrorHandler errorHandler = new ErrorHandler();

        ArgumentRegister argumentRegister = new ArgumentRegister();

        argumentRegister.register(new PlayerArgumentProcessor());
        argumentRegister.register(new GameModeArgumentProcessor());

        CommandRegister commandRegister = new CommandRegister(argumentRegister, errorHandler);

        //command will be /test:gm or just /gm
        commandRegister.register("test", new GameModeCommand());
    }
}
