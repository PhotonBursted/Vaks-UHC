package com.photonburst.VaksUHC.Listeners;

import com.photonburst.VaksUHC.UHCTeam;
import com.photonburst.VaksUHC.Utils;
import com.photonburst.VaksUHC.VaksUHC;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Handles player related events
 */
public class PlayerListener implements Listener {
    /**
     * List of players who have died during this match
     */
    public static ArrayList<String> killed = new ArrayList<>();

    /**
     * Handles any players that join (puts them on a team, gives them the right gamemode/effects, etc)
     * @param e             The caught event when a player joins the server
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Get the player instance of whoever just joined
        Player player = e.getPlayer();

        ArrayList<UHCTeam> teamList = VaksUHC.plugin.teamList;
        // Initialize a value to playerTeam so the compiler knows a value has been assigned in any case
        UHCTeam playerTeam = new UHCTeam("", "", new String[0]);
        boolean success = false;
        // Loop through the teamList, trying to find the player
        for(UHCTeam team: teamList) {
            if(team.contains(player.getName())) {
                playerTeam = team;
            }
            success = team.contains(player.getName()) || success;
        }

        // If the player has a team in the configuration and hasn't been killed, add them to their team
        if(success && !killed.contains(player.getName())) {
            setToTeamMember(player, playerTeam);
            player.sendMessage(ChatColor.GOLD +"Welcome"+
                    ChatColor.BOLD +" "+ ChatColor.RED +""+ player.getName() +
                    ChatColor.RESET +""+ ChatColor.GOLD +"! You have been added to team"+
                    ChatColor.BOLD +" "+ playerTeam.getTeamColorCode() +""+  playerTeam.getTeamName(false)
            );
        } else {
            setToSpectator(player);
            player.sendMessage(ChatColor.GOLD +"Welcome"+
                    ChatColor.BOLD +" "+ ChatColor.RED +""+ player.getName() +
                    ChatColor.RESET +""+ ChatColor.GOLD +"! You have been added to the"+
                    ChatColor.BOLD +" "+ ChatColor.GRAY +"spectators"
            );
        }
    }

    /**
     * Handles any players that die during the match
     * @param e             The caught event when a player dies
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        // Get the player instance of whoever just died
        Player player = e.getEntity();
        Server.Spigot spigot = player.getServer().spigot();

        // If the player died before, do nothing. Else, put them into spectator mode after some delay
        if(!killed.contains(player.getName())) {
            killed.add(killed.size(), player.getName());
            VaksUHC.plugin.playerMap.remove(player.getName());
            if(!VaksUHC.plugin.playerMap.containsValue(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()).getName())) {
                spigot.broadcast(new ComponentBuilder("Oh snap! Team ")
                        .color(net.md_5.bungee.api.ChatColor.GOLD)
                        .bold(false)
                        .append(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()).getDisplayName())
                        .color(Utils.convertToColorCodeBun(Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName()).getPrefix()))
                        .bold(true)
                        .append(" has been wiped out!")
                        .color(net.md_5.bungee.api.ChatColor.GOLD)
                        .bold(false)
                        .create()
                );
            }
            player.sendMessage(ChatColor.GOLD + "Oh no! It seems like you have died... Within " +
                    ChatColor.BOLD + "10 seconds" +
                    ChatColor.RESET + "" + ChatColor.GOLD + ", you'll be moved to a spectator role. Wish your teammates all the best!"
            );

            new BukkitRunnable() {
                @Override
                public void run() {
                    setToSpectator(player);
                }

            }.runTaskLater(VaksUHC.plugin, 20 * VaksUHC.plugin.getConfig().getInt("game.team.death-kick"));
        }
    }

    /**
     * Sets a player to be spectator
     * @param player        Player to be put into spectator mode
     */
    public void setToSpectator(Player player) {
        player.getScoreboard().getTeam("Spectator").addEntry(player.getName());
        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, true));
    }

    public void setToTeamMember(Player player, UHCTeam team) {
        player.getScoreboard().getTeam(team.getTeamColor()).addEntry(player.getName());
        player.setGameMode(GameMode.SURVIVAL);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 0, 0, true));
    }
}