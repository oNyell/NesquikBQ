package com.onyell.batataquente.commands.cmds;

import com.onyell.batataquente.Main;
import com.onyell.batataquente.annotations.Commands;
import com.onyell.batataquente.enums.Messages;
import com.onyell.batataquente.interfaces.CommandInterface;
import com.onyell.batataquente.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@Commands(cmd = "dev", alias = {"devs", "developer"})
public class DevCommand implements CommandInterface {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("onyell.dev")) {
            sender.sendMessage(Messages.GENERAL_NO_PERMISSION.getMessage());
            return;
        }

        if (args.length < 1) {
            sendHelpMessage(sender);
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
            case "reload": {
                Main.getInstance().reloadPlugin();
                sender.sendMessage(Messages.DEV_RELOAD.getMessage());
                Logger.debug(Messages.DEBUG_CONFIG_RELOADED.get("player", sender.getName()));
                break;
            }
            case "add": {
                handleAdd(sender, args);
                break;
            }
            case "remove": {
                handleRemove(sender, args);
                break;
            }
            case "logs": {
                handleLogs(sender, args);
                break;
            }
            case "test": {
                Logger.debug(Messages.TEST_DEBUG.get());
                Logger.info(Messages.TEST_INFO.get());
                Logger.warning(Messages.TEST_WARNING.get());
                Logger.error(Messages.TEST_ERROR.get());
                sender.sendMessage(Messages.DEV_TEST.getMessage());
                break;
            }
            default: {
                sendHelpMessage(sender);
                break;
            }
        }
    }

    private void handleAdd(CommandSender sender, String[] args) {
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
                sender.sendMessage(Messages.GENERAL_PLAYER_NOT_FOUND.getMessage());
                return;
            }
            
            Logger.addDebugPlayer(target);
            sender.sendMessage(Messages.DEV_ADD_OTHER.getMessage("player", target.getName()));
            target.sendMessage(Messages.DEV_ADD_NOTIFY.getMessage());
            Logger.debug(Messages.DEBUG_PLAYER_ADDED.get("player", target.getName(), "sender", sender.getName()));
        }
    }

    private void handleRemove(CommandSender sender, String[] args) {
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
                sender.sendMessage(Messages.GENERAL_PLAYER_NOT_FOUND.getMessage());
                return;
            }
            
            Logger.removeDebugPlayer(target);
            sender.sendMessage(Messages.DEV_REMOVE_OTHER.getMessage("player", target.getName()));
            target.sendMessage(Messages.DEV_REMOVE_NOTIFY.getMessage());
            Logger.debug(Messages.DEBUG_PLAYER_REMOVED.get("player", target.getName(), "sender", sender.getName()));
        }
    }

    private void handleLogs(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Messages.LOG_USAGE.getMessage());
            return;
        }
        
        String subCommand = args[1].toLowerCase();
        switch (subCommand) {
            case "list": {
                listLogs(sender);
                break;
            }
            case "view": {
                if (args.length < 3) {
                    sender.sendMessage(Messages.LOG_VIEW_USAGE.getMessage());
                    return;
                }
                
                String date = args[2];
                viewLog(sender, date);
                break;
            }
            default: {
                sender.sendMessage(Messages.LOG_USAGE.getMessage());
                break;
            }
        }
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§e===== §bBatataQuente Developer Commands §e=====");
        sender.sendMessage("§b/dev on §7- Ativa o modo debug");
        sender.sendMessage("§b/dev off §7- Desativa o modo debug");
        sender.sendMessage("§b/dev add [jogador] §7- Adiciona um jogador à lista de debug");
        sender.sendMessage("§b/dev remove [jogador] §7- Remove um jogador da lista de debug");
        sender.sendMessage("§b/dev reload §7- Recarrega as configurações e mensagens");
        sender.sendMessage("§b/dev logs list §7- Lista os arquivos de log disponíveis");
        sender.sendMessage("§b/dev logs view <data> §7- Visualiza um arquivo de log");
        sender.sendMessage("§b/dev test §7- Envia mensagens de teste para o console");
        sender.sendMessage("§e=====================================");
    }

    private void listLogs(CommandSender sender) {
        List<String> logDates = Logger.getLogDates();
        
        if (logDates.isEmpty()) {
            sender.sendMessage(Messages.LOG_NOT_FOUND.getMessage());
            return;
        }

        sender.sendMessage("§e===== §bArquivos de Log §e=====");
        for (String date : logDates) {
            File logFile = new File(Main.getInstance().getDataFolder(), "logs/log-" + date + ".log");
            long size = logFile.length() / 1024;
            sender.sendMessage("§b" + date + " §7- §e" + size + " KB");
        }
        sender.sendMessage("§e=========================");
        sender.sendMessage("§aUse §e/dev logs view <data> §apara visualizar um arquivo de log.");
    }

    private void viewLog(CommandSender sender, String date) {
        String content = Logger.getLogContent(date);
        
        if (content == null) {
            sender.sendMessage(Messages.LOG_FILE_NOT_FOUND.getMessage("date", date));
            return;
        }

        try {
            List<String> lines = Arrays.asList(content.split("\n"));
            
            sender.sendMessage("§e===== §bLog do dia " + date + " §e=====");
            sender.sendMessage("§7Mostrando as últimas 10 linhas de " + lines.size() + " linhas");
            
            int startIndex = Math.max(0, lines.size() - 10);
            for (int i = startIndex; i < lines.size(); i++) {
                sender.sendMessage("§7" + lines.get(i));
            }
            
            sender.sendMessage("§e=========================");
        } catch (Exception e) {
            sender.sendMessage(Messages.LOG_ERROR.getMessage("error", e.getMessage()));
            Logger.error("Erro ao processar conteúdo do log: " + e.getMessage());
        }
    }
}
