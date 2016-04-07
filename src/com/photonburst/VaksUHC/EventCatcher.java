package com.photonburst.VaksUHC;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class EventCatcher implements Listener {
    ArrayList<String> killed = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        ArrayList<Team> teamList = Main.plugin.teamList;
        Team playerTeam = new Team("", "", new String[0]);
        boolean success = false;
        for(Team team: teamList) {
            if(team.contains(player.getName())) {
                playerTeam = team;
            }
            success = team.contains(player.getName()) || success;
        }

        if(success && !killed.contains(player.getName())) {
            player.getScoreboard().getTeam(playerTeam.getTeamColor()).addEntry(player.getName());
            player.setGameMode(GameMode.SURVIVAL);
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

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if(!killed.contains(player.getName())) {
            player.sendMessage(ChatColor.GOLD + "Oh no! It seems like you have died... Within " +
                    ChatColor.BOLD + "10 seconds" +
                    ChatColor.RESET + "" + ChatColor.GOLD + ", you'll be moved to a spectator role. Wish your teammates all the best!"
            );

            new BukkitRunnable() {
                @Override
                public void run() {
                    setToSpectator(player);
                }

            }.runTaskLater(Main.plugin, 20 * Main.plugin.getConfig().getInt("game.team.death-kick"));
        }
    }

    public void setToSpectator(Player player) {
        killed.add(killed.size(), player.getName());
        player.getScoreboard().getTeam("Spectator").addEntry(player.getName());
        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, true));
    }
}