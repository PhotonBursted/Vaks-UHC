package com.photonburst.VaksUHC;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

public class EventCatcher implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Server.Spigot spigot = e.getPlayer().getServer().spigot();

        ArrayList<Team> teamList = Main.plugin.teamList;
        Team playerTeam = new Team("", "", new String[0]);
        for(Team team: teamList) {
            if(team.contains(player.getName())) {
                playerTeam = team;
            }
        }
        player.getScoreboard().getTeam(playerTeam.getTeamName()).addEntry(player.getName());

        spigot.broadcast(
                new ComponentBuilder("Welcome, ")
                        .color(ChatColor.GOLD)
                        .append(player.getName())
                        .color(ChatColor.RED)
                        .bold(true)
                        .append("! You have been added to team ")
                        .color(ChatColor.GOLD)
                        .append(playerTeam.getTeamName())
                        .color(playerTeam.getTeamColorCode())
                        .bold(true)
                        .create()
        );
    }
}
