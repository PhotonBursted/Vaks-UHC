package com.photonburst.VaksUHC.Managers;

import com.photonburst.VaksUHC.UHCTeam;
import com.photonburst.VaksUHC.VaksUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Handles player related events
 */
public class PlayerJoinManager implements Listener {

    public static void assignPlayers(Collection<? extends Player> players) {
        for(Player player: players) {
            assignPlayer(player, VaksUHC.plugin.board.getTeam(findPlayerInTeamList(player.getName()).getTeamColor()));
        }
    }

    private static void assignPlayer(Player player, Team team) {
        team.addEntry(player.getName());
    }

    private static void assignPlayer(String player, String team) {
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

        Bukkit.getScheduler().runTaskLater(VaksUHC.plugin, new Runnable() {
            @Override
            public void run() {
                // Set that player up for a game
                setUpPlayer(player);
            }
        }, 20L);
    }

    /**
     * Sets a player to be team member
     * @param player        Player to be put into player mode
     */
    private static void setToTeamMember(Player player, UHCTeam team) {
        assignPlayer(player.getName(), team.getTeamColor());
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public static void setUpPlayer(Player player) {
        ArrayList<UHCTeam> teamList = VaksUHC.plugin.teamList;

        // Initialize a value to playerTeam so the compiler knows a value has been assigned in any case
        UHCTeam playerTeam = findPlayerInTeamList(player.getName());

        if(VaksUHC.plugin.playerMap.containsKey(player.getName())) {
            setToTeamMember(player, playerTeam);
            player.sendMessage(ChatColor.GOLD +"Welcome"+
                    ChatColor.BOLD +" "+ ChatColor.RED +""+ player.getName() +
                    ChatColor.RESET +""+ ChatColor.GOLD +"! You have been added to team"+
                    ChatColor.BOLD +" "+ playerTeam.getTeamColorCode() +""+  playerTeam.getTeamName(false)
            );
        } else {
            PlayerDeathManager.setToSpectator(player);
            player.sendMessage(ChatColor.GOLD +"Welcome"+
                    ChatColor.BOLD +" "+ ChatColor.RED +""+ player.getName() +
                    ChatColor.RESET +""+ ChatColor.GOLD +"! You have been added to the"+
                    ChatColor.BOLD +" "+ ChatColor.GRAY +"spectators"
            );
        }
    }
}