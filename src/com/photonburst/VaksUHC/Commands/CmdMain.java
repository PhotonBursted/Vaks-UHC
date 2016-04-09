package com.photonburst.VaksUHC.Commands;

import com.photonburst.VaksUHC.Game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Central command handler
 */
public class CmdMain implements CommandExecutor {
    /**
     * Responds to a commmand passing through the chat
     * @param sender            Entity sending the command
     * @param cmd               Command which was sent
     * @param label             Raw message
     * @param args              Trimmmed message, excluding the command
     * @return                  The success of the command handling
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("vuhc")) {
            if(args.length == 0) {
                sender.sendMessage(ChatColor.GOLD +"This command needs arguments. Type "+ ChatColor.BOLD +"/help VaksUHC"+ ChatColor.GOLD +" for more info!");
            }

            if(args[0].equalsIgnoreCase("start")) {
                if(sender.hasPermission("VaksUHC.startMatch")) {
                    if (!GameManager.isRunning()) {
                        GameManager.startGame();
                    } else {
                        sender.sendMessage(ChatColor.RED +"That's not possible while a match is already running!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED +"You have no permission to do that!");
                }
            }
        }

        return true;
    }
}