package com.github.klyser8.karmaoverload.storage;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.karma.Alignment;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Preferences {

    public static final int JSON_STORAGE = 1;
    public static final int MYSQL_STORAGE = 2;

    private String dbName, host, port, user, password;

    private String language;

    private DebugLevel debugLevel = DebugLevel.NONE;
    private boolean commandSounds = true;

    private int storageType = 1;
    private int saveInterval = 120;
    private int karmaLimitResetInterval = 3600;

    private Particle karmaGainParticle = null;
    private Particle karmaLossParticle = null;

    private int historySize = 25;
    private int startingScore = 0;
    private int lowLimit;
    private int highLimit;

    private boolean softCap = true;

    private boolean worldListEnabler;
    private final List<World> worldList;
    private final List<GameMode> enabledGameModes;

    private final Karma plugin;
    public Preferences(Karma plugin) {
        this.plugin = plugin;
        this.worldList = new ArrayList<>();
        this.enabledGameModes = new ArrayList<>();
    }

    public void loadPreferences() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        language = config.getString("Language");

        debugLevel = DebugLevel.valueOf(Objects.requireNonNull(config.getString("Debug Level")).toUpperCase());
        commandSounds = config.getBoolean("Command Sound");

        storageType = config.getInt("Storage Type");
        if (storageType == MYSQL_STORAGE) {
            dbName = config.getString("Database.Name");
            host = config.getString("Database.Host");
            port = config.getString("Database.Port");
            user = config.getString("Database.Username");
            password = config.getString("Database.Password");
        }

        saveInterval = config.getInt("Autosave Interval");
        karmaLimitResetInterval = config.getInt("Karma Limit Reset Interval");


        if (config.getString("Karma Gain Particle") != null) {
            karmaGainParticle = Particle.valueOf(config.getString("Karma Gain Particle"));
        }
        if (config.getString("Karma Loss Particle") != null) {
            karmaLossParticle = Particle.valueOf(config.getString("Karma Loss Particle"));
        }

        historySize = config.getInt("Karma History Size");
        startingScore = config.getInt("Starting Score");
        softCap = config.getBoolean("Karma Soft Cap");
        for (Alignment alignment : plugin.getAlignments()) {
            if (alignment.getLowThreshold() < lowLimit) lowLimit = alignment.getLowThreshold();
            if (alignment.getHighThreshold() > highLimit) highLimit = alignment.getHighThreshold();
        }
        enabledGameModes.clear();
        for (String gameModeString : config.getStringList("Enabled GameModes")) {
            enabledGameModes.add(GameMode.valueOf(gameModeString));
        }

        worldListEnabler = config.getBoolean("Enabled World List");
        worldList.clear();
        for (String worldName : config.getStringList("Worlds")) {
            worldList.add(Bukkit.getWorld(worldName));
        }
        plugin.reloadDB();
        plugin.getLanguageHandler().setup();
    }

    public DebugLevel getDebugLevel() {
        return debugLevel;
    }

    public String getLanguage() {
        return language;
    }

    public int getStorageType() {
        return storageType;
    }

    public int getStartingScore() {
        return startingScore;
    }

    public int getLowLimit() {
        return lowLimit;
    }

    public int getHighLimit() {
        return highLimit;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }

    public int getHistorySize() {
        return historySize;
    }

    public int getKarmaLimitResetInterval() {
        return karmaLimitResetInterval;
    }

    public boolean isSoftCap() {
        return softCap;
    }

    public boolean isCommandSoundsEnabled() {
        return commandSounds;
    }

    public boolean isWorldListEnabler() {
        return worldListEnabler;
    }

    public List<World> getWorldList() {
        return worldList;
    }

    public List<GameMode> getEnabledGameModes() {
        return enabledGameModes;
    }

    public Particle getKarmaGainParticle() {
        return karmaGainParticle;
    }

    public Particle getKarmaLossParticle() {
        return karmaLossParticle;
    }
}
