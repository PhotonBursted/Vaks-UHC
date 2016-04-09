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

/**
 * Manages all chat-related events
 */
public class ChatManager implements Listener {
    /**
     * Detects whenever a player sends a message
     * @param e                 The event object to listen to
     */
    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        // Get the player the message came from
        Player player = e.getPlayer();
        // Get the contents of the message
        String msg = e.getMessage();

        // Retrieve the scoreboard team the player is part of
        Team SCteam = VaksUHC.plugin.board.getEntryTeam(player.getName());
        // Retrieve the UHC team the player was signed up for
        UHCTeam UHCteam = PlayerJoinManager.findPlayerInTeamList(player.getName());

        // Retrieve the token necessary to switch chat modes
        String s = VaksUHC.plugin.getConfig().getString("game.chat.global-chat-token");

        // Should there not be a game running, all chat is global
        if(GameManager.isRunning()) {
            // If the message is prepended with the token
            if (msg.startsWith(s)) {
                // Talk in global chat
                e.setFormat("[G] <" + SCteam.getPrefix() + player.getName() + SCteam.getSuffix() + "> " + msg.substring(s.length()));

                // If the sender was a spectator and spectator global chat is disabled, cancel the message
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