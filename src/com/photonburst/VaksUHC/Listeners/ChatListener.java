package com.photonburst.VaksUHC.Listeners;

import com.photonburst.VaksUHC.UHCTeam;
import com.photonburst.VaksUHC.Utils;
import com.photonburst.VaksUHC.VaksUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String msg = e.getMessage();
        ChatColor teamColor = ChatColor.GRAY;
        // Get the color associated with the player's team

        if(msg.startsWith("@team")) {
            // Let only team members see the message
            try {
                e.getRecipients().clear();
                for (UHCTeam team: VaksUHC.plugin.teamList) {
                    if (team.contains(player.getName())) {
                        teamColor = team.getTeamColorCode();
                        for (String teamPlayer: team.getPlayers()) {
                            try {
                                e.getRecipients().add(Bukkit.getPlayer(teamPlayer).getPlayer());
                            } catch (NullPointerException ex) { }
                        }
                    }
                }
            } catch (UnsupportedOperationException ex) {
                e.isCancelled();
                player.sendMessage(ChatColor.ITALIC +""+ ChatColor.RED +"The message failed to send. Try again!");
            }

            // Reshape the message so it shows it's a team message
            e.setFormat(ChatColor.DARK_GREEN + "["
                    + teamColor
                    + player.getName()
                    + ChatColor.GOLD
                    + " -> team"
                    + ChatColor.DARK_GREEN
                    + "]"
                    + ChatColor.WHITE
                    + msg.substring(5)
            );
        }
    }
}