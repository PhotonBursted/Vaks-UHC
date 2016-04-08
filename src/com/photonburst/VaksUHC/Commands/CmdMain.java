package com.photonburst.VaksUHC.Commands;

import com.photonburst.VaksUHC.Game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdMain implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("vuhc")) {
            if(args[0].equalsIgnoreCase("start")) {
                GameManager.startGame();
            }
        }

        return true;
    }
}