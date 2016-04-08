package com.photonburst.VaksUHC.ScoreBoard;

import com.photonburst.VaksUHC.Managers.PlayerManager;
import com.photonburst.VaksUHC.UHCTeam;
import com.photonburst.VaksUHC.Utils;
import com.photonburst.VaksUHC.VaksUHC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class Sidebar extends BukkitRunnable {
    Scoreboard board;
    Objective o;

    public static int ticks = 60 * VaksUHC.plugin.getConfig().getInt("game.sidebar.timer-length");
    private int countdown = ticks, episodeCount = 1;

    public Sidebar(Objective o) {
        this.o = o;
        this.board = VaksUHC.plugin.board;

        for(Player player: Bukkit.getOnlinePlayers()) {
            player.setScoreboard(board);
        }
    }

    @Override
    public void run() {
        countdown--; //Taking away 1 from countdown every second

        List<String> entriesList = new ArrayList<>();
        entriesList.addAll(board.getEntries());
        for(UHCTeam team: VaksUHC.plugin.teamList) {
            for(String player: team.getPlayers()) {
                try {
                    entriesList.remove(entriesList.indexOf(player));
                } catch (Exception e) {}
            }
        }
        for(String entry: entriesList) { board.resetScores(entry); }

        o.getScore("Teams left: "+ ChatColor.BOLD +""+ (VaksUHC.plugin.playerMap.values().stream().distinct().count())) .setScore(4);
        o.getScore("Players left: "+ ChatColor.BOLD +""+ (Utils.calculatePlayerCount() - PlayerManager.killed.size())) .setScore(3);

        if(VaksUHC.plugin.getConfig().getBoolean("game.sidebar.timer-enabled")) {
            o.getScore("-----------------")                                                                             .setScore(2);
            o.getScore("        (" + Utils.secToMin(countdown) + ")")                                                   .setScore(1);
            o.getScore("      Episode " + (episodeCount < 10 ? "0" + episodeCount : episodeCount))                      .setScore(0);
        }

        if(countdown == 0) { //If countdown == 0.
            episodeCount++;
            countdown = ticks;
        }
    }
}
