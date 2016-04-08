package com.photonburst.VaksUHC;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds methods with a general purpose
 */
public class Utils {
    public static int calculatePlayerCount() {
        int count = 0;
        for (UHCTeam team : VaksUHC.plugin.teamList) {
            count += team.getTeamSize();
        }
        return count;
    }

    /**
     * Converts a string into a Bukkit ChatColor reference
     * @param color             The String to be converted
     * @return                  The Bukkit ChatColor reference
     */
    public static ChatColor convertToColorCodeBuk(String color) {
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

    /**
     * Converts a string into a Bukkit ChatColor reference
     * @param color             The String to be converted
     * @return                  The Bukkit ChatColor reference
     */
    public static net.md_5.bungee.api.ChatColor convertToColorCodeBun(String color) {
        Map<String, net.md_5.bungee.api.ChatColor> colorMap = new HashMap<>();
        colorMap.put("Black", net.md_5.bungee.api.ChatColor.BLACK);
        colorMap.put("Dark Blue", net.md_5.bungee.api.ChatColor.DARK_BLUE);
        colorMap.put("Dark Green", net.md_5.bungee.api.ChatColor.DARK_GREEN);
        colorMap.put("Dark Aqua", net.md_5.bungee.api.ChatColor.DARK_AQUA);
        colorMap.put("Dark Red", net.md_5.bungee.api.ChatColor.DARK_RED);
        colorMap.put("Dark Purple", net.md_5.bungee.api.ChatColor.DARK_PURPLE);
        colorMap.put("Gold", net.md_5.bungee.api.ChatColor.GOLD);
        colorMap.put("Gray", net.md_5.bungee.api.ChatColor.GRAY);
        colorMap.put("Dark Gray", net.md_5.bungee.api.ChatColor.DARK_GRAY);
        colorMap.put("Blue", net.md_5.bungee.api.ChatColor.BLUE);
        colorMap.put("Green", net.md_5.bungee.api.ChatColor.GREEN);
        colorMap.put("Aqua", net.md_5.bungee.api.ChatColor.AQUA);
        colorMap.put("Red", net.md_5.bungee.api.ChatColor.RED);
        colorMap.put("Light Purple", net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        colorMap.put("Yellow", net.md_5.bungee.api.ChatColor.YELLOW);
        colorMap.put("White", net.md_5.bungee.api.ChatColor.WHITE);

        return colorMap.get(color);
    }

    /**
     * Replaces conventional output methods to decrease maintenance inside of the plugin
     * @param msg               The message to be displayed
     */
    public static void println(String msg) {
        System.out.println("[VaksUHC] "+ msg);
    }

    public static String secToMin(int i) {
        int ms = i / 60;
        int ss = i % 60;

        String m = (ms < 10 ? "0" : "") + ms;
        String s = (ss < 10 ? "0" : "") + ss;

        return m + ":" + s;
    }

    /**
     * Converts a string to camel case, separated by spaces
     * @param s                 The String which is to be converted
     * @return                  A camel cased string, transformed from s
     */
    public static String toCamelCase(String s) {
        String result = "";

        if(s.length() == 0) { return ""; } else {
            for (String part : s.split(" ")) {
                result += Character.toUpperCase(part.charAt(0));
                if (s.length() > 1) {
                    result += part.substring(1, part.length()).toLowerCase();
                }
            }
        }

        return result;
    }
}
