package com.photonburst.VaksUHC;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends JavaPlugin {
    ArrayList<Team> teamList = new ArrayList<>();
    public static Main plugin;

    File configf, teamsf;
    static FileConfiguration config, teams;

    private void createConfigs() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            configf = new File(getDataFolder(), "config.yml");
            teamsf = new File(getDataFolder(), "teams.yml");
            if (!configf.exists()) {
                Utils.println("config.yml not found, creating!");
                saveResource("config.yml", false);
                saveDefaultConfig();
            } else {
                Utils.println("config.yml found, loading!");
            }

            if (!teamsf.exists()) {
                Utils.println("teams.yml not found, creating!");
                saveResource("teams.yml", false);
            } else {
                Utils.println("teams.yml found, loading!");
            }

            config = new YamlConfiguration();
            teams = new YamlConfiguration();
            teamList = Team.getTeams(teamList);
            ScoreBoard.setup();

            try {
                config.load(configf);
                teams.load(teamsf);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getTeamConfig() {
        return teams;
    }

    @Override
    public void onEnable() {
        plugin = this;

        Utils.println("Whoo! Bring in the murder! :D");
        createConfigs();

        getServer().getPluginManager().registerEvents(new EventCatcher(), this);

        this.getCommand("vuhc").setExecutor(new CommandMain());
    }

    @Override
    public void onDisable(){
        ScoreBoard.cleanup();

        Utils.println("Really... You're killing all the fun!");
        Utils.println("...get it? Killing? ....kay I'm leaving now");

        plugin = null;
    }
}