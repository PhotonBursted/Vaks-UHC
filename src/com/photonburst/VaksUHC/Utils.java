package com.photonburst.VaksUHC;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static ChatColor convertToColorCode(String color) {
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

    public static void println(String msg) {
        System.out.println("[VaksUHC] "+ msg);
    }

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
