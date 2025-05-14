package com.onyell.batataquente.enums;

import com.onyell.batataquente.language.Languages;

/**
 * Enum com todas as mensagens do sistema
 * Cada mensagem possui um tipo e uma mensagem padrão
 * As mensagens podem ser personalizadas através do sistema de idiomas
 */
public enum Messages {
    PREFIX(MessageType.INFO, "§7[§bBatataQuente§7] "),
    NO_PERMISSION(MessageType.ERROR, "Você não tem permissão para usar esse comando."),
    PLAYER_NOT_FOUND(MessageType.ERROR, "Jogador não encontrado ou não está online."),
    DEV_HELP(MessageType.INFO, "Utilize \"/dev [on/off/add/remove/test]\" para gerenciar o modo debug."),
    DEV_ON(MessageType.SUCCESS, "Debug ativado com sucesso."),
    DEV_OFF(MessageType.ERROR, "Debug desativado com sucesso."),
    DEV_ADD_USAGE(MessageType.ERROR, "Utilize \"/dev add <jogador>\" para adicionar um jogador à lista de debug."),
    DEV_ADD_SELF(MessageType.SUCCESS, "Você foi adicionado à lista de debug. Agora você receberá todas as mensagens de debug no chat."),
    DEV_ADD_OTHER(MessageType.SUCCESS, "Jogador {player} foi adicionado à lista de debug."),
    DEV_ADD_NOTIFY(MessageType.SUCCESS, "Você foi adicionado à lista de debug. Agora você receberá todas as mensagens de debug no chat."),
    DEV_REMOVE_USAGE(MessageType.ERROR, "Utilize \"/dev remove <jogador>\" para remover um jogador da lista de debug."),
    DEV_REMOVE_SELF(MessageType.ERROR, "Você foi removido da lista de debug."),
    DEV_REMOVE_OTHER(MessageType.ERROR, "Jogador {player} foi removido da lista de debug."),
    DEV_REMOVE_NOTIFY(MessageType.ERROR, "Você foi removido da lista de debug."),
    DEV_TEST(MessageType.SUCCESS, "Mensagens de teste enviadas para o console e jogadores na lista de debug."),
    DEBUG_ENABLED_BY(MessageType.DEBUG, "Modo debug ativado por {player}"),
    DEBUG_PLAYER_ADDED(MessageType.DEBUG, "Jogador {player} adicionado à lista de debug por {sender}"),
    DEBUG_PLAYER_REMOVED(MessageType.DEBUG, "Jogador {player} removido da lista de debug por {sender}"),
    DEBUG_TEST(MessageType.DEBUG, "Este é um teste de mensagem de debug"),
    INFO_TEST(MessageType.INFO, "Este é um teste de mensagem de informação"),
    WARNING_TEST(MessageType.WARNING, "Este é um teste de mensagem de aviso"),
    ERROR_TEST(MessageType.ERROR, "Este é um teste de mensagem de erro");
    
    private final MessageType type;
    private final String defaultMessage;
    
    Messages(MessageType type, String defaultMessage) {
        this.type = type;
        this.defaultMessage = defaultMessage;
    }
    
    /**
     * Obtém a mensagem formatada com as substituições
     * @param replacements Pares de chave-valor para substituição (ex: "player", "Steve")
     * @return Mensagem formatada
     */
    public String get(Object... replacements) {
        return Languages.getMessage(this, replacements);
    }
    
    /**
     * Obtém a mensagem com prefixo e formatada com as substituições
     * @param replacements Pares de chave-valor para substituição (ex: "player", "Steve")
     * @return Mensagem formatada com prefixo
     */
    public String getMessage(Object... replacements) {
        return PREFIX.get() + get(replacements);
    }
    public String getDefaultMessage() {
        return defaultMessage;
    }
    public MessageType getType() {
        return type;
    }
} 