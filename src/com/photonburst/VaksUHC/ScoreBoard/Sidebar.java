package com.photonburst.VaksUHC.ScoreBoard;

import com.photonburst.VaksUHC.Game.GameManager;
import com.photonburst.VaksUHC.UHCTeam;
import com.photonburst.VaksUHC.Utils;
import com.photonburst.VaksUHC.VaksUHC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the sidebar with timer, players/teams left, etc.
 */
public class Sidebar extends BukkitRunnable {
    /**
     * The scoreboard instance the rest of the plugin uses too
     */
    private final Scoreboard board;
    /**
     * Objectives for displaying in the sidebar
     * @see         #swapBuffer()
     */
    private Objective o, b;

    /**
     * The amount of seconds to set the countdown to every time
     */
    private static final int seconds = 60 * VaksUHC.plugin.getConfig().getInt("game.sidebar.timer-length");
    /**
     * The main variables for keeping track of the timer
     */
    public static int countdown = seconds, episodeCount = 1;

    /**
     * Main constructor of the sidebar, initializing variables
     */
    public Sidebar() {
        // Get the scoreboard
        this.board = VaksUHC.plugin.board;

        // Register the objectives showing on the sidebar
        this.o = board.registerNewObjective("sidebar", "dummy");
        o.setDisplayName(org.bukkit.ChatColor.GREEN +""+ VaksUHC.plugin.getConfig().getString("game.sidebar.title"));
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.b = o;

        // Set the scoreboard for all online players
        for(Player player: Bukkit.getOnlinePlayers()) {
            player.setScoreboard(board);
        }
    }

    /**
     * Updates the sidebar every second(!) because of its BukkitRunnable extension
     *
     * @see         BukkitRunnable
     * @see         GameManager#startGame
     */
    @Override
    public void run() {
        countdown--; // Decrements the countdown by 1 every second

        // Gets a list of all "fake" players
        List<String> entriesList = new ArrayList<>();
        entriesList.addAll(board.getEntries());
        for(UHCTeam team: VaksUHC.plugin.teamList) {
            for(String player: team.getPlayers()) {
                try {
                    entriesList.remove(entriesList.indexOf(player));
                } catch (Exception ignored) {}
            }
        }

        // Removes all fake players from the scoreboard
        for(String entry: entriesList) { board.resetScores(entry); }

        // Update the information on the scoreboard
        update();

        if(countdown == 0) { // Should the countdown become 0, start again and increment the episode counter
            episodeCount++;
            countdown = seconds;
        }
    }

    /**
     * Updates the information displayed on the sidebar
     */
    private void update() {
        // Saves the text, swaps the buffer, then saves it again
        // This means there's better covering overall (and thus less flicker)
        putScores();
        swapBuffer();
        putScores();
    }

    /**
     * Saves pieces of text onto the sidebar
     */
    private void putScores() {
        b.getScore("Teams left: "+ ChatColor.BOLD +""+ (VaksUHC.plugin.playerMap.values().stream().distinct().count())) .setScore(4);
        b.getScore("Players left: "+ ChatColor.BOLD +""+ (VaksUHC.plugin.playerMap.size()))                             .setScore(3);

        if(VaksUHC.plugin.getConfig().getInt("game.sidebar.timer-length") != 0) {
            b.getScore("-----------------")                                                                             .setScore(2);
            b.getScore("         .:" + Utils.secToMin(countdown) + ":.")                                                .setScore(1);
            b.getScore("      Episode " + (episodeCount < 10 ? "0" + episodeCount : episodeCount))                      .setScore(0);
        }
    }

    /**
     * Swaps the buffer- and original objectives
     */
    private void swapBuffer() {
        b.setDisplaySlot(o.getDisplaySlot());

        Objective t = o;
        o = b;
        b = t;
    }
}
