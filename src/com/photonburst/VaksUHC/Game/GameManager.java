package com.photonburst.VaksUHC.Game;

import com.photonburst.VaksUHC.Managers.PlayerDeathManager;
import com.photonburst.VaksUHC.Managers.PlayerJoinManager;
import com.photonburst.VaksUHC.ScoreBoard.Sidebar;
import com.photonburst.VaksUHC.UHCTeam;
import com.photonburst.VaksUHC.VaksUHC;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.List;

/**
 * Handles most major game mechanics
 */
public class GameManager {
    /**
     * Holds whether a game is running at the moment
     */
    private static boolean gameInProgress = false;

    /**
     * Starts off a new game
     */
    public static void startGame() {
        // Sets environmental options
        new GameManager().setGameOptions();

        // Assigns all players to their teams
        setUpMatch();

        // Generates sidebar
        (new Sidebar()).runTaskTimerAsynchronously(VaksUHC.plugin, 0, 20);

        // Initializes the death listener, so deaths only count when in a match - not while lobbying
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathManager(), VaksUHC.plugin);

        // Set world border
        new WorldBorder().build(VaksUHC.plugin.getConfig().getInt("game.env.worldborder.size.initial"),
                                VaksUHC.plugin.getConfig().getInt("game.env.worldborder.size.final"),
                                VaksUHC.plugin.getConfig().getInt("game.env.worldborder.size.shrinkDuration"),
                                VaksUHC.plugin.getConfig().getInt("game.env.worldborder.size.shrinkDelay"));
    }

    /**
     * Sets up the player side of things
     *
     * @see             PlayerJoinManager#assignPlayer(Player, Team)
     * @see             PlayerJoinManager#setUpPlayer(Player)
     */
    public static void setUpMatch() {
        // Clears the list of killed people
        PlayerDeathManager.killed.clear();
        // Restores the player map
        VaksUHC.plugin.playerMap = UHCTeam.getPlayerMap(VaksUHC.plugin.teamList);

        // Gets all online players
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        // Assigns all players to the teams they were signed up for
        PlayerJoinManager.assignPlayers(Bukkit.getOnlinePlayers());

        players.forEach(PlayerJoinManager::setUpPlayer);
    }

    /**
     * Sets the right environmental features for starting the game
     */
    private void setGameOptions() {
        // Notify the game is running
        gameInProgress = true;

        // Set several settings for all worlds on the server
        List<World> worlds = Bukkit.getWorlds();
        for(World world: worlds) {
            world.setDifficulty(Difficulty.HARD);
            world.setTime(0);
            world.setGameRuleValue("naturalRegeneration", "false");
            world.setGameRuleValue("keepInventory", "false");
            world.setGameRuleValue("doMobSpawning", "true");
            world.setGameRuleValue("doDayLightCycle", VaksUHC.plugin.getConfig().getString("game.env.eternal-day"));
        }
    }

    /**
     * Checks whether a match is underway
     * @return              Whether the game is running or not
     */
    public static boolean isRunning() {
        return gameInProgress;
    }
}
