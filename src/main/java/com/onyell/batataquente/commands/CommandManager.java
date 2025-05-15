package com.onyell.batataquente.commands;

import com.onyell.batataquente.Main;
import com.onyell.batataquente.annotations.Commands;
import com.onyell.batataquente.interfaces.CommandInterface;
import com.onyell.batataquente.utils.Logger;
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
        Logger.debug("Iniciando registro de comandos...");
        
        Reflections reflections = new Reflections(Main.getInstance().getClass().getPackage().getName());
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Commands.class);
        
        Logger.debug("Encontrados " + annotatedClasses.size() + " comandos para registrar");
        
        for (Class<?> clazz : annotatedClasses) {
            if (CommandInterface.class.isAssignableFrom(clazz)) {
                Commands annotation = clazz.getAnnotation(Commands.class);
                String name = annotation.cmd();
                String[] aliases = annotation.alias();
                Boolean onlyPlayer = annotation.onlyPlayer();

                try {
                    Logger.debug("Registrando comando: " + name + " (Classe: " + clazz.getSimpleName() + ")");
                    
                    Constructor<?> constructor = clazz.getConstructor();
                    CommandInterface cmd = (CommandInterface) constructor.newInstance();
                    new CommandManager(name, cmd, onlyPlayer, aliases);
                    
                    Logger.debug("Comando " + name + " registrado com sucesso!");
                } catch (Exception e) {
                    Logger.error("Falha ao registrar comando " + name + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        Logger.debug("Registro de comandos concluído");
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
            
            Logger.debug("Comando /" + name + " mapeado com aliases: " + Arrays.toString(aliases));
        } catch (Exception e) {
            Logger.error("Falha ao obter mapa de comandos: " + e.getMessage());
            return;
        }
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) && onlyPlayer) {
            sender.sendMessage("You must be a player to use this command.");
            Logger.debug("Tentativa de executar comando somente para jogadores por não-jogador: " + label);
            return false;
        }

        String senderName = (sender instanceof Player) ? sender.getName() : "CONSOLE";
        String argsStr = String.join(" ", args);
        Logger.debug("Comando executado: /" + label + " " + argsStr + " (por " + senderName + ")");

        long startTime = System.currentTimeMillis();
        cmd.execute(sender, label, args);
        long endTime = System.currentTimeMillis();

        Logger.debug("Comando /" + label + " executado em " + (endTime - startTime) + "ms");
        
        return true;
    }
}
