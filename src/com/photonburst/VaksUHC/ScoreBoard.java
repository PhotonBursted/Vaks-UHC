package com.photonburst.VaksUHC;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;

public class ScoreBoard {
    public static void setup() {
        ArrayList<Team> teamList = Main.plugin.teamList;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        for(Team team: teamList) {
            org.bukkit.scoreboard.Team scteam = board.registerNewTeam(team.getTeamName().substring(0, Math.min(16, team.getTeamName().length())));
            scteam.setPrefix(team.getTeamColorCode() +"");
        }
    }

    public static void cleanup() {
        for(org.bukkit.scoreboard.Team scteam: Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            scteam.unregister();
        }

        for(Objective sco: Bukkit.getScoreboardManager().getMainScoreboard().getObjectives()) {
            sco.unregister();
        }
    }
}
