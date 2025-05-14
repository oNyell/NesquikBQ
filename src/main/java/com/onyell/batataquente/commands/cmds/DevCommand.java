package com.onyell.batataquente.commands.cmds;

import com.onyell.batataquente.annotations.Commands;
import com.onyell.batataquente.interfaces.CommandInterface;
import com.onyell.batataquente.utils.Logger;
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
            sender.sendMessage("§cUtilize \"/dev [on/off/add/remove]\" para gerenciar o modo debug.");
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
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cApenas jogadores podem ser adicionados à lista de debug.");
                    return;
                }
                
                Player player = (Player) sender;
                Logger.addDebugPlayer(player);
                sender.sendMessage("§aVocê foi adicionado à lista de debug. Agora você receberá todas as mensagens de debug no chat.");
                Logger.debug("Jogador " + player.getName() + " adicionado à lista de debug");
                break;
            }
            case "remove": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cApenas jogadores podem ser removidos da lista de debug.");
                    return;
                }
                
                Player player = (Player) sender;
                Logger.removeDebugPlayer(player);
                sender.sendMessage("§cVocê foi removido da lista de debug.");
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
