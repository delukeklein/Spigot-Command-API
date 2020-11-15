package com.lukedeklein.spigot.command.api;

import com.lukedeklein.spigot.command.api.annotation.Optional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Represents a register for registering any {@link ArgumentProcessor}.
 */
public class ArgumentRegister {

    private final Set<ArgumentProcessor<?>> parameters;

    public ArgumentRegister() {
        this.parameters = new HashSet<>();
    }

    public void register(ArgumentProcessor<?> argumentProcessor) {
        boolean result = containsArgumentType(getArgumentType(argumentProcessor.getClass()));

        if (!result) {
            parameters.add(argumentProcessor);
        }
    }

    public boolean containsArgumentType(Class<?> parameterType) {
        return parameterType == String[].class
                || parameterType == CommandSender.class
                || getArgumentProcessorByType(parameterType) != null;
    }

    boolean checkMethod(Method method) {
        if (method == null) {
            return false;
        }

        boolean foundOptional = false;

        for (var parameter : method.getParameters()) {
            var parameterType = parameter.getType();

            if (parameterType == String[].class || parameterType == CommandSender.class) {
                continue;
            }

            if (getArgumentProcessorByType(parameterType) == null) {
                Bukkit.getLogger().warning(
                        "[Zephyr Network Command API] The parameter type '"
                                + parameterType.getName() + "' of method '"
                                + method.getName() + "' in command class '"
                                + method.getDeclaringClass().getName() + "' is not registered"
                );

                return false;
            }

            boolean isOptional = parameter.isAnnotationPresent(Optional.class);

            if(foundOptional && !isOptional) {
                Bukkit.getLogger().warning(
                        "[Zephyr Network Command API] The non optional parameter type '"
                                + parameterType.getName() + "' of method '"
                                + method.getName() + "' in command class '"
                                + method.getDeclaringClass().getName() + "' is placed after an optional parameter"
                );

                return false;
            }

            if(isOptional) {
                foundOptional = true;
            }
        }

        return true;
    }


    Argument<?>[] toArgumentArray(Method method) {
        if (method == null) {
            return new Argument<?>[0];
        }

        var arguments = new ArrayList<ArgumentProcessor<?>>();

        for (var parameter : method.getParameters()) {
            var parameterType = parameter.getType();

            if (parameterType != String[].class && parameterType != CommandSender.class) {
                var argumentProcessor = getArgumentProcessorByType(parameterType);

                if (argumentProcessor == null) {
                    return new Argument<?>[0];
                }

                arguments.add(new Argument(argumentProcessor, parameter.isAnnotationPresent(Optional.class)));
            }
        }

        return arguments.toArray(Argument<?>[]::new);
    }

    private <T> ArgumentProcessor<?> getArgumentProcessorByType(Class<T> argumentType) {
        for (var parameter : parameters) {
            var containedTypeClass = getArgumentType(parameter.getClass());

            if (containedTypeClass == argumentType) {
                return parameter;
            }
        }

        return null;
    }

    private Class<?> getArgumentType(Class<?> argumentProcessorClass) {
        try {
            return argumentProcessorClass.getMethod("process", String.class).getReturnType();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
}