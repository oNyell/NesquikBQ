package com.onyell.batataquente.language;

import com.onyell.batataquente.Main;
import com.onyell.batataquente.enums.MessageType;
import com.onyell.batataquente.enums.Messages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Languages {
    
    private static final Map<Messages, String> messagesCache = new HashMap<>();
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)\\}");
    
    public static void loadMessages() {
        messagesCache.clear();
        
        String language = Main.getInstance().getConfig().getString("language", "ptbr");
        
        File languageFile = new File(Main.getInstance().getDataFolder(), "messages_" + language + ".yml");
        FileConfiguration langConfig = null;
        
        if (languageFile.exists()) {
            langConfig = YamlConfiguration.loadConfiguration(languageFile);
        }
        
        for (Messages message : Messages.values()) {
            String path = message.name().toLowerCase().replace("_", ".");
            String defaultMessage = message.getDefaultMessage();
            if (langConfig != null && langConfig.contains(path)) {
                messagesCache.put(message, langConfig.getString(path));
            } else {
                messagesCache.put(message, defaultMessage);
            }
        }
    }
    
    /**
     * Obtém uma mensagem formatada com base em seus placeholders
     * @param message Mensagem a ser obtida
     * @param replacements Pares de chave-valor para substituição (ex: "player", "Steve")
     * @return A mensagem formatada
     */
    public static String getMessage(Messages message, Object... replacements) {
        String msg = messagesCache.getOrDefault(message, message.getDefaultMessage());

        msg = message.getType().getColor() + msg;
        
        if (replacements == null || replacements.length == 0) {
            return msg;
        }

        Map<String, String> replacementsMap = new HashMap<>();
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String key = String.valueOf(replacements[i]);
                String value = String.valueOf(replacements[i + 1]);
                replacementsMap.put(key, value);
            }
        }
        
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(msg);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = replacementsMap.getOrDefault(placeholder, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        
        matcher.appendTail(result);
        return result.toString();
    }
    
    /**
     * Salva todas as mensagens padrão em um arquivo de idioma
     * Útil para criar ou atualizar arquivos de tradução
     * @param language Idioma a ser salvo (ex: "ptbr", "enus")
     */
    public static void saveDefaultMessages(String language) {
        try {
            File dataFolder = Main.getInstance().getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            
            File langFile = new File(dataFolder, "messages_" + language + ".yml");
            boolean isNewFile = !langFile.exists();

            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);

            String header = "# Arquivo de mensagens para o idioma: " + language + "\n" +
                          "# Você pode personalizar todas as mensagens do plugin aqui.\n" +
                          "# Use § para cores. Placeholders: {player}, {sender}, etc.";
            langConfig.options().header(header);

            for (Messages message : Messages.values()) {
                String path = message.name().toLowerCase().replace("_", ".");
                if (!langConfig.contains(path) || isNewFile) {
                    langConfig.set(path, message.getDefaultMessage());
                }
            }

            File tempFile = new File(dataFolder, "messages_" + language + "_temp.yml");
            langConfig.save(tempFile);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(langFile), StandardCharsets.UTF_8)) {
                writer.write(header + "\n");

                YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(tempFile);
                for (Messages message : Messages.values()) {
                    String path = message.name().toLowerCase().replace("_", ".");
                    String value = tempConfig.getString(path);
                    if (value != null) {
                        writer.write(path + ": \"" + value.replace("\"", "\\\"") + "\"\n");
                    }
                }
            }
            tempFile.delete();
            Main.getInstance().getLogger().info("Arquivo de mensagens para " + language + " salvo com sucesso!");
        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Erro ao salvar arquivo de mensagens para " + language + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
