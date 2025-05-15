package com.onyell.batataquente.utils;

import com.onyell.batataquente.Main;
import com.onyell.batataquente.language.Languages;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;


public class ConfigManager {

    private static final Map<String, Object> DEFAULT_CONFIG = new HashMap<>();
    
    static {
        DEFAULT_CONFIG.put("language", "ptbr");
        DEFAULT_CONFIG.put("debug", false);
        DEFAULT_CONFIG.put("prefix-enabled", true);

        DEFAULT_CONFIG.put("logging.enabled", true);
        DEFAULT_CONFIG.put("logging.debug-to-file", true);
        DEFAULT_CONFIG.put("logging.max-log-days", 7);
    }

    public static void reloadAll() {
        Logger.debug("Iniciando recarga completa de configurações");
        
        reloadConfig();
        validateConfig();
        reloadMessages();

        Logger.initialize();
        
        Logger.info("Todas as configurações foram recarregadas com sucesso");
    }

    public static void reloadConfig() {
        Logger.debug("Recarregando configurações do arquivo config.yml");
        Main.getInstance().reloadConfig();
    }

    public static void reloadMessages() {
        Logger.debug("Recarregando arquivos de mensagens");
        Languages.loadMessages();
    }

    public static void validateConfig() {
        Logger.debug("Validando configurações");
        
        FileConfiguration config = Main.getInstance().getConfig();
        boolean needsSave = false;

        for (Map.Entry<String, Object> entry : DEFAULT_CONFIG.entrySet()) {
            String key = entry.getKey();
            Object defaultValue = entry.getValue();
            
            if (!config.contains(key)) {
                config.set(key, defaultValue);
                needsSave = true;
                Logger.debug("Configuração ausente detectada: " + key + ", definindo para valor padrão: " + defaultValue);
            }
        }

        if (!(config.get("debug") instanceof Boolean)) {
            config.set("debug", DEFAULT_CONFIG.get("debug"));
            needsSave = true;
            Logger.warning("Tipo inválido para 'debug', restaurando valor padrão");
        }
        
        if (!(config.get("language") instanceof String)) {
            config.set("language", DEFAULT_CONFIG.get("language"));
            needsSave = true;
            Logger.warning("Tipo inválido para 'language', restaurando valor padrão");
        }

        int maxLogDays = config.getInt("logging.max-log-days", 7);
        if (maxLogDays < 1 || maxLogDays > 90) {
            config.set("logging.max-log-days", 7);
            needsSave = true;
            Logger.warning("Valor inválido para 'logging.max-log-days', restaurando para 7");
        }

        if (needsSave) {
            Main.getInstance().saveConfig();
            Logger.info("Configurações foram validadas e corrigidas");
        }
    }

    public static void createDefaultConfig() {
        Main.getInstance().saveDefaultConfig();
        validateConfig();
    }
} 