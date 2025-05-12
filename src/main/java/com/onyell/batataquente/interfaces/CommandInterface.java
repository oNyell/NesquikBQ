package com.onyell.batataquente.interfaces;

import org.bukkit.command.CommandSender;

public interface CommandInterface {

    void execute(CommandSender sender, String label, String[] args);

}