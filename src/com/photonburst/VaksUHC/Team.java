package com.photonburst.VaksUHC;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Team {
    private String teamName;
    private String teamColor;
    private String[] players;

    Team(String teamName, String teamColor, String[] players) {
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.players = players;
    }

    public boolean contains(String player) {
        boolean contains = false;

        for(String teamPlayer: players) {
            if(teamPlayer.equals(player) || contains) {
                contains = true;
            }
        }

        return contains;
    }

    public String[] getPlayers() {
        return this.players;
    }

    public String getTeamColor() {
        return this.teamColor;
    }

    public ChatColor getTeamColorCode() {
        return Utils.convertToColorCode(getTeamColor());
    }

    public String getTeamName(boolean doCamelCase) {
        if(doCamelCase) { return Utils.toCamelCase(this.teamName); } else { return this.teamName; }
    }

    public int getTeamSize() {
        return this.players.length;
    }

    public static ArrayList<Team> getTeams(ArrayList<Team> teamList) {
        ArrayList<String[]> rows = new ArrayList<>();
        teamList.clear();

        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);

        File input = new File(Main.plugin.getDataFolder(), "teams.csv");

        try {
            MappingIterator<String[]> it = mapper.readerFor(String[].class).readValues(input);
            while (it.hasNext()) {
                rows.add(rows.size(), it.next());
            }
        } catch (IOException e) {
            Utils.println("[WARN] teams.csv wasn't found! Please supply before loading the plugin.");
        }

        for(int i=1; i<rows.size(); i++) {
            String[] team = rows.get(i);
            String[] players = new String[team.length - 2];

            for(int j=2; j<team.length; j++) {
                players[j-2] = team[j];
            }

            teamList.add(teamList.size(), new Team(team[0], team[1], players));
        }

        for(int i=0; i<teamList.size(); i++) {
            Team team = teamList.get(i);

            Main.plugin.getTeamConfig().set("teams.team"+ (i+1) +".name", team.getTeamName(false));
            Main.plugin.getTeamConfig().set("teams.team"+ (i+1) +".color", team.getTeamColor());

            for(int j=0; j<team.getTeamSize(); j++) {
                Main.plugin.getTeamConfig().set("teams.team"+ (i+1) +".players.player"+ (j+1), team.getPlayers()[j]);
            }
        }

        try {
            Main.plugin.getTeamConfig().save(Main.plugin.teamsf);
        } catch (IOException e) {
            Utils.println("Couldn't save team configuration!");
            e.printStackTrace();
        }

        return teamList;
    }

    @Override
    public String toString() {
        return "[teamName="+ this.teamName +", teamColor="+ this.teamColor +", players="+ Arrays.toString(players);
    }
}
