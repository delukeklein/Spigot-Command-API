package com.lukedeklein.spigot.command.api;

import com.lukedeklein.spigot.command.api.annotation.Optional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class CommandMethodNode extends CommandNode {

    private final int optionalIndex;

    private final boolean exceed;

    private final BaseCommand baseCommand;
    private final Method method;

    private final Object[] methodArguments;

    private final Class<?>[] parameterTypes;
    private final Argument<?>[] arguments;

    CommandMethodNode(CommandNodeInfo commandNodeInfo, BaseCommand baseCommand, Method method, ArgumentRegister argumentRegister) {
        super(commandNodeInfo);

        this.baseCommand = baseCommand;
        this.method = method;

        this.methodArguments = new Object[method.getParameterCount()];

        this.parameterTypes = method.getParameterTypes();
        this.arguments = argumentRegister.toArgumentArray(method);

        boolean exceed = false;

        int optionalIndex = -1;

        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            var parameterType = parameter.getType();

            if (parameterType == String[].class) {
                exceed = true;
            }

            if (optionalIndex == -1 && parameter.isAnnotationPresent(Optional.class)) {
                optionalIndex = i;
            }
        }

        this.optionalIndex = optionalIndex == -1 ? arguments.length : optionalIndex;
        this.exceed = exceed;
    }

    @Override
    protected boolean onExecute(CommandSender sender, String label, String[] args) {
        if (args.length <= index + optionalIndex) {
            sender.sendMessage(unknownErrorMessage());
            sender.sendMessage(syntaxErrorMessage(args.length, label, args));

            return true;
        } else if (args.length > index + optionalIndex && !exceed) {
            sender.sendMessage(unknownErrorMessage());
            sender.sendMessage(syntaxErrorMessage(optionalIndex, label, args));

            return true;
        }

        if (args.length < index && hasChildren() && !exceed) {
            sender.sendMessage(argumentErrorMessage(args[index]));
            sender.sendMessage(syntaxErrorMessage(index, label, args));
            return true;
        }

        for (int i = 0, j = 0; i < methodArguments.length; i++) {
            var parameterType = parameterTypes[i];

            if (parameterType == CommandSender.class) {
                methodArguments[i] = sender;
            } else if (parameterType == String[].class) {
                methodArguments[i] = args.length > index + arguments.length
                        ? Arrays.copyOfRange(args, index + arguments.length, args.length)
                        : new String[0];
            } else if (j < arguments.length && index + j < args.length) {
                var arg = args[index + j];
                var argument = arguments[j];
                var methodArgument = argument.process(arg);

                if (methodArgument != null) {
                    methodArguments[i] = methodArgument;
                } else {
                    sender.sendMessage(argument.argumentError(arg));
                    sender.sendMessage(syntaxErrorMessage(index + j, label, args));

                    return false;
                }

                j++;
            }
        }

        try {
            method.invoke(baseCommand, methodArguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.getCause().printStackTrace();
        }

        return true;
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        int argumentIndex = args.length - index - 1;

        List<String> tabComplete = new ArrayList<>();

        if (argumentIndex >= 0 && argumentIndex < arguments.length //&& arguments.length != 0
        ) {
            var argument = args[args.length - 1];

            for (var string : this.arguments[argumentIndex].tabComplete(sender, argument)) {
                if (StringUtil.startsWithIgnoreCase(string, argument)) {
                    tabComplete.add(string);
                }
            }
        }

        return tabComplete;
    }
}