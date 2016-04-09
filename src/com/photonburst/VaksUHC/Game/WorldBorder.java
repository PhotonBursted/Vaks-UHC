package com.photonburst.VaksUHC.Game;

import com.photonburst.VaksUHC.Exceptions.OptionNotConfiguredException;
import com.photonburst.VaksUHC.ScoreBoard.Sidebar;
import com.photonburst.VaksUHC.VaksUHC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Controls the world border movement during the match
 */
public class WorldBorder{
    /**
     * Create a world border of given dimensions and time
     * @param sizeInit              Size of the world border at the start of animation
     * @param sizeRes               Size of the world border at the end of animation
     * @param shrinkMins            The time span over which to shrink
     * @param delayMins             The amount of time to wait before shrinking
     */
    public void build(double sizeInit, double sizeRes, long shrinkMins, long delayMins) {
        // Get the world border of the main world
        World world = Bukkit.getWorld("world");
        org.bukkit.WorldBorder border = world.getWorldBorder();

        // Determine what the warning type should be when getting close to the world border
        String warningType = VaksUHC.plugin.getConfig().getString("game.env.worldborder.visual.type");
        try {
            if (warningType.equals("distance")) {
                border.setWarningDistance(VaksUHC.plugin.getConfig().getInt("game.env.worldborder.visual.amount"));
            } else if (warningType.equals("time")) {
                border.setWarningTime(VaksUHC.plugin.getConfig().getInt("game.env.worldborder.visual.amount"));
            } else if (!warningType.equals("none")) {
                throw new OptionNotConfiguredException("game.env.worldborder.visual.type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the world border to its initial size
        border.setSize(sizeInit);

        // -- Set of timers below, showing occasional alerts and updates concerning the state of the world border
        new BukkitRunnable() {
            @Override
            public void run() {
                // If both dimensions are equal, don't animate
                if (sizeInit != sizeRes) {
                    if(VaksUHC.plugin.getConfig().getBoolean("game.env.worldborder.chat.shrinkStateChange")) {
                        Bukkit.getServer().spigot().broadcast(message("WORLDBORDER IS NOW SHRINKING!", border));
                    }

                    // After waiting out the delay timer, set the world border to animate to its new location
                    border.setSize(sizeRes, shrinkMins * 60);

                    // Get occasional updates on how much the world border has changed in size
                    if (VaksUHC.plugin.getConfig().getInt("game.env.worldborder.chat.shrinkInterval") != 0) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getServer().spigot().broadcast(message("WORLDBORDER UPDATE!", border));

                                // If the timer will run out before the end of the animation, quit the timer
                                if(Sidebar.episodeCount * VaksUHC.plugin.getConfig().getInt("game.sidebar.timer-length") >= shrinkMins + delayMins) {
                                    cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(VaksUHC.plugin, VaksUHC.plugin.getConfig().getInt("game.env.worldborder.chat.shrinkInterval") * 60 * 20,
                        VaksUHC.plugin.getConfig().getInt("game.env.worldborder.chat.shrinkInterval") * 60 * 20);
                    }

                    // Timer keeping track of when the world border actually stops changing
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().spigot().broadcast(message("WORLDBORDER HAS STOPPED SHRINKING!", border));
                        }
                    }.runTaskLaterAsynchronously(VaksUHC.plugin, shrinkMins * 60 * 20);
                } else {
                    cancel();
                }
            }
        }.runTaskLaterAsynchronously(VaksUHC.plugin, delayMins * 60 * 20);
    }

    /**
     * Convenience method to make sending a message concerning the world border a little more compact
     * @param msg               The message to be sent
     * @param border            The world border object to target
     * @return                  An object suitable for broadcasting
     */
    private static BaseComponent[] message(String msg, org.bukkit.WorldBorder border) {
        return new ComponentBuilder("-=[!] ")
            .color(ChatColor.DARK_RED)
            .bold(true)
            .append(msg +" ("+ Math.round(border.getSize()) +"x"+ Math.round(border.getSize()) +")")
            .color(ChatColor.RED)
            .bold(false)
            .append(" [!]=-")
            .color(ChatColor.DARK_RED)
            .bold(true)
            .create();

    }
}