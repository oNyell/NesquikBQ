package com.onyell.batataquente;

import com.onyell.batataquente.commands.CommandManager;
import dev.slickcollections.kiwizin.plugin.KPlugin;
import dev.slickcollections.kiwizin.plugin.logger.KLogger;
import lombok.Getter;

public class Main extends KPlugin {

    @Getter
    private static Main instance;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        CommandManager.setupCommands();
        getLogger().info("§aPlugin ativo com sucesso!");
    }

    @Override
    public void disable() {
        getLogger().info("§aPlugin desligado com sucesso!");
    }

    @Override
    public void start() {
    }
}