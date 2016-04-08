package com.photonburst.VaksUHC.Managers;

import com.photonburst.VaksUHC.Game.GameManager;
import com.photonburst.VaksUHC.UHCTeam;
import com.photonburst.VaksUHC.VaksUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

public class ChatManager implements Listener {
    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String msg = e.getMessage();
        Team SCteam = VaksUHC.plugin.board.getEntryTeam(player.getName());
        UHCTeam UHCteam = PlayerJoinManager.findPlayerInTeamList(player.getName());
        ChatColor teamColor = ChatColor.GRAY;
        String s = VaksUHC.plugin.getConfig().getString("game.chat.global-chat-token");
        // Get the color associated with the player's team

        if(GameManager.isRunning()) {
            if (msg.startsWith(s)) {
                e.setFormat("[G] <" + SCteam.getPrefix() + player.getName() + SCteam.getSuffix() + "> " + msg.substring(s.length()));
                if (!VaksUHC.plugin.getConfig().getBoolean("game.chat.allow-spectator-global") && PlayerDeathManager.killed.contains(player.getName())) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.ITALIC + "" + ChatColor.RED + "Spectators cannot send global messages!");
                }
            } else {
                // Let only team members see the message
                try {
                    // Clear list of people getting the message
                    e.getRecipients().clear();

                    // Find the team the player who sent the message is part of
                    for (String teamMember : SCteam.getEntries()) {
                        // Try to add the player to the recipients list. Escape NullPointer in case of being offline/not on the team anymore
                        try {
                            e.getRecipients().add(Bukkit.getPlayer(teamMember).getPlayer());
                        } catch (NullPointerException ignored) {
                        }
                    }
                    // Sometimes, the list getRecipients() returns is immutable
                    // Catch this error and report to the player
                } catch (UnsupportedOperationException ex) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.ITALIC + "" + ChatColor.RED + "The message failed to send. Try again!");
                }

                // Reshape the message so it shows it's a team message
                e.setFormat((PlayerDeathManager.killed.contains(player.getName()) ? "[S]" : "[T]") + " <" + UHCteam.getTeamColorCode() + player.getName() + ChatColor.WHITE + "> " + msg);
            }
        } else {
            e.setFormat("[G] <" + SCteam.getPrefix() + player.getName() + SCteam.getSuffix() + "> " + msg);
        }
    }
}