package com.github.klyser8.karmaoverload.api;

import com.github.klyser8.karmaoverload.Karma;

/**
 * Parent class of all Karma Effects. If any new Karma Effect is
 * created, it should extend this class in order to be recognized
 * by the plugin.
 */
public abstract class KarmaEffect {

    protected final String permission;
    protected final double chance;

    protected Karma plugin;
    public KarmaEffect(Karma plugin, double chance, String permission) {
        this.permission = permission;
        if (chance == 0) this.chance = 1; //If chance is left out in the yml, then the effect shall take place all the time
        else this.chance = chance;
        this.plugin = plugin;
    }

    public String getPermission() {
        return permission;
    }

    public double getChance() {
        return chance;
    }
}
