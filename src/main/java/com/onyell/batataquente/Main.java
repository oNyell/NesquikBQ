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

        getLogger().info("Carregando plugin BatataQuente...");
    }

    @Override
    public void enable() {
        initializeMessagesSystem();

        Logger.initialize();

        Logger.debug("Plugin está sendo inicializado...");

        CommandManager.setupCommands();

        Logger.info("Plugin ativo com sucesso!");
        Logger.debug("Inicialização concluída!");
    }

    @Override
    public void disable() {
        Logger.debug("Plugin está sendo desligado...");

        Logger.info("Plugin desligado com sucesso!");
        Logger.shutdown();
    }

    @Override
    public void start() {
        Logger.debug("Método start() chamado - Fase final de inicialização");
    }
    
    private void initializeMessagesSystem() {
        Languages.loadMessages();

        Languages.saveDefaultMessages("ptbr");
        Languages.saveDefaultMessages("enus");
        
        getLogger().info("Sistema de mensagens inicializado com sucesso.");
    }
}