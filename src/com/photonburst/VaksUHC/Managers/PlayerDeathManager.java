package com.photonburst.VaksUHC.Managers;

import com.photonburst.VaksUHC.Utils;
import com.photonburst.VaksUHC.VaksUHC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class PlayerDeathManager implements Listener {
    /**
     * List of players who aren't active in the match
     */
    public static final ArrayList<String> killed = new ArrayList<>();

    public PlayerDeathManager() {
    }

    /**
     * Handles any players that die during the match
     *
     * @param e The caught event when a player dies
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        // Get the player instance of whoever just died
        Player player = e.getEntity();
        Server.Spigot spigot = player.getServer().spigot();

        // If the player died before, do nothing. Else, put them into spectator mode after some delay
        if (!killed.contains(player.getName())) {
            player.setGameMode(GameMode.SPECTATOR);

            // Add the player to the list of dead people
            killed.add(killed.size(), player.getName());

            // Remove the player from the player map
            VaksUHC.plugin.playerMap.remove(player.getName());
            // Check if the team of the player has any survivors
            if (!VaksUHC.plugin.playerMap.containsValue(VaksUHC.plugin.board.getEntryTeam(player.getName()).getName())) {
                spigot.broadcast(new ComponentBuilder("Oh snap! Team ")
                        .color(ChatColor.GOLD)
                        .bold(false)
                        .append(PlayerJoinManager.findPlayerInTeamList(player.getName()).getTeamName(false))
                        .color(Utils.convertToColorCodeBun(PlayerJoinManager.findPlayerInTeamList(player.getName()).getTeamColor()))
                        .bold(true)
                        .append(" has been wiped out!")
                        .color(ChatColor.GOLD)
                        .bold(false)
                        .create()
                );
            } else {
                player.sendMessage(ChatColor.GOLD + "Oh no! It seems like you have died... Within " +
                        ChatColor.BOLD + "10 seconds" +
                        ChatColor.RESET + "" + ChatColor.GOLD + ", you'll be moved to a spectator role. Wish your teammates all the best!"
                );
            }

            // Move the player after a certain amount of time
            new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerDeathManager.setToSpectator(player);
                }
            }.runTaskLater(VaksUHC.plugin, 20 * VaksUHC.plugin.getConfig().getInt("game.team.death-kick"));
        }
    }

    /**
     * Sets a player to be spectator
     * @param player        Player to be put into spectator mode
     */
    static void setToSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, true));
    }
}