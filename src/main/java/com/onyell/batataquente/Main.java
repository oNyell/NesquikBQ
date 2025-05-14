package com.onyell.batataquente;

import com.onyell.batataquente.commands.CommandManager;
import com.onyell.batataquente.language.Languages;
import com.onyell.batataquente.utils.Logger;
import dev.slickcollections.kiwizin.plugin.KPlugin;
import dev.slickcollections.kiwizin.plugin.logger.KLogger;
import lombok.Getter;

public class Main extends KPlugin {

    @Getter
    private static Main instance;

    @Override
    public void load() {
        instance = this;
        saveDefaultConfig();
    }

    @Override
    public void enable() {
        // Inicializa os sistemas de mensagens
        initializeMessagesSystem();
        
        // Inicializa o Logger
        Logger.initialize();
        
        // Registra comandos
        CommandManager.setupCommands();
        
        Logger.info("Plugin ativo com sucesso!");
    }

    @Override
    public void disable() {
        Logger.info("Plugin desligado com sucesso!");
    }

    @Override
    public void start() {
    }
    
    /**
     * Inicializa o sistema de mensagens
     */
    private void initializeMessagesSystem() {
        // Carrega as mensagens do idioma configurado
        Languages.loadMessages();
        
        // Salva os arquivos de mensagens padrão para os idiomas disponíveis
        // Isso garante que todos os idiomas terão todas as mensagens
        Languages.saveDefaultMessages("ptbr"); // Português
        Languages.saveDefaultMessages("enus"); // Inglês
        
        getLogger().info("Sistema de mensagens inicializado com sucesso.");
    }
}