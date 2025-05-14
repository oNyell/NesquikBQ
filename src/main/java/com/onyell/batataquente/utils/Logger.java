package com.onyell.batataquente.utils;

import com.onyell.batataquente.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Logger {
    
    @Getter
    private static boolean debugMode = false;
    
    @Getter
    private static final List<UUID> debugPlayers = new ArrayList<>();
    public static void initialize() {
        debugMode = Main.getInstance().getConfig().getBoolean("debug");
        Main.getInstance().getLogger().info("§eDebug mode: " + (debugMode ? "§aON" : "§cOFF"));
    }
    
    /**
     * Ativa ou desativa o modo de debug
     * @param debug true para ativar, false para desativar
     */
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
        Main.getInstance().getConfig().set("debug", debug);
        Main.getInstance().saveConfig();
    }
    
    /**
     * Adiciona um jogador à lista de jogadores que receberão mensagens de debug
     * @param player Jogador que receberá as mensagens
     */
    public static void addDebugPlayer(Player player) {
        if (!debugPlayers.contains(player.getUniqueId())) {
            debugPlayers.add(player.getUniqueId());
        }
    }
    
    /**
     * Remove um jogador da lista de jogadores que recebem mensagens de debug
     * @param player Jogador a ser removido
     */
    public static void removeDebugPlayer(Player player) {
        debugPlayers.remove(player.getUniqueId());
    }
    
    /**
     * Envia uma mensagem de debug para o console e para todos os jogadores na lista de debug
     * @param message Mensagem a ser enviada
     */
    public static void debug(String message) {
        if (!debugMode) return;
        
        String formattedMessage = "§e[DEBUG] §f" + message;

        Main.getInstance().getLogger().info(formattedMessage);

        for (UUID uuid : debugPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(formattedMessage);
            }
        }
    }
    
    /**
     * Envia uma mensagem de info para o console
     * @param message Mensagem a ser enviada
     */
    public static void info(String message) {
        Main.getInstance().getLogger().info("§b[INFO] §f" + message);
    }
    
    /**
     * Envia uma mensagem de erro para o console
     * @param message Mensagem a ser enviada
     */
    public static void error(String message) {
        Main.getInstance().getLogger().severe("§c[ERROR] §f" + message);
    }
    
    /**
     * Envia uma mensagem de aviso para o console
     * @param message Mensagem a ser enviada
     */
    public static void warning(String message) {
        Main.getInstance().getLogger().warning("§6[WARNING] §f" + message);
    }
} 