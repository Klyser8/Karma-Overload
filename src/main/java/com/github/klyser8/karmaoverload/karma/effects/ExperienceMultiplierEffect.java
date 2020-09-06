package com.github.klyser8.karmaoverload.karma.effects;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.api.Sound;

public class ExperienceMultiplierEffect extends KarmaEffect {

    private final double multiplier;
    private final Sound sound;

    public ExperienceMultiplierEffect(Karma plugin, double chance, double multiplier, String permission, Sound sound) {
        super(plugin, chance, permission);
        this.multiplier = multiplier;
        this.sound = sound;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public Sound getSound() {
        return sound;
    }
}
