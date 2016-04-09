package com.photonburst.VaksUHC;

import com.photonburst.VaksUHC.Commands.CmdMain;
import com.photonburst.VaksUHC.Game.WorldBorder;
import com.photonburst.VaksUHC.Managers.ChatManager;
import com.photonburst.VaksUHC.Managers.PlayerJoinManager;
import com.photonburst.VaksUHC.ScoreBoard.ScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Master class
 */
public class VaksUHC extends JavaPlugin {
    /**
     * List of all the UHCTeams in the match
     * @see                 UHCTeam
     * @see                 UHCTeam#getTeams(ArrayList)
     */
    public ArrayList<UHCTeam> teamList = new ArrayList<>();
    /**
     * List of all players alive, linked to their respective team
     */
    public Map<String, String> playerMap = new HashMap<>();

    /**
     * Reference to this plugin instance, meaning other classes can use it too
     */
    public static VaksUHC plugin;
    /**
     * Scoreboard to be used for dividing up teams, showing information, etc.
     */
    public Scoreboard board;

    /**
     * The locations of configuration files
     */
    File configf, teamsf;
    /**
     * The YAML relatives of the Files, able to read and write to YAML files
     */
    private static FileConfiguration config, teams;

    /**
     * Method creating the configuration files if they don't exist yet, loading them if they do
     */
    private void createConfigs() {
        try {
            // If the data folder for the plugin doesn't exist, make it
            if (!getDataFolder().exists()) {
                //noinspection ResultOfMethodCallIgnored
                getDataFolder().mkdirs();
            }

            configf = new File(getDataFolder(), "config.yml");
            teamsf = new File(getDataFolder(), "teams.yml");

            // If a config file doesn't exist, clone it out of the jar
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

            // Create all the UHCTeam instances out of the .csv template
            teamList = UHCTeam.getTeams(teamList);
            // Get the players, linked to their teams
            playerMap = UHCTeam.getPlayerMap(teamList);

            // Set up all the scoreboard teams and objectives
            ScoreBoard.setup();

            // Load the config files into their configurations
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

    /**
     * Gets a reference to the team configuration
     * @return              A reference to the team's YAML configuration
     */
    public FileConfiguration getTeamConfig() {
        return teams;
    }

    /**
     * Runs on enabling the plugin, meaning it's the method initializing everything that has to be initialized
     */
    @Override
    public void onEnable() {
        plugin = this;
        // Create the general scoreboard instance
        board = Bukkit.getScoreboardManager().getNewScoreboard();

        Utils.println("Whoo! Bring in the murder! :D");
        createConfigs();
        // Generate a small world border so people can't travel too far before starting the match
        new WorldBorder().build(150, 150, 0, 0);

        // Register the main listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinManager(), this);
        getServer().getPluginManager().registerEvents(new ChatManager(), this);
        // Register the command controlling the match
        getCommand("vuhc").setExecutor(new CmdMain());
    }

    /**
     * Runs on disabling the plugin, purposed to clean up the things the plugin generated
     */
    @Override
    public void onDisable() {
        // Remove any scoreboards or objectives
        ScoreBoard.cleanup();

        Utils.println("Really... You're killing all the fun!");
        Utils.println("...get it? Killing? ....kay I'm leaving now");

        plugin = null;
        board = null;
    }
}