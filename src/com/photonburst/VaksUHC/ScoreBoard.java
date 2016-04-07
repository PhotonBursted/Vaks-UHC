package com.photonburst.VaksUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScoreBoard {
    private static Map<String, org.bukkit.scoreboard.Team.OptionStatus> optOptions = new HashMap<>();

    public static void setup() {
        ArrayList<Team> teamList = Main.plugin.teamList;

        optOptions.put("always", org.bukkit.scoreboard.Team.OptionStatus.ALWAYS);
        optOptions.put("never", org.bukkit.scoreboard.Team.OptionStatus.NEVER);
        optOptions.put("other-teams", org.bukkit.scoreboard.Team.OptionStatus.FOR_OTHER_TEAMS);
        optOptions.put("own-team", org.bukkit.scoreboard.Team.OptionStatus.FOR_OWN_TEAM);

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();

        org.bukkit.scoreboard.Team specTeam = createTeam(board, "Spectator", "Spectator", ChatColor.GRAY);
        specTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);

        try {
            setObjective(board, "in-tablist");
            setObjective(board, "under-name");
        } catch(Exception e) {
            e.printStackTrace();
        }

        Utils.println(board.getObjectives().size() +" objectives present already.");
        for(Team team: teamList) {
            org.bukkit.scoreboard.Team scteam = createTeam(board, team);

            scteam.setAllowFriendlyFire(Main.plugin.getConfig().getBoolean("game.team.friendly-fire"));
            setTeamOption(scteam, "player-collision", org.bukkit.scoreboard.Team.Option.COLLISION_RULE);
            setTeamOption(scteam, "death-message", org.bukkit.scoreboard.Team.Option.DEATH_MESSAGE_VISIBILITY);
            setTeamOption(scteam, "nametag", org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY);
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

    private static org.bukkit.scoreboard.Team createTeam(Scoreboard board, Team team) {
        return createTeam(board, team.getTeamColor(), team.getTeamName(false).substring(0, Math.min(32, team.getTeamName(false).length())), team.getTeamColorCode());
    }

    private static org.bukkit.scoreboard.Team createTeam(Scoreboard board, String teamName, String teamDisplayName, ChatColor color) {
        org.bukkit.scoreboard.Team scteam = board.registerNewTeam(teamName);
        scteam.setDisplayName(teamDisplayName);
        scteam.setPrefix(color +"");
        scteam.setSuffix(""+ ChatColor.WHITE);

        return scteam;
    }

    private static void setObjective(Scoreboard board, String slot) throws OptionNotConfiguredException {
        DisplaySlot displaySlot = DisplaySlot.PLAYER_LIST;
        if(slot.equals("in-tablist")) {
            displaySlot = DisplaySlot.PLAYER_LIST;
        } else if(slot.equals("under-name")) {
            displaySlot = DisplaySlot.BELOW_NAME;
        }

        switch (Main.plugin.getConfig().getString("game.stats." + slot)) {
            case "health":
                try {
                    board.getObjective("Health").setDisplaySlot(displaySlot);
                } catch(Exception e) {
                    board.registerNewObjective("Health", "health");
                    board.getObjective("Health").setDisplaySlot(displaySlot);
                }
                break;
            case "playerKillCount":
                try {
                    board.getObjective("Kills").setDisplaySlot(displaySlot);
                } catch(Exception e) {
                    board.registerNewObjective("Kills", "playerKillCount");
                    board.getObjective("Kills").setDisplaySlot(displaySlot);
                }
                break;
            case "none":
                break;
            default:
                throw new OptionNotConfiguredException(slot);
        }
    }

    private static void setTeamOption(org.bukkit.scoreboard.Team scteam, String configVal, org.bukkit.scoreboard.Team.Option option) {
        try {
            if(optOptions.containsKey(Main.plugin.getConfig().getString("game.team." + configVal).toLowerCase())) {
                scteam.setOption(option, optOptions.get(Main.plugin.getConfig().getString("game.team." + configVal)));
            } else throw new OptionNotConfiguredException(configVal);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
