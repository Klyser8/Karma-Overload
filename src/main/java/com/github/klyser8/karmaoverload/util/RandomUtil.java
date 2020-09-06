package com.github.klyser8.karmaoverload.util;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.storage.DebugLevel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomUtil {

    private static final Random random = new Random();

    public static void debugMessage(Karma plugin, Object value, DebugLevel level) {
        if (plugin.getPreferences().getDebugLevel().getLevel() < level.getLevel()) return;
        Bukkit.getLogger().info(ChatColor.LIGHT_PURPLE + "[Karma] - " + ChatColor.RESET + value);
    }

    public static void errorMessage(Karma plugin, Object value, DebugLevel level) {
        if (plugin.getPreferences().getDebugLevel().getLevel() < level.getLevel()) return;
        Bukkit.getLogger().severe(ChatColor.DARK_PURPLE + "[Karma] - " + ChatColor.RED + value);
    }

    /**
     * Checks if the current version of the server is the one specified.
     *
     * @param versions versions to compare to the server version.
     * @return true if one of written versions is the server's version.
     */
    public static boolean isVersion(final String... versions) {
        if (versions == null) {
            return false;
        }

        for (int i = 0; i < versions.length; i++) {
            if (Karma.VERSION.contains(versions[i])) return true;
        }

        return false;
    }

    public static Player pickRandomPlayer(List<Player> playerList) {
        return playerList.get(random.nextInt(playerList.size()));
    }


}
