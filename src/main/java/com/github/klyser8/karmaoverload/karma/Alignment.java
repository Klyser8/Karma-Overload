package com.github.klyser8.karmaoverload.karma;

import com.github.klyser8.karmaoverload.api.KarmaAction;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.actions.KarmaActionType;
import com.github.klyser8.karmaoverload.karma.effects.KarmaEffectType;
import net.md_5.bungee.api.ChatColor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Stores all data related to an alignment. New alignments can be
 * created by creating new instances of this class, but it is strongly
 * suggested to instead create YAML files for each alignment wanted, as
 * {@link AlignmentFactory} will create the Alignment objects itself.
 */
public class Alignment {

    private final int lowThreshold, highThreshold;
    private final String name;
    private final ChatColor color;
    private final Sound alignSound;
    private final boolean particles;

    private final double killPenalty;
    private final double karmaLimit;
    private final double gainRepeatMultiplier;
    private final double lossRepeatMultiplier;

    private final Map<KarmaEffectType, KarmaEffect> karmaEffects;
    private final Map<KarmaActionType, KarmaAction> karmaActions;
    private final List<String> commands;

    public Alignment(int lowThreshold, int highThreshold, String name, ChatColor color, Sound alignSound, boolean particles, double killPenalty, double karmaLimit,
                     double gainRepeatMultiplier, double lossRepeatMultiplier, Map<KarmaActionType, KarmaAction> karmaActions, Map<KarmaEffectType,
                     KarmaEffect> karmaEffects, List<String> commands) {
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        this.name = name;
        this.color = color;
        this.alignSound = alignSound;
        this.particles = particles;
        this.karmaLimit = karmaLimit;
        this.killPenalty = killPenalty;
        this.gainRepeatMultiplier = gainRepeatMultiplier;
        this.lossRepeatMultiplier = lossRepeatMultiplier;
        this.karmaActions = Collections.unmodifiableMap(karmaActions);
        this.karmaEffects = Collections.unmodifiableMap(karmaEffects);
        this.commands = Collections.unmodifiableList(commands);
    }

    public int getLowThreshold() {
        return lowThreshold;
    }

    public int getHighThreshold() {
        return highThreshold;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public double getKillPenalty() {
        return killPenalty;
    }

    public double getGainRepeatMultiplier() {
        return gainRepeatMultiplier;
    }

    public double getLossRepeatMultiplier() {
        return lossRepeatMultiplier;
    }

    public Map<KarmaEffectType, KarmaEffect> getKarmaEffects() {
        return karmaEffects;
    }

    public List<String> getCommands() {
        return commands;
    }

    public Map<KarmaActionType, KarmaAction> getKarmaActions() {
        return karmaActions;
    }

    public double getKarmaLimit() {
        return karmaLimit;
    }

    /**
     * Returns the sound effect that should play upon reaching
     * this alignment.
     * @return {@link Sound}
     */
    public Sound getAlignSound() {
        return alignSound;
    }

    /**
     * Returns whether this alignment should play particles
     * upon being reached.
     * @return boolean
     */
    public boolean isParticles() {
        return particles;
    }
}
