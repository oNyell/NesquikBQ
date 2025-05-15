package com.onyell.batataquente.utils;

import com.onyell.batataquente.Main;
import com.onyell.batataquente.language.Languages;

public class ConfigManager {
    public static void reloadAll() {
        reloadConfig();
        reloadMessages();
        Logger.initialize();
    }
    public static void reloadConfig() {
        Main.getInstance().reloadConfig();
    }
    public static void reloadMessages() {
        Languages.loadMessages();
    }
} 