package com.onyell.batataquente.language;

import com.onyell.batataquente.Main;
import com.onyell.batataquente.enums.MessageType;
import com.onyell.batataquente.enums.Messages;
import com.onyell.batataquente.utils.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        Logger.debug("Carregando mensagens em: " + language);
        
        File languageFile = new File(Main.getInstance().getDataFolder(), "messages_" + language + ".yml");
        
        if (!languageFile.exists()) {
            Logger.warning("Arquivo de idioma não encontrado. Criando arquivo padrão.");
            saveDefaultMessages(language);
        }
        
        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(languageFile);
        
        int loaded = 0;
        for (Messages message : Messages.values()) {
            String path = getConfigPath(message);
            String defaultMessage = message.getDefaultMessage();
            
            if (langConfig.contains(path)) {
                messagesCache.put(message, langConfig.getString(path));
                loaded++;
            } else {
                messagesCache.put(message, defaultMessage);
                Logger.debug("Mensagem não encontrada no arquivo: " + path);
            }
        }
        
        Logger.info("Sistema de mensagens carregado: " + loaded + " mensagens encontradas.");
    }
    
    /**
     * Converte o nome da enum para o caminho no arquivo de configuração
     * @param message Mensagem a ser convertida
     * @return Caminho na configuração
     */
    private static String getConfigPath(Messages message) {
        String name = message.name();
        String category = name.split("_")[0].toLowerCase();
        String key = name.substring(name.indexOf("_") + 1).toLowerCase();

        if (name.equals("PREFIX")) {
            return "system.prefix";
        }

        if (name.indexOf("_") == -1) {
            return "general." + name.toLowerCase();
        }
        
        return category + "." + key;
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
        File dataFolder = Main.getInstance().getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            Logger.error("Falha ao criar pasta de dados do plugin.");
            return;
        }
        
        File langFile = new File(dataFolder, "messages_" + language + ".yml");
        boolean isNewFile = !langFile.exists();
        
        try {
            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
            
            String header = "# Arquivo de mensagens para o idioma: " + language + "\n" +
                          "# Você pode personalizar todas as mensagens do plugin aqui.\n" +
                          "# Use § para cores. Placeholders: {player}, {sender}, etc.";
            langConfig.options().header(header);

            Map<String, Map<String, String>> categories = new HashMap<>();
            
            for (Messages message : Messages.values()) {
                String path = getConfigPath(message);
                String defaultValue = message.getDefaultMessage();
                
                if (!langConfig.contains(path) || isNewFile) {
                    String[] parts = path.split("\\.");
                    String category = parts[0];
                    String key = parts.length > 1 ? parts[1] : "";
                    
                    Map<String, String> categoryMap = categories.computeIfAbsent(category, k -> new HashMap<>());
                    categoryMap.put(key, defaultValue);
                    
                    langConfig.set(path, defaultValue);
                }
            }

            File tempFile = new File(dataFolder, "messages_" + language + "_temp.yml");
            langConfig.save(tempFile);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(langFile), StandardCharsets.UTF_8)) {
                writer.write(header + "\n\n");

                for (Map.Entry<String, Map<String, String>> categoryEntry : categories.entrySet()) {
                    String category = categoryEntry.getKey();
                    Map<String, String> messages = categoryEntry.getValue();
                    
                    writer.write("# Mensagens de " + category + "\n");
                    
                    for (Map.Entry<String, String> messageEntry : messages.entrySet()) {
                        String key = messageEntry.getKey();
                        String value = messageEntry.getValue().replace("\"", "\\\"");
                        
                        if (key.isEmpty()) {
                            writer.write(category + ": \"" + value + "\"\n");
                        } else {
                            writer.write(category + "." + key + ": \"" + value + "\"\n");
                        }
                    }
                    
                    writer.write("\n");
                }
            }

            if (!tempFile.delete()) {
                Logger.warning("Não foi possível excluir arquivo temporário: " + tempFile.getPath());
            }
            
            Logger.info("Arquivo de mensagens para " + language + " salvo com sucesso!");
        } catch (IOException e) {
            Logger.error("Erro ao salvar arquivo de mensagens para " + language + ": " + e.getMessage());
        }
    }
}
