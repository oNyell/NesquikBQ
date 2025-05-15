package com.onyell.batataquente.enums;

import com.onyell.batataquente.language.Languages;
import lombok.Getter;

@Getter
public enum Messages {
    PREFIX(MessageType.INFO, "§7[§bBatataQuente§7] ", MessageCategory.SYSTEM),

    GENERAL_NO_PERMISSION(MessageType.ERROR, "Você não tem permissão para usar esse comando.", MessageCategory.GENERAL),
    GENERAL_PLAYER_NOT_FOUND(MessageType.ERROR, "Jogador não encontrado ou não está online.", MessageCategory.GENERAL),

    DEV_HELP(MessageType.INFO, "Utilize \"/dev help\" para ver a lista de comandos disponíveis.", MessageCategory.DEV),
    DEV_ON(MessageType.SUCCESS, "Debug ativado com sucesso.", MessageCategory.DEV),
    DEV_OFF(MessageType.ERROR, "Debug desativado com sucesso.", MessageCategory.DEV),
    DEV_ADD_USAGE(MessageType.ERROR, "Utilize \"/dev add <jogador>\" para adicionar um jogador à lista de debug.", MessageCategory.DEV),
    DEV_ADD_SELF(MessageType.SUCCESS, "Você foi adicionado à lista de debug. Agora você receberá todas as mensagens de debug no chat.", MessageCategory.DEV),
    DEV_ADD_OTHER(MessageType.SUCCESS, "Jogador {player} foi adicionado à lista de debug.", MessageCategory.DEV),
    DEV_ADD_NOTIFY(MessageType.SUCCESS, "Você foi adicionado à lista de debug. Agora você receberá todas as mensagens de debug no chat.", MessageCategory.DEV),
    DEV_REMOVE_USAGE(MessageType.ERROR, "Utilize \"/dev remove <jogador>\" para remover um jogador da lista de debug.", MessageCategory.DEV),
    DEV_REMOVE_SELF(MessageType.ERROR, "Você foi removido da lista de debug.", MessageCategory.DEV),
    DEV_REMOVE_OTHER(MessageType.ERROR, "Jogador {player} foi removido da lista de debug.", MessageCategory.DEV),
    DEV_REMOVE_NOTIFY(MessageType.ERROR, "Você foi removido da lista de debug.", MessageCategory.DEV),
    DEV_TEST(MessageType.SUCCESS, "Mensagens de teste enviadas para o console e jogadores na lista de debug.", MessageCategory.DEV),
    DEV_RELOAD(MessageType.SUCCESS, "Configurações e mensagens recarregadas com sucesso.", MessageCategory.DEV),

    LOG_USAGE(MessageType.ERROR, "Utilize \"/dev logs [list/view] [data]\" para gerenciar os logs.", MessageCategory.LOG),
    LOG_VIEW_USAGE(MessageType.ERROR, "Utilize \"/dev logs view <data>\" para visualizar um arquivo de log.", MessageCategory.LOG),
    LOG_NOT_FOUND(MessageType.ERROR, "Nenhum arquivo de log encontrado.", MessageCategory.LOG),
    LOG_FILE_NOT_FOUND(MessageType.ERROR, "Arquivo de log para a data {date} não encontrado.", MessageCategory.LOG),
    LOG_ERROR(MessageType.ERROR, "Erro ao ler o arquivo de log: {error}", MessageCategory.LOG),

    DEBUG_ENABLED_BY(MessageType.DEBUG, "Modo debug ativado por {player}", MessageCategory.DEBUG),
    DEBUG_PLAYER_ADDED(MessageType.DEBUG, "Jogador {player} adicionado à lista de debug por {sender}", MessageCategory.DEBUG),
    DEBUG_PLAYER_REMOVED(MessageType.DEBUG, "Jogador {player} removido da lista de debug por {sender}", MessageCategory.DEBUG),
    DEBUG_CONFIG_RELOADED(MessageType.DEBUG, "Configurações e mensagens recarregadas por {player}", MessageCategory.DEBUG),
    DEBUG_COMMAND_REGISTERED(MessageType.DEBUG, "Comando {command} registrado com sucesso", MessageCategory.DEBUG),
    DEBUG_COMMAND_EXECUTED(MessageType.DEBUG, "Comando {command} executado por {player} em {time}ms", MessageCategory.DEBUG),

    TEST_DEBUG(MessageType.DEBUG, "Este é um teste de mensagem de debug", MessageCategory.TEST),
    TEST_INFO(MessageType.INFO, "Este é um teste de mensagem de informação", MessageCategory.TEST),
    TEST_WARNING(MessageType.WARNING, "Este é um teste de mensagem de aviso", MessageCategory.TEST),
    TEST_ERROR(MessageType.ERROR, "Este é um teste de mensagem de erro", MessageCategory.TEST);
    
    private final MessageType type;
    private final String defaultMessage;
    private final MessageCategory category;
    
    Messages(MessageType type, String defaultMessage, MessageCategory category) {
        this.type = type;
        this.defaultMessage = defaultMessage;
        this.category = category;
    }
    
    /**
     * Obtém a mensagem formatada sem prefixo
     * @param replacements Pares de chave-valor para substituição (ex: "player", "Steve")
     * @return Mensagem formatada sem prefixo
     */
    public String get(Object... replacements) {
        return Languages.getMessage(this, replacements);
    }
    
    /**
     * Obtém a mensagem formatada com prefixo, quando apropriado
     * @param replacements Pares de chave-valor para substituição (ex: "player", "Steve")
     * @return Mensagem formatada com ou sem prefixo
     */
    public String getMessage(Object... replacements) {
        if (this == PREFIX) {
            return get(replacements);
        }
        if (category.hasPrefix()) {
            return PREFIX.get() + get(replacements);
        } else {
            return get(replacements);
        }
    }

    public enum MessageCategory {
        SYSTEM(false),
        GENERAL(false),
        DEV(true),
        LOG(true),
        DEBUG(false),
        TEST(false);
        
        private final boolean hasPrefix;
        
        MessageCategory(boolean hasPrefix) {
            this.hasPrefix = hasPrefix;
        }
        
        public boolean hasPrefix() {
            return hasPrefix;
        }
    }
} 