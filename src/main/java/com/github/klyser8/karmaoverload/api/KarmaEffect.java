package com.github.klyser8.karmaoverload.api;

import com.github.klyser8.karmaoverload.KarmaOverload;

public abstract class KarmaEffect {

    protected final String permission;
    protected final double chance;

    protected KarmaOverload plugin;
    public KarmaEffect(KarmaOverload plugin, double chance, String permission) {
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