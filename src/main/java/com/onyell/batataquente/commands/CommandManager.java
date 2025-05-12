package com.onyell.batataquente.commands;

import com.onyell.batataquente.Main;
import com.onyell.batataquente.annotations.Commands;
import com.onyell.batataquente.interfaces.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

public class CommandManager extends Command {

    public static void setupCommands() {
        Reflections reflections = new Reflections(Main.getInstance().getClass().getPackage().getName());
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Commands.class);
        for (Class<?> clazz : annotatedClasses) {
            if (CommandInterface.class.isAssignableFrom(clazz)) {
                Commands annotation = clazz.getAnnotation(Commands.class);
                String name = annotation.cmd();
                String[] aliases = annotation.alias();
                Boolean onlyPlayer = annotation.onlyPlayer();

                try {
                    Constructor<?> constructor = clazz.getConstructor();
                    CommandInterface cmd = (CommandInterface) constructor.newInstance();
                    new CommandManager(name, cmd, onlyPlayer, aliases);
                } catch (Exception e) {
                    Main.getInstance().getLogger().info("Failed to register command: " + e);
                }
            }
        }
    }

    private final CommandInterface cmd;
    private final Boolean onlyPlayer;

    public CommandManager(String name, CommandInterface cmd, Boolean onlyPlayer, String[] aliases) {
        super(name);
        this.cmd = cmd;
        this.onlyPlayer = onlyPlayer;
        setAliases(Arrays.asList(aliases));

        try {
            SimpleCommandMap map = (SimpleCommandMap) Main.getInstance().getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Main.getInstance().getServer());
            map.register(getName(), Main.getInstance().getName(), this);
        } catch (Exception e) {
            Main.getInstance().getLogger().info("Failed to get command map: " + e);
            return;
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) && onlyPlayer) {
            sender.sendMessage("You must be a player to use this command.");
            return false;
        }
        cmd.execute(sender, label, args);
        return true;
    }

}
