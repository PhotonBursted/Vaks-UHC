package com.photonburst.VaksUHC.ScoreBoard;

import com.photonburst.VaksUHC.Exceptions.OptionNotConfiguredException;
import com.photonburst.VaksUHC.UHCTeam;
import com.photonburst.VaksUHC.VaksUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all things related to the scoreboard
 */
public class ScoreBoard {
    /**
     * Map of all values the team options can get
     */
    private static final Map<String, Team.OptionStatus> optOptions = new HashMap<>();

    /**
     * Setup of the scoreboard system. Creates all the teams, sets the options following config, etcetera.
     */
    public static void setup() {
        Scoreboard board = VaksUHC.plugin.board;

        // Set the scoreboard for all players online at that moment
        for(Player player: Bukkit.getOnlinePlayers()) {
            player.setScoreboard(VaksUHC.plugin.board);
        }
        // Get the actual list of teams
        ArrayList<UHCTeam> teamList = VaksUHC.plugin.teamList;

        // Update the options map for team modifications
        optOptions.put("always", Team.OptionStatus.ALWAYS);
        optOptions.put("never", Team.OptionStatus.NEVER);
        optOptions.put("other-teams", Team.OptionStatus.FOR_OTHER_TEAMS);
        optOptions.put("own-team", Team.OptionStatus.FOR_OWN_TEAM);

        // Set objectives for in the tab list and below the player's names
        try {
            setObjective(board, "in-tablist");
            setObjective(board, "under-name");
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Loop through all teams
        for(UHCTeam team: teamList) {
            // Create all scoreboard teams based on the UHCTeam format
            Team scteam = createTeam(board, team);

            // Adjust the teams so they all have the right settings, based on the config files
            scteam.setAllowFriendlyFire(VaksUHC.plugin.getConfig().getBoolean("game.team.friendly-fire"));
            try {
                setTeamOption(scteam, "player-collision", Team.Option.COLLISION_RULE);
                setTeamOption(scteam, "death-message", Team.Option.DEATH_MESSAGE_VISIBILITY);
                setTeamOption(scteam, "nametag", Team.Option.NAME_TAG_VISIBILITY);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cleans up any residue the scoreboard leaves behind on stopping the plugin or server.
     */
    public static void cleanup() {
        // Remove all teams
        VaksUHC.plugin.board.getTeams().forEach(Team::unregister);

        // Remove all objectives
        VaksUHC.plugin.board.getObjectives().forEach(Objective::unregister);
    }

    /**
     * Method to create a scoreboard team
     * @param board         The scoreboard to add the team to
     * @param team          The UHCTeam to get all the information out of
     * @return              A scoreboard-compatible team, made up of the data in the UHCTeam, without any specific settings
     *
     * @see                 #createTeam(Scoreboard, String, String, ChatColor)
     */
    private static Team createTeam(Scoreboard board, UHCTeam team) {
        return createTeam(board,
                          team.getTeamColor(),
                          team.getTeamName(false).substring(0, Math.min(32, team.getTeamName(false).length())),
                          team.getTeamColorCode());
    }

    /**
     * Method to create a scoreboard team
     * @param board         The scoreboard to add the team to
     * @param teamName      The short name to reference the team with
     * @param teamDisplayName The longer, displayed name of the team
     * @param color         The color the team will have
     * @return              A scoreboard-compatible team without any specific settings
     *
     * @see                 #createTeam(Scoreboard, String, String, ChatColor)
     */
    private static Team createTeam(Scoreboard board, String teamName, String teamDisplayName, ChatColor color) {
        Team scteam = board.registerNewTeam(teamName);
        scteam.setDisplayName(teamDisplayName);

        // Prefix and suffix are used to color the teams
        scteam.setPrefix(color +"");
        scteam.setSuffix(""+ ChatColor.WHITE);

        return scteam;
    }

    /**
     * Sets statistics on the screen
     * @param board         The scoreboard to get the values from
     * @param slot          The space to fit the statistic in
     * @throws OptionNotConfiguredException
     */
    private static void setObjective(Scoreboard board, String slot) throws OptionNotConfiguredException {
        // Set the display slot to something so the compiler knows the value has been set either way
        DisplaySlot displaySlot = DisplaySlot.PLAYER_LIST;
        if(slot.equals("in-tablist")) {
            displaySlot = DisplaySlot.PLAYER_LIST;
        } else if(slot.equals("under-name")) {
            displaySlot = DisplaySlot.BELOW_NAME;
        }

        // Check what type of objective has to be created
        switch (VaksUHC.plugin.getConfig().getString("game.stats." + slot)) {
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

    /**
     * Sets options for the teams (name tag visibility, player collisions, etc.)
     * @param scteam            The scoreboard team to target
     * @param configPath        The partial path to the setting in the configuration file
     * @param option            The option to apply to the team
     */
    private static void setTeamOption(Team scteam, String configPath, Team.Option option) throws OptionNotConfiguredException {
        if(optOptions.containsKey(VaksUHC.plugin.getConfig().getString("game.team." + configPath).toLowerCase())) {
            scteam.setOption(option, optOptions.get(VaksUHC.plugin.getConfig().getString("game.team." + configPath)));
        } else throw new OptionNotConfiguredException(configPath);
    }
}
