package com.photonburst.VaksUHC;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Custom class for saving teams in an easier format
 */
public class UHCTeam {
    private String teamName;
    private String teamColor;
    private String[] players;

    /**
     * Constructor for a new UHCTeam
     * @param teamName      The display name of the team
     * @param teamColor     The color the team will have in tab lists and on player names
     * @param players       The set of players which are part of the team
     */
    public UHCTeam(String teamName, String teamColor, String[] players) {
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.players = players;
    }

    /**
     * Method to check whether a certain player is part of a team
     * @param player        The player to check the presence of
     * @return              <code>true</code> if the player is actually part of this team
     */
    public boolean contains(String player) {
        boolean contains = false;

        for(String teamPlayer: players) {
            if(teamPlayer.equals(player) || contains) {
                contains = true;
            }
        }

        return contains;
    }

    /**
     * Returns the set of players in this team
     * @return              The set of players in the team
     */
    public String[] getPlayers() {
        return this.players;
    }

    /**
     * Gets the color which is to be used to color the team
     * @return              The string representing the team color
     */
    public String getTeamColor() {
        return this.teamColor;
    }

    /**
     * Gets the actual ChatColor reference for the team color
     * @return              The Bukkit ChatColor reference to the team color
     */
    public ChatColor getTeamColorCode() {
        return Utils.convertToColorCode(getTeamColor());
    }

    /**
     * Gets the team's display name
     * @param doCamelCase   Determines whether or not to transform the name into camelCase to save space
     * @return              The team display name String
     */
    public String getTeamName(boolean doCamelCase) {
        if(doCamelCase) { return Utils.toCamelCase(this.teamName); } else { return this.teamName; }
    }

    /**
     * Gets the team's size
     * @return              The amount of players in the team
     */
    public int getTeamSize() {
        return this.players.length;
    }

    /**
     * Converts a .csv spreadsheet template into a UHCTeam object
     * @param teamList      Used purely as a reference to an earlier object, overwriting it
     * @return              A list of all UHCTeams
     */
    public static ArrayList<UHCTeam> getTeams(ArrayList<UHCTeam> teamList) {
        ArrayList<String[]> rows = new ArrayList<>();
        teamList.clear();

        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        File input = new File(VaksUHC.plugin.getDataFolder(), "teams.csv");

        try {
            MappingIterator<String[]> it = mapper.readerFor(String[].class).readValues(input);
            while (it.hasNext()) {
                rows.add(rows.size(), it.next());
            }
        } catch (IOException e) {
            Utils.println("teams.csv wasn't found! Please supply before loading the plugin.");
            System.exit(0);
        }

        for(int i=1; i<rows.size(); i++) {
            String[] team = rows.get(i);
            String[] players = new String[team.length - 2];

            for(int j=2; j<team.length; j++) {
                players[j-2] = team[j];
            }

            teamList.add(teamList.size(), new UHCTeam(team[0], team[1], players));
        }

        for(int i=0; i<teamList.size(); i++) {
            UHCTeam team = teamList.get(i);

            VaksUHC.plugin.getTeamConfig().set("teams.team"+ (i+1) +".name", team.getTeamName(false));
            VaksUHC.plugin.getTeamConfig().set("teams.team"+ (i+1) +".color", team.getTeamColor());

            for(int j=0; j<team.getTeamSize(); j++) {
                VaksUHC.plugin.getTeamConfig().set("teams.team"+ (i+1) +".players.player"+ (j+1), team.getPlayers()[j]);
            }
        }

        try {
            VaksUHC.plugin.getTeamConfig().save(VaksUHC.plugin.teamsf);
        } catch (IOException e) {
            Utils.println("Couldn't save team configuration!");
            e.printStackTrace();
        }

        return teamList;
    }

    /**
     * Return a string representation of a UHCTeam object
     * @return              A string containing all information in a UHCTeam
     */
    @Override
    public String toString() {
        return "[teamName="+ this.teamName +", teamColor="+ this.teamColor +", players="+ Arrays.toString(players);
    }
}
