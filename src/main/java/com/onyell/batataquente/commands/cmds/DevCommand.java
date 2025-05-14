package com.onyell.batataquente.commands.cmds;

import com.onyell.batataquente.annotations.Commands;
import com.onyell.batataquente.enums.Messages;
import com.onyell.batataquente.interfaces.CommandInterface;
import com.onyell.batataquente.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Isso é um comando de DEBUG, ao ativa-lo, o console começará a dar o debug das funções
 */
@Commands(cmd = "dev", alias = {"devs", "developer"})
public class DevCommand implements CommandInterface {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("onyell.dev")) {
            sender.sendMessage(Messages.NO_PERMISSION.getMessage());
            return;
        }
        
        if (args.length < 1) {
            sender.sendMessage(Messages.DEV_HELP.getMessage());
            return;
        }
        
        String arg = args[0].toLowerCase();
        switch (arg) {
            case "on": {
                Logger.setDebugMode(true);
                sender.sendMessage(Messages.DEV_ON.getMessage());
                Logger.debug(Messages.DEBUG_ENABLED_BY.get("player", sender.getName()));
                break;
            }
            case "off": {
                Logger.setDebugMode(false);
                sender.sendMessage(Messages.DEV_OFF.getMessage());
                break;
            }
            case "add": {
                if (args.length < 2) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Messages.DEV_ADD_USAGE.getMessage());
                        return;
                    }

                    Player player = (Player) sender;
                    Logger.addDebugPlayer(player);
                    sender.sendMessage(Messages.DEV_ADD_SELF.getMessage());
                    Logger.debug(Messages.DEBUG_PLAYER_ADDED.get("player", player.getName(), "sender", "si mesmo"));
                } else {
                    String playerName = args[1];
                    Player target = Bukkit.getPlayer(playerName);
                    
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage(Messages.PLAYER_NOT_FOUND.getMessage());
                        return;
                    }
                    
                    Logger.addDebugPlayer(target);
                    sender.sendMessage(Messages.DEV_ADD_OTHER.getMessage("player", target.getName()));
                    target.sendMessage(Messages.DEV_ADD_NOTIFY.getMessage());
                    Logger.debug(Messages.DEBUG_PLAYER_ADDED.get("player", target.getName(), "sender", sender.getName()));
                }
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Messages.DEV_REMOVE_USAGE.getMessage());
                        return;
                    }

                    Player player = (Player) sender;
                    Logger.removeDebugPlayer(player);
                    sender.sendMessage(Messages.DEV_REMOVE_SELF.getMessage());
                } else {
                    String playerName = args[1];
                    Player target = Bukkit.getPlayer(playerName);
                    
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage(Messages.PLAYER_NOT_FOUND.getMessage());
                        return;
                    }
                    
                    Logger.removeDebugPlayer(target);
                    sender.sendMessage(Messages.DEV_REMOVE_OTHER.getMessage("player", target.getName()));
                    target.sendMessage(Messages.DEV_REMOVE_NOTIFY.getMessage());
                    Logger.debug(Messages.DEBUG_PLAYER_REMOVED.get("player", target.getName(), "sender", sender.getName()));
                }
                break;
            }
            case "test": {
                Logger.debug(Messages.DEBUG_TEST.get());
                Logger.info(Messages.INFO_TEST.get());
                Logger.warning(Messages.WARNING_TEST.get());
                Logger.error(Messages.ERROR_TEST.get());
                sender.sendMessage(Messages.DEV_TEST.getMessage());
                break;
            }
            default: {
                sender.sendMessage(Messages.DEV_HELP.getMessage());
                break;
            }
        }
    }
}
