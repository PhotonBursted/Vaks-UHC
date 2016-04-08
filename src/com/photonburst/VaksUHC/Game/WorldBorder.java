package com.photonburst.VaksUHC.Game;

import com.photonburst.VaksUHC.Exceptions.OptionNotConfiguredException;
import com.photonburst.VaksUHC.ScoreBoard.Sidebar;
import com.photonburst.VaksUHC.VaksUHC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class WorldBorder{
    public void build(double sizeInit, double sizeRes, long shrinkMins, long delayMins) {
        World world = Bukkit.getWorld("world");
        org.bukkit.WorldBorder border = world.getWorldBorder();

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
        border.setSize(sizeInit);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (sizeInit != sizeRes) {
                    if(VaksUHC.plugin.getConfig().getBoolean("game.env.worldborder.chat.shrinkStateChange")) {
                        Bukkit.getServer().spigot().broadcast(new ComponentBuilder("-=[!] ")
                                .color(ChatColor.DARK_RED)
                                .bold(true)
                                .append("WORLDBORDER IS NOW SHRINKING! ("+ Math.round(border.getSize()) +"x"+ Math.round(border.getSize()) +")")
                                .color(ChatColor.RED)
                                .bold(false)
                                .append(" [!]=-")
                                .color(ChatColor.DARK_RED)
                                .bold(true)
                                .create()
                        );
                    }

                    border.setSize(sizeRes, shrinkMins * 60 / 60);

                    if (VaksUHC.plugin.getConfig().getInt("game.env.worldborder.chat.shrinkInterval") != 0) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getServer().spigot().broadcast(new ComponentBuilder("-=[!] ")
                                        .color(ChatColor.DARK_RED)
                                        .bold(true)
                                        .append("WORLDBORDER UPDATE! ("+ Math.round(border.getSize()) +"x"+ Math.round(border.getSize()) +")")
                                        .color(ChatColor.RED)
                                        .bold(false)
                                        .append(" [!]=-")
                                        .color(ChatColor.DARK_RED)
                                        .bold(true)
                                        .create()
                                );

                                if(Sidebar.episodeCount * VaksUHC.plugin.getConfig().getInt("game.sidebar.timer-length") >= shrinkMins + delayMins) {
                                    cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(VaksUHC.plugin, VaksUHC.plugin.getConfig().getInt("game.env.worldborder.chat.shrinkInterval") * 60 * 20,
                        VaksUHC.plugin.getConfig().getInt("game.env.worldborder.chat.shrinkInterval") * 60 * 20);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getServer().spigot().broadcast(new ComponentBuilder("-=[!] ")
                                    .color(ChatColor.DARK_RED)
                                    .bold(true)
                                    .append("WORLDBORDER HAS STOPPED SHRINKING! ("+ Math.round(border.getSize()) +"x"+ Math.round(border.getSize()) +")")
                                    .color(ChatColor.RED)
                                    .bold(false)
                                    .append(" [!]=-")
                                    .color(ChatColor.DARK_RED)
                                    .bold(true)
                                    .create()
                            );
                        }
                    }.runTaskLaterAsynchronously(VaksUHC.plugin, shrinkMins * 60 * 20);
                }
            }
        }.runTaskLaterAsynchronously(VaksUHC.plugin, delayMins * 60 * 20);
    }
}