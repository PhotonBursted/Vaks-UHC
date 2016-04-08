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

import java.util.Collection;
import java.util.List;

public class GameManager {
    private static boolean gameInProgress = false;
    public static void startGame() {
        new GameManager().setGameOptions();

        // Assign all players to their teams
        setUpMatch();

        // Generate sidebar
        (new Sidebar()).runTaskTimerAsynchronously(VaksUHC.plugin, 0, 20);

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathManager(), VaksUHC.plugin);

        // Set world border
        new WorldBorder().build(VaksUHC.plugin.getConfig().getInt("game.env.worldborder.size.initial"),
                                VaksUHC.plugin.getConfig().getInt("game.env.worldborder.size.final"),
                                VaksUHC.plugin.getConfig().getInt("game.env.worldborder.size.shrinkDuration"),
                                VaksUHC.plugin.getConfig().getInt("game.env.worldborder.size.shrinkDelay"));
    }

    public static void setUpMatch() {
        PlayerDeathManager.killed.clear();
        VaksUHC.plugin.playerMap = UHCTeam.getPlayerMap(VaksUHC.plugin.teamList);

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        PlayerJoinManager.assignPlayers(Bukkit.getOnlinePlayers());

        players.forEach(PlayerJoinManager::setUpPlayer);
    }

    private void setGameOptions() {
        gameInProgress = true;

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

    public static boolean isRunning() {
        return gameInProgress;
    }
}
