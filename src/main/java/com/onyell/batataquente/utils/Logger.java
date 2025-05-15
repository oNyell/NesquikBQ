package com.onyell.batataquente.utils;

import com.onyell.batataquente.Main;
import com.onyell.batataquente.enums.MessageType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Logger {
    
    @Getter
    private static boolean debugMode = false;
    
    @Getter
    private static final List<UUID> debugPlayers = new ArrayList<>();
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    private static File logDirectory;
    private static File currentLogFile;
    private static PrintWriter logWriter;
    public static void initialize() {
        debugMode = Main.getInstance().getConfig().getBoolean("debug");

        setupLogDirectory();

        setupCurrentLogFile();

        log("§eDebug mode: " + (debugMode ? "§aON" : "§cOFF"));
        log("§bSistema de logs inicializado com sucesso");
    }

    private static void setupLogDirectory() {
        logDirectory = new File(Main.getInstance().getDataFolder(), "logs");
        if (!logDirectory.exists()) {
            if (logDirectory.mkdirs()) {
                Main.getInstance().getLogger().info("Diretório de logs criado com sucesso");
            } else {
                Main.getInstance().getLogger().severe("Falha ao criar diretório de logs");
            }
        }
    }

    private static void setupCurrentLogFile() {
        closeLogFile();
        
        String today = FILE_DATE_FORMAT.format(new Date());
        currentLogFile = new File(logDirectory, "log-" + today + ".log");
        
        try {
            logWriter = new PrintWriter(new FileWriter(currentLogFile, true), true);

            if (currentLogFile.length() == 0) {
                logWriter.println("# Log do plugin BatataQuente - " + today);
                logWriter.println("# Formato: [HORA] [TIPO] Mensagem");
                logWriter.println("---------------------------------------------");
            }

            logWriter.println();
            logWriter.println("# Nova sessão iniciada em " + TIME_FORMAT.format(new Date()));
            logWriter.println();
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("Falha ao abrir arquivo de log: " + e.getMessage());
        }
    }

    private static void closeLogFile() {
        if (logWriter != null) {
            logWriter.println();
            logWriter.println("# Sessão encerrada em " + TIME_FORMAT.format(new Date()));
            logWriter.println();
            logWriter.close();
            logWriter = null;
        }
    }

    public static void shutdown() {
        closeLogFile();
    }

    private static void checkLogFileDate() {
        if (currentLogFile != null) {
            String today = FILE_DATE_FORMAT.format(new Date());
            String currentFileName = currentLogFile.getName();
            String expectedFileName = "log-" + today + ".log";
            
            if (!currentFileName.equals(expectedFileName)) {
                setupCurrentLogFile();
            }
        } else {
            setupCurrentLogFile();
        }
    }

    private static void logToFile(String type, String message) {
        try {
            checkLogFileDate();
            
            if (logWriter != null) {
                String time = TIME_FORMAT.format(new Date());
                String cleanMessage = message.replaceAll("§[0-9a-fk-or]", "");
                logWriter.println("[" + time + "] [" + type + "] " + cleanMessage);
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§c[BatataQuente] Erro ao registrar log: " + e.getMessage());
        }
    }

    public static void setDebugMode(boolean debug) {
        debugMode = debug;
        Main.getInstance().getConfig().set("debug", debug);
        Main.getInstance().saveConfig();
        log("Debug mode set to: " + (debugMode ? "ON" : "OFF"));
    }

    public static void addDebugPlayer(Player player) {
        if (!debugPlayers.contains(player.getUniqueId())) {
            debugPlayers.add(player.getUniqueId());
        }
    }

    public static void removeDebugPlayer(Player player) {
        debugPlayers.remove(player.getUniqueId());
    }

    public static void log(String message) {
        logToFile("LOG", message);
    }

    public static void debug(String message) {
        if (!debugMode) return;
        
        String formattedMessage = MessageType.DEBUG.getColor() + "[DEBUG] §f" + message;

        Main.getInstance().getLogger().info(formattedMessage);

        logToFile("DEBUG", message);

        for (UUID uuid : debugPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(formattedMessage);
            }
        }
    }

    public static void info(String message) {
        String formattedMessage = MessageType.INFO.getColor() + "[INFO] §f" + message;
        Main.getInstance().getLogger().info(formattedMessage);
        logToFile("INFO", message);
    }

    public static void error(String message) {
        String formattedMessage = MessageType.ERROR.getColor() + "[ERROR] §f" + message;
        Main.getInstance().getLogger().severe(formattedMessage);
        logToFile("ERROR", message);
    }

    public static void warning(String message) {
        String formattedMessage = MessageType.WARNING.getColor() + "[WARNING] §f" + message;
        Main.getInstance().getLogger().warning(formattedMessage);
        logToFile("WARNING", message);
    }
} 