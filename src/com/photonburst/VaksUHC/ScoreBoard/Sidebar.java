package com.photonburst.VaksUHC.ScoreBoard;

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

public class Sidebar extends BukkitRunnable {
    private final Scoreboard board;
    private Objective o, b;

    private static final int ticks = 60 * VaksUHC.plugin.getConfig().getInt("game.sidebar.timer-length");
    public static int countdown = ticks, episodeCount = 1;

    public Sidebar() {
        this.board = VaksUHC.plugin.board;

        this.o = board.registerNewObjective("sidebar", "dummy");
        o.setDisplayName(org.bukkit.ChatColor.GREEN +""+ VaksUHC.plugin.getConfig().getString("game.sidebar.title"));
        o.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.b = o;

        for(Player player: Bukkit.getOnlinePlayers()) {
            player.setScoreboard(board);
        }
    }

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
            countdown = ticks;
        }
    }

    private void update() {
        putScores();
        swapBuffer();
        putScores();
    }

    private void putScores() {
        b.getScore("Teams left: "+ ChatColor.BOLD +""+ (VaksUHC.plugin.playerMap.values().stream().distinct().count())) .setScore(4);
        b.getScore("Players left: "+ ChatColor.BOLD +""+ (VaksUHC.plugin.playerMap.size()))                             .setScore(3);

        if(VaksUHC.plugin.getConfig().getInt("game.sidebar.timer-length") != 0) {
            b.getScore("-----------------")                                                                             .setScore(2);
            b.getScore("         .:" + Utils.secToMin(countdown) + ":.")                                                  .setScore(1);
            b.getScore("      Episode " + (episodeCount < 10 ? "0" + episodeCount : episodeCount))                      .setScore(0);
        }
    }

    private void swapBuffer() {
        b.setDisplaySlot(o.getDisplaySlot());

        Objective t = o;
        o = b;
        b = t;
    }
}
