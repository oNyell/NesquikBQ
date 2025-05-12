package com.onyell.batataquente.commands.cmds;

import com.onyell.batataquente.annotations.Commands;
import com.onyell.batataquente.interfaces.CommandInterface;
import org.bukkit.command.CommandSender;

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
            sender.sendMessage("§cUtilize \"/dev [on/off]\" para ativar ou desativar o debug.");
            return;
        }
        String arg = args[0].toLowerCase();
        switch (arg) {
            case "on": {
                sender.sendMessage("§aDebug ativado com sucesso.");
                //TODO: Ativar o debug utilizando a config.yml (Ao reiniciar o servidor, ela voltará para o padrão {onDebug: false})
                break;
            }
            case "off": {
                sender.sendMessage("§cDebug desativado com sucesso.");
                //TODO: Desativar o debug utilizando a config.yml (Ao reiniciar o servidor, ela voltará para o padrão {onDebug: true})
                break;
            }
            default: {
                sender.sendMessage("§cUtilize \"/dev [on/off]\" para ativar ou desativar o debug.");
                break;
            }
        }
    }
}
