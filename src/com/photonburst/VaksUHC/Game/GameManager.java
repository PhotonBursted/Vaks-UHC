package com.photonburst.VaksUHC.Game;

import com.photonburst.VaksUHC.Managers.PlayerManager;
import com.photonburst.VaksUHC.ScoreBoard.ScoreBoard;
import com.photonburst.VaksUHC.ScoreBoard.Sidebar;
import com.photonburst.VaksUHC.VaksUHC;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;

import java.util.List;

public class GameManager {
    private static boolean gameInProgress = false;
    public static void startGame() {
        new GameManager().setGameOptions();

        // Assign all players to their teams
        PlayerManager.setUpPlayers();

        // Generate sidebar
        (new Sidebar(ScoreBoard.o)).runTaskTimer(VaksUHC.plugin, 0, 20);
    }

    public void setGameOptions() {
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
