package com.github.klyser8.karmaoverload.karma.effects;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.api.Sound;

public class DropChanceEffect extends KarmaEffect {

    private final double chanceMultiplier;
    private final Sound sound;

    public DropChanceEffect(KarmaOverload plugin, double chance, double dropChanceMultiplier, String permission, Sound sound) {
        super(plugin, chance, permission);
        this.chanceMultiplier = dropChanceMultiplier;
        this.sound = sound;
    }

    public double getChanceMultiplier() {
        return chanceMultiplier;
    }

    public Sound getSound() {
        return sound;
    }
}
