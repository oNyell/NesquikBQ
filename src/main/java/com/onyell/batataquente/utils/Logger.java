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
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Logger {
    
    @Getter
    private static boolean debugMode = false;
    
    @Getter
    private static final List<UUID> debugPlayers = new CopyOnWriteArrayList<>();

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FILE_NAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static File logDirectory;
    private static File currentLogFile;
    private static PrintWriter logWriter;

    private static boolean loggingEnabled = true;
    private static boolean debugToFile = true;

    public static void initialize() {
        debugMode = Main.getInstance().getConfig().getBoolean("debug", false);
        loggingEnabled = Main.getInstance().getConfig().getBoolean("logging.enabled", true);
        debugToFile = Main.getInstance().getConfig().getBoolean("logging.debug-to-file", true);

        setupLogDirectory();

        if (!loggingEnabled) {
            log("Sistema de logs desativado por configuração");
            return;
        }

        setupCurrentLogFile();
        cleanOldLogs();

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

        String today = LocalDate.now().format(FILE_NAME_FORMAT);
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
            loggingEnabled = false;
        }
    }

    private static void closeLogFile() {
        if (logWriter != null) {
            try {
                logWriter.println();
                logWriter.println("# Sessão encerrada em " + TIME_FORMAT.format(new Date()));
                logWriter.println();
            } finally {
                logWriter.close();
                logWriter = null;
            }
        }
    }
    public static void shutdown() {
        closeLogFile();
    }

    private static void checkLogFileDate() {
        if (!loggingEnabled) return;
        
        LocalDate today = LocalDate.now();
        String expectedFileName = "log-" + today.format(FILE_NAME_FORMAT) + ".log";

        if (currentLogFile == null || !currentLogFile.getName().equals(expectedFileName)) {
            setupCurrentLogFile();
        }
    }

    private static void cleanOldLogs() {
        if (!loggingEnabled || logDirectory == null) return;
        
        int maxDays = Main.getInstance().getConfig().getInt("logging.max-log-days", 7);
        if (maxDays < 1) return;
        LocalDate cutoffDate = LocalDate.now().minus(maxDays, ChronoUnit.DAYS);
        
        File[] logFiles = logDirectory.listFiles((dir, name) -> name.startsWith("log-") && name.endsWith(".log"));
        if (logFiles == null || logFiles.length == 0) return;
        
        int deletedCount = 0;
        for (File file : logFiles) {
            try {
                BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                LocalDate fileDate = attrs.creationTime()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
                
                if (fileDate.isBefore(cutoffDate)) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            } catch (IOException e) {
                try {
                    String fileName = file.getName();
                    String dateStr = fileName.substring(4, 14);
                    LocalDate fileDate = LocalDate.parse(dateStr, FILE_DATE_FORMAT);
                    
                    if (fileDate.isBefore(cutoffDate)) {
                        if (file.delete()) {
                            deletedCount++;
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
        
        if (deletedCount > 0) {
            debug("Limpeza automática: " + deletedCount + " arquivos de log antigos foram removidos");
        }
    }

    private static void logToFile(String type, String message) {
        if (!loggingEnabled) return;

        if (type.equals("DEBUG") && !debugToFile) return;
        
        try {
            checkLogFileDate();
            
            if (logWriter != null) {
                String time = TIME_FORMAT.format(new Date());
                String cleanMessage = message.replaceAll("§[0-9a-fk-or]", "");
                logWriter.println("[" + time + "] [" + type + "] " + cleanMessage);
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§c[BatataQuente] Erro ao registrar log: " + e.getMessage());

            if (loggingEnabled) {
                loggingEnabled = false;
                Bukkit.getConsoleSender().sendMessage("§c[BatataQuente] Logging em arquivo desativado devido a erros");
            }
        }
    }
    
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
        Main.getInstance().getConfig().set("debug", debug);
        Main.getInstance().saveConfig();
        log("Modo debug " + (debugMode ? "ativado" : "desativado"));
    }
    
    public static void addDebugPlayer(Player player) {
        if (!debugPlayers.contains(player.getUniqueId())) {
            debugPlayers.add(player.getUniqueId());
        }
    }
    
    public static void removeDebugPlayer(Player player) {
        debugPlayers.remove(player.getUniqueId());
    }
    
    public static void clearDebugPlayers() {
        debugPlayers.clear();
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
    
    public static List<File> getLogFiles() {
        if (logDirectory == null || !logDirectory.exists()) {
            return new ArrayList<>();
        }
        
        File[] files = logDirectory.listFiles((dir, name) -> name.startsWith("log-") && name.endsWith(".log"));
        return files == null ? new ArrayList<>() : Arrays.asList(files);
    }

    public static List<String> getLogDates() {
        List<File> files = getLogFiles();
        
        return files.stream()
            .map(File::getName)
            .map(name -> name.substring(4, 14))
            .sorted()
            .collect(Collectors.toList());
    }
    
    public static String getLogContent(String date) {
        try {
            File logFile = new File(logDirectory, "log-" + date + ".log");
            if (!logFile.exists()) {
                return null;
            }
            
            return new String(Files.readAllBytes(logFile.toPath()));
        } catch (IOException e) {
            error("Erro ao ler arquivo de log: " + e.getMessage());
            return null;
        }
    }
} 