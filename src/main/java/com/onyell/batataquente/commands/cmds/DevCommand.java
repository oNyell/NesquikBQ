package com.onyell.batataquente.commands.cmds;

import com.onyell.batataquente.annotations.Commands;
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
            sender.sendMessage("§cVocê não tem permissão para usar esse comando.");
            return;
        }
        
        if (args.length < 1) {
            sender.sendMessage("§cUtilize \"/dev [on/off/add/remove/test]\" para gerenciar o modo debug.");
            return;
        }
        
        String arg = args[0].toLowerCase();
        switch (arg) {
            case "on": {
                Logger.setDebugMode(true);
                sender.sendMessage("§aDebug ativado com sucesso.");
                Logger.debug("Modo debug ativado por " + sender.getName());
                break;
            }
            case "off": {
                Logger.setDebugMode(false);
                sender.sendMessage("§cDebug desativado com sucesso.");
                break;
            }
            case "add": {
                if (args.length < 2) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§cUtilize \"/dev add <jogador>\" para adicionar um jogador à lista de debug.");
                        return;
                    }

                    Player player = (Player) sender;
                    Logger.addDebugPlayer(player);
                    sender.sendMessage("§aVocê foi adicionado à lista de debug. Agora você receberá todas as mensagens de debug no chat.");
                    Logger.debug("Jogador " + player.getName() + " adicionado à lista de debug");
                } else {
                    String playerName = args[1];
                    Player target = Bukkit.getPlayer(playerName);
                    
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage("§cJogador não encontrado ou não está online.");
                        return;
                    }
                    
                    Logger.addDebugPlayer(target);
                    sender.sendMessage("§aJogador " + target.getName() + " foi adicionado à lista de debug.");
                    target.sendMessage("§aVocê foi adicionado à lista de debug. Agora você receberá todas as mensagens de debug no chat.");
                    Logger.debug("Jogador " + target.getName() + " adicionado à lista de debug por " + sender.getName());
                }
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("§cUtilize \"/dev remove <jogador>\" para remover um jogador da lista de debug.");
                        return;
                    }

                    Player player = (Player) sender;
                    Logger.removeDebugPlayer(player);
                    sender.sendMessage("§cVocê foi removido da lista de debug.");
                } else {
                    String playerName = args[1];
                    Player target = Bukkit.getPlayer(playerName);
                    
                    if (target == null || !target.isOnline()) {
                        sender.sendMessage("§cJogador não encontrado ou não está online.");
                        return;
                    }
                    
                    Logger.removeDebugPlayer(target);
                    sender.sendMessage("§cJogador " + target.getName() + " foi removido da lista de debug.");
                    target.sendMessage("§cVocê foi removido da lista de debug.");
                    Logger.debug("Jogador " + target.getName() + " removido da lista de debug por " + sender.getName());
                }
                break;
            }
            case "test": {
                Logger.debug("Este é um teste de mensagem de debug");
                Logger.info("Este é um teste de mensagem de informação");
                Logger.warning("Este é um teste de mensagem de aviso");
                Logger.error("Este é um teste de mensagem de erro");
                sender.sendMessage("§aMensagens de teste enviadas para o console e jogadores na lista de debug.");
                break;
            }
            default: {
                sender.sendMessage("§cUtilize \"/dev [on/off/add/remove/test]\" para gerenciar o modo debug.");
                break;
            }
        }
    }
}
