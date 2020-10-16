package com.github.klyser8.karmaoverload.storage;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import static com.github.klyser8.karmaoverload.util.RandomUtil.debugMessage;
import static com.github.klyser8.karmaoverload.util.RandomUtil.errorMessage;

public class ProfileWriter {

    private final Karma plugin;
    private final Preferences pref;

    private final BukkitRunnable saveRunnable;
    public ProfileWriter(Karma plugin) {
        this.plugin = plugin;
        pref = plugin.getPreferences();
        saveRunnable = new SaveRunnable();
    }

    /*public void saveProfileYaml(KarmaProfile profile) {
        Player player = profile.getPlayer();
        File playerFile = new File(plugin.getDataFolder() + File.separator + "players", player.getUniqueId().toString() + ".plr");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        try {
            playerConfig.set("Karma Score", profile.getKarma());
            playerConfig.save(playerFile);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "(Players) Could not save " + player.getUniqueId().toString() + ".plr!");
        }
    }*/

    public void saveProfileGson(KarmaProfile profile) {
        if (profile == null) {
            errorMessage(plugin, "A player's Karma Profile was null. The server should immediately be restarted, in order to prevent data loss", DebugLevel.NONE);
            return;
        }
        Player player = profile.getPlayer();

        File playerDir = new File(plugin.getDataFolder(), "players");
        if (!playerDir.exists()) playerDir.mkdir();

        File playerFile = new File(plugin.getDataFolder() + File.separator + "players", player.getUniqueId().toString() + ".json");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        DeserializedKarmaProfile deserializedProfile = new DeserializedKarmaProfile(profile.getHistory(), profile.getKarma());
        String scoreData = gson.toJson(deserializedProfile);
        try {
            FileWriter writer = new FileWriter(playerFile, false);
            writer.write(scoreData);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveProfileDB(KarmaProfile profile) {
        if (profile == null) {
            errorMessage(plugin, "A player's Karma Profile was null. The server should immediately be restarted, in order to prevent data loss", DebugLevel.NONE);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
            String uuid = profile.getPlayer().getUniqueId().toString();

            String updateSQLQuery = "UPDATE KarmaTable SET Score = " + profile.getKarma() +
                    " WHERE UUID = '" + profile.getPlayer().getUniqueId().toString() + "';";
            String addUserSQLQuery = "INSERT INTO KarmaTable (UUID)" +
                    " SELECT * FROM (SELECT '" + uuid + "') AS tmp" +
                    " WHERE NOT EXISTS (" +
                    " SELECT UUID From KarmaTable WHERE UUID ='" + uuid + "') LIMIT 1;";

            try {
                PreparedStatement statement = plugin.getConnection().prepareStatement(addUserSQLQuery);
                statement.executeUpdate();
                statement = plugin.getConnection().prepareStatement(updateSQLQuery);
                statement.executeUpdate();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            if (profile.getHistory().size() == 0) return;
            String deleteHistoryQuery = "DELETE FROM KarmaHistory WHERE UUID = '" + uuid + "';";
            String addHistoryQuery = "INSERT INTO KarmaHistory VALUES";
            for (KarmaProfile.HistoryEntry entry : profile.getHistory()) {
                java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                addHistoryQuery += " ('" + uuid + "', '" + sdf.format(entry.getDate()) + "', '" + entry.getSource() + "', " + entry.getAmount() + "),";
            }
            addHistoryQuery = StringUtils.removeEnd(addHistoryQuery, ",") + ";";
            try {
                PreparedStatement statement = plugin.getConnection().prepareStatement(deleteHistoryQuery);
                statement.executeUpdate();
                statement = plugin.getConnection().prepareStatement(addHistoryQuery);
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void startAutoSave() {
        saveRunnable.runTaskTimerAsynchronously(plugin, 20, 20);
    }

    public void stopAutoSave() {
        saveRunnable.cancel();
    }

    private class SaveRunnable extends BukkitRunnable {
        int count = 0;
        @Override
        public void run() {
            count++;
            if (count < pref.getSaveInterval()) return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (pref.getStorageType() == Preferences.JSON_STORAGE) saveProfileGson(plugin.getProfileProvider().getProfile(player));
                else if (pref.getStorageType() == Preferences.MYSQL_STORAGE)
                    saveProfileDB(plugin.getProfileProvider().getProfile(player));
            }
            debugMessage(plugin, "Player data saved automatically", DebugLevel.LOW);
            count = 0;
        }
    }

}
