package com.photonburst.VaksUHC.Listeners;

import com.photonburst.VaksUHC.Utils;
import com.photonburst.VaksUHC.VaksUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    public void onMessage(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String msg = e.getMessage();
        ChatColor playerColor = ChatColor.GRAY;
        // Get the color associated with the player's team
        if(VaksUHC.plugin.playerMap.containsKey(player.getName())) {
            playerColor = Utils.convertToColorCodeBuk(VaksUHC.plugin.board.getEntryTeam(player.getName()).getName());
        }

        if(msg.startsWith("@team")) {
            // Reshape the message so it shows it's a team message
            e.setFormat(ChatColor.DARK_GREEN + "["
                    + playerColor
                    + player.getName()
                    + ChatColor.GOLD
                    + " -> their team"
                    + ChatColor.DARK_GREEN
                    + "]"
                    + ChatColor.WHITE
                    + msg.substring(5)
            );
        }
    }
}