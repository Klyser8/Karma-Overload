package com.github.klyser8.karmaoverload.util;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.storage.DebugLevel;
import com.github.klyser8.karmaoverload.storage.Preferences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class RandomUtil {

    public static void debugMessage(KarmaOverload plugin, Object value, DebugLevel level) {
        if (plugin.getPreferences().getDebugLevel() != level) return;
        Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[Karma] - " + ChatColor.RESET + value);
    }

    public static void errorMessage(KarmaOverload plugin, Object value, DebugLevel level) {
        if (plugin.getPreferences().getDebugLevel() != level) return;
        Bukkit.getLogger().severe(ChatColor.DARK_PURPLE + "[Karma] - " + ChatColor.RED + value);
    }

}
