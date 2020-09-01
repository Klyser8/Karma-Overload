package com.github.klyser8.karmaoverload.karma.effects;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.api.Sound;

public class KarmaMultiplierEffect extends KarmaEffect {

    private final double gainMultiplier;
    private final double lossMultiplier;
    private final Sound gainSound;
    private final Sound lossSound;

    public KarmaMultiplierEffect(KarmaOverload plugin, double chance, double positiveMult, double negativeMult, String permission, Sound gainSound, Sound lossSound) {
        super(plugin, chance, permission);
        this.gainMultiplier = positiveMult;
        this.lossMultiplier = negativeMult;
        this.gainSound = gainSound;
        this.lossSound = lossSound;
    }

    public double getGainMultiplier() {
        return gainMultiplier;
    }

    public double getLossMultiplier() {
        return lossMultiplier;
    }

    public Sound getGainSound() {
        return gainSound;
    }

    public Sound getLossSound() {
        return lossSound;
    }
}
