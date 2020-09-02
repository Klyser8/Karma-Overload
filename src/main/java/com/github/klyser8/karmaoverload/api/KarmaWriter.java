package com.github.klyser8.karmaoverload.api;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.events.AlignmentChangeEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaGainEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaLossEvent;
import com.github.klyser8.karmaoverload.karma.Alignment;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.KarmaSource;
import com.github.klyser8.karmaoverload.storage.DebugLevel;
import com.github.klyser8.karmaoverload.storage.Preferences;
import org.bukkit.Bukkit;

import java.sql.Date;
import java.time.Instant;

import static com.github.klyser8.karmaoverload.util.RandomUtil.debugMessage;

/**
 * Class which holds all necessary methods to update a player's
 * current alignment and increase/decrease their Karma score.
 *
 * All the methods below are static, for ease of access.
 */
public class KarmaWriter {

    private KarmaWriter() {}

    private static void addKarma(KarmaOverload plugin, KarmaProfile profile, double amount, KarmaSource source) {
        if (amount <= 0) return;
        double recentKarma = plugin.getKarmaLimitMap().get(profile.getPlayer());
        KarmaGainEvent event = new KarmaGainEvent(profile, profile.getKarma(), profile.getKarma() + amount, amount, source);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        if (source != KarmaSource.COMMAND && source != KarmaSource.VOTING) {
            if (plugin.getPreferences().isSoftCap()) {
                if (recentKarma >= profile.getAlignment().getKarmaLimit()) return;
            } else {
                if (recentKarma + event.getGainedKarma() > profile.getAlignment().getKarmaLimit()) return;
            }
        }
        profile.setKarma(event.getOldKarma() + event.getGainedKarma());
        profile.addHistoryEntry(Date.from(Instant.now()), source, event.getGainedKarma());
        if (source == KarmaSource.COMMAND || source == KarmaSource.VOTING) return;
        plugin.getKarmaLimitMap().put(profile.getPlayer(), recentKarma + event.getGainedKarma());
    }

    private static void subtractKarma(KarmaOverload plugin, KarmaProfile profile, double amount, KarmaSource source) {
        if (amount <= 0) return;
        double recentKarma = plugin.getKarmaLimitMap().get(profile.getPlayer());
        KarmaLossEvent event = new KarmaLossEvent(profile, profile.getKarma(), profile.getKarma() - amount, amount, source);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        if (source != KarmaSource.COMMAND && source != KarmaSource.VOTING) {
            if (plugin.getPreferences().isSoftCap()) {
                if (recentKarma >= profile.getAlignment().getKarmaLimit()) return;
            } else {
                if (recentKarma + event.getLostKarma() > profile.getAlignment().getKarmaLimit()) return;
            }
        }
        profile.setKarma(event.getOldKarma() - event.getLostKarma());
        profile.addHistoryEntry(Date.from(Instant.now()), source, -event.getLostKarma());
        if (source == KarmaSource.COMMAND || source == KarmaSource.VOTING) return;
        plugin.getKarmaLimitMap().put(profile.getPlayer(), recentKarma + event.getLostKarma());
    }


    /** Changes the player's Karma score based on the amount provided.
     *  A negative amount will decrease the player's Karma score, while
     *  a positive amount will increase it.
     *  Must be called synchronously to prevent crashes.
     *
     *  This method ALWAYS triggers events, unlike
     *  {@link com.github.klyser8.karmaoverload.karma.KarmaProfile#setKarma(double)}
     *
     *  A player's karma cannot exceed the limit established using this method.
     *
     * @param plugin instance of {@link KarmaOverload}
     * @param profile the profile to update.
     * @param amount the amount to alter the player's Karma score by
     * @param source the source which the Karma score comes from. Check {@link com.github.klyser8.karmaoverload.karma.KarmaSource}
     */
    public static void changeKarma(KarmaOverload plugin, KarmaProfile profile, double amount, KarmaSource source) {
        Preferences pref = plugin.getPreferences();
        if (amount < 0) {
            amount = Math.abs(amount);
            if (profile.getKarma() - amount < pref.getLowLimit())  {
                amount = profile.getKarma() - pref.getLowLimit();
            }
            subtractKarma(plugin, profile, amount, source);
        } else {
            if (profile.getKarma() + amount > pref.getHighLimit()) amount = pref.getHighLimit() - profile.getKarma();
            addKarma(plugin, profile, amount, source);
        }
        updateAlignment(plugin, profile, true);
    }


    /** Sets a player's Karma score to the provided amount.
     *  This will trigger a KarmaGainEvent or KarmaLossEvent based on
     *  the difference between the new Karma and the old Karma.
     *
     *  A player's karma cannot exceed the limit established using this method.
     *
     * @param plugin plugin instance of {@link KarmaOverload}
     * @param profile the profile to update
     * @param karma the new karma score to set.
     * @param source the source which the karma came from. Check {@link com.github.klyser8.karmaoverload.karma.KarmaSource}
     */
    public static void setKarma(KarmaOverload plugin, KarmaProfile profile, double karma, KarmaSource source) {
        if (karma > plugin.getPreferences().getHighLimit()) karma = plugin.getPreferences().getHighLimit();
        double change = karma - profile.getKarma();
        KarmaWriter.changeKarma(plugin, profile, change, source);
    }


    /** Updates the player's Karma alignment. This method should be used
     *  after the player's Karma score has been changed without using
     *  {@link #changeKarma(KarmaOverload, KarmaProfile, double, KarmaSource)}
     *
     * @param plugin instance of {@link KarmaOverload}
     * @param profile the profile to update.
     * @param triggerEvent whether this method should trigger a {@link com.github.klyser8.karmaoverload.api.events.AlignmentChangeEvent}
     */
    public static void updateAlignment(KarmaOverload plugin, KarmaProfile profile, boolean triggerEvent) {
        for (Alignment alignment : plugin.getAlignments()) {
            if (!(alignment.getLowThreshold() <= profile.getKarma() && profile.getKarma() <= alignment.getHighThreshold())) continue;
            if (profile.getAlignment() != null && profile.getAlignment().getName().equalsIgnoreCase(alignment.getName())) return;
            if (triggerEvent) {
                AlignmentChangeEvent event = new AlignmentChangeEvent(profile, profile.getAlignment(), alignment);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) return;
            }
            profile.setAlignment(alignment);
        }
    }

}
