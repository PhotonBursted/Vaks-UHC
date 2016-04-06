package com.photonburst.VaksUHC;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    public ChatColor convertToColorCode(String color) {
        Map<String, ChatColor> colorMap = new HashMap<>();
        colorMap.put("Black", ChatColor.BLACK);
        colorMap.put("Dark Blue", ChatColor.DARK_BLUE);
        colorMap.put("Dark Green", ChatColor.DARK_GREEN);
        colorMap.put("Dark Aqua", ChatColor.DARK_AQUA);
        colorMap.put("Dark Red", ChatColor.DARK_RED);
        colorMap.put("Dark Purple", ChatColor.DARK_PURPLE);
        colorMap.put("Gold", ChatColor.GOLD);
        colorMap.put("Gray", ChatColor.GRAY);
        colorMap.put("Dark Gray", ChatColor.DARK_GRAY);
        colorMap.put("Blue", ChatColor.BLUE);
        colorMap.put("Green", ChatColor.GREEN);
        colorMap.put("Aqua", ChatColor.AQUA);
        colorMap.put("Red", ChatColor.RED);
        colorMap.put("Light Purple", ChatColor.LIGHT_PURPLE);
        colorMap.put("Yellow", ChatColor.YELLOW);
        colorMap.put("White", ChatColor.WHITE);

        return colorMap.get(color);
    }

    public String[] getPlayers() {
        return this.players;
    }

    public String getTeamColor() {
        return this.teamColor;
    }

    public ChatColor getTeamColorCode() {
        return convertToColorCode(getTeamColor());
    }

    public String getTeamName() {
        return this.teamName;
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

            Main.plugin.getTeamConfig().set("teams.team"+ (i+1) +".name", team.getTeamName());
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
