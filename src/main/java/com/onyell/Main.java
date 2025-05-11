package com.onyell;

import dev.slickcollections.kiwizin.plugin.KPlugin;
import dev.slickcollections.kiwizin.plugin.logger.KLogger;
import lombok.Getter;

public class Main extends KPlugin {

    @Getter
    public static Main instance;

    @Override
    public void start() {
        // Fazer o Load das informações.
        getLogger().info("");
    }

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }
}