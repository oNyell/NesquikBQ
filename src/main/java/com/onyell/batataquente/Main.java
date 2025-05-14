package com.onyell.batataquente;

import com.onyell.batataquente.commands.CommandManager;
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
        Logger.initialize();
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
}