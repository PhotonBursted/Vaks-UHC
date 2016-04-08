package com.photonburst.VaksUHC.Managers;

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
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Handles player related events
 */
public class PlayerManager implements Listener {
    /**
     * List of players who have died during this match
     */
    public static ArrayList<String> killed = new ArrayList<>();

    public static void assignPlayers(Collection<? extends Player> players) {
        for(Player player: players) {
            assignPlayer(player, VaksUHC.plugin.board.getTeam(findPlayerInTeamList(player.getName()).getTeamColor()));
        }
    }

    static void assignPlayer(Player player, Team team) {
        team.addEntry(player.getName());
    }

    static void assignPlayer(String player, String team) {
        assignPlayer(Bukkit.getPlayer(player), VaksUHC.plugin.board.getTeam(team));
    }

    public static UHCTeam findPlayerInTeamList(String player) {
        UHCTeam playerTeam = new UHCTeam("", "", new String[0]);

        for (UHCTeam team : VaksUHC.plugin.teamList) {
            if (team.contains(player)) {
                playerTeam = team;
                break;
            }
        }

        return playerTeam;
    }

    /**
     * Handles any players that join (puts them on a team, gives them the right gamemode/effects, etc)
     * @param e             The caught event when a player joins the server
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Get the player instance of whoever just joined
        Player player = e.getPlayer();

        // Set that player up for a game
        setUpPlayer(player);
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
            player.setGameMode(GameMode.SPECTATOR);
            // Add the player to the list of dead people
            killed.add(killed.size(), player.getName());

            // Remove the player from the player map
            VaksUHC.plugin.playerMap.remove(player.getName());
            // Check if the team of the player has any survivors
            if(!VaksUHC.plugin.playerMap.containsValue(VaksUHC.plugin.board.getEntryTeam(player.getName()).getName())) {
                spigot.broadcast(new ComponentBuilder("Oh snap! Team ")
                        .color(net.md_5.bungee.api.ChatColor.GOLD)
                        .bold(false)
                        .append(VaksUHC.plugin.board.getEntryTeam(player.getName()).getDisplayName())
                        .color(Utils.convertToColorCodeBun(VaksUHC.plugin.board.getEntryTeam(player.getName()).getPrefix()))
                        .bold(true)
                        .append(" has been wiped out!")
                        .color(net.md_5.bungee.api.ChatColor.GOLD)
                        .bold(false)
                        .create()
                );
            }
            // Send a message to the player
            player.sendMessage(ChatColor.GOLD + "Oh no! It seems like you have died... Within " +
                    ChatColor.BOLD + "10 seconds" +
                    ChatColor.RESET + "" + ChatColor.GOLD + ", you'll be moved to a spectator role. Wish your teammates all the best!"
            );

            // Move the player after a certain amount of time
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
    public static void setToSpectator(Player player) {
        assignPlayer(player.getName(), "Spectator");
        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0, true));
    }

    /**
     * Sets a player to be team member
     * @param player        Player to be put into player mode
     */
    public static void setToTeamMember(Player player, UHCTeam team) {
        assignPlayer(player.getName(), team.getTeamColor());
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public static void setUpPlayer(Player player) {
        ArrayList<UHCTeam> teamList = VaksUHC.plugin.teamList;

        // Initialize a value to playerTeam so the compiler knows a value has been assigned in any case
        UHCTeam playerTeam = new UHCTeam("", "", new String[0]);
        boolean success = false;

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

    public static void setUpPlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        assignPlayers(Bukkit.getOnlinePlayers());

        for (Player player : players) {
            setUpPlayer(player);
        }
    }
}