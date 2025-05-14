package com.onyell.batataquente.enums;

public enum MessageType {
    ERROR("§c"),
    SUCCESS("§a"),
    WARNING("§e"),
    INFO("§b"),
    DEBUG("§7"),
    CONSOLE("§f");
    
    private final String color;
    
    MessageType(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
} 