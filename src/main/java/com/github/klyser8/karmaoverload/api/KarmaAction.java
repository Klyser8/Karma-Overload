package com.github.klyser8.karmaoverload.api;

import com.github.klyser8.karmaoverload.KarmaOverload;

public abstract class KarmaAction {

    protected final KarmaOverload plugin;
    protected final double amount;
    protected final double chance;
    protected final String permission;

    public KarmaAction(KarmaOverload plugin, double amount, double chance, String permission) {
        this.plugin = plugin;
        this.amount = amount;
        this.chance = chance;
        this.permission = permission;
    }

    public double getChance() {
        return chance;
    }

    public double getAmount() {
        return amount;
    }

    public String getPermission() {
        return permission;
    }
}
