package com.onyell.batataquente;

import com.onyell.batataquente.commands.CommandManager;
import com.onyell.batataquente.language.Languages;
import com.onyell.batataquente.utils.ConfigManager;
import com.onyell.batataquente.utils.Logger;
import dev.slickcollections.kiwizin.plugin.KPlugin;
import lombok.Getter;

public class Main extends KPlugin {

    @Getter
    private static Main instance;
    
    @Getter
    private static final String version = "1.0.0";

    @Override
    public void load() {
        instance = this;

        ConfigManager.createDefaultConfig();

        getLogger().info("=================================================");
        getLogger().info("Carregando plugin BatataQuente v" + version);
        getLogger().info("=================================================");
    }

    @Override
    public void enable() {
        long startTime = System.currentTimeMillis();
        
        try {
            initializeMessagesSystem();
            initializeLoggingSystem();
            registerCommands();

            long elapsedTime = System.currentTimeMillis() - startTime;
            Logger.info("Plugin ativo com sucesso! (" + elapsedTime + "ms)");
            
        } catch (Exception e) {
            getLogger().severe("Erro durante a inicialização do plugin: " + e.getMessage());
            e.printStackTrace();

            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void disable() {
        Logger.debug("Plugin está sendo desligado...");

        Logger.info("Plugin desligado com sucesso!");
        Logger.shutdown();
        
        getLogger().info("=================================================");
        getLogger().info("Plugin BatataQuente desativado");
        getLogger().info("=================================================");
    }

    @Override
    public void start() {
        Logger.debug("Método start() chamado - Fase final de inicialização");
    }
    
    private void initializeMessagesSystem() {
        Logger.debug("Inicializando sistema de mensagens...");

        Languages.loadMessages();

        Languages.saveDefaultMessages("ptbr");
        Languages.saveDefaultMessages("enus");
        
        Logger.info("Sistema de mensagens inicializado com sucesso");
    }
    
    private void initializeLoggingSystem() {
        Logger.debug("Inicializando sistema de logs...");
        Logger.initialize();
        Logger.debug("Sistema de logs inicializado com sucesso");
    }
    
    private void registerCommands() {
        Logger.debug("Registrando comandos...");
        CommandManager.setupCommands();
        Logger.debug("Comandos registrados com sucesso");
    }

    public void reloadPlugin() {
        Logger.debug("Recarregando plugin...");
        ConfigManager.reloadAll();
        Logger.info("Plugin recarregado com sucesso");
    }
}