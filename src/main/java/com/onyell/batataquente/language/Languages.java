package com.onyell.batataquente.language;

import com.onyell.batataquente.Main;
import com.onyell.batataquente.enums.MessageType;
import com.onyell.batataquente.enums.Messages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsável pelo gerenciamento de mensagens e traduções
 */
public class Languages {
    
    private static final Map<Messages, String> messagesCache = new HashMap<>();
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)\\}");
    
    /**
     * Carrega todas as mensagens do arquivo de idioma atual
     */
    public static void loadMessages() {
        messagesCache.clear();
        
        // Obtém o idioma selecionado na config.yml
        String language = Main.getInstance().getConfig().getString("language", "ptbr");
        
        // Carrega o arquivo de idioma apropriado (se existir)
        File languageFile = new File(Main.getInstance().getDataFolder(), "messages_" + language + ".yml");
        FileConfiguration langConfig = null;
        
        if (languageFile.exists()) {
            langConfig = YamlConfiguration.loadConfiguration(languageFile);
        }
        
        // Carrega todas as mensagens no cache
        for (Messages message : Messages.values()) {
            String path = message.name().toLowerCase().replace("_", ".");
            String defaultMessage = message.getDefaultMessage();
            
            // Se o arquivo de idioma existir e contiver a mensagem, use-a
            if (langConfig != null && langConfig.contains(path)) {
                messagesCache.put(message, langConfig.getString(path));
            } else {
                // Caso contrário, use a mensagem padrão
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
        // Obtém a mensagem do cache ou a mensagem padrão
        String msg = messagesCache.getOrDefault(message, message.getDefaultMessage());
        
        // Adiciona a cor correspondente ao tipo de mensagem
        msg = message.getType().getColor() + msg;
        
        // Se não houver substituições, retorna a mensagem como está
        if (replacements == null || replacements.length == 0) {
            return msg;
        }
        
        // Processa as substituições
        Map<String, String> replacementsMap = new HashMap<>();
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String key = String.valueOf(replacements[i]);
                String value = String.valueOf(replacements[i + 1]);
                replacementsMap.put(key, value);
            }
        }
        
        // Substitui os placeholders na mensagem
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
        File langFile = new File(Main.getInstance().getDataFolder(), "messages_" + language + ".yml");
        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
        
        // Adiciona um cabeçalho ao arquivo
        langConfig.options().header(
                "Arquivo de mensagens para o idioma: " + language + "\n" +
                "Você pode personalizar todas as mensagens do plugin aqui.\n" +
                "Use § para cores. Placeholders: {player}, {sender}, etc."
        );
        
        // Salva todas as mensagens padrão
        for (Messages message : Messages.values()) {
            String path = message.name().toLowerCase().replace("_", ".");
            
            // Só adiciona ao arquivo se a mensagem ainda não existir
            if (!langConfig.contains(path)) {
                langConfig.set(path, message.getDefaultMessage());
            }
        }
        
        // Salva o arquivo
        try {
            langConfig.save(langFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
