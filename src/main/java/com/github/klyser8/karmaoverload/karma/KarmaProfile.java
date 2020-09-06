package com.github.klyser8.karmaoverload.karma;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.KarmaWriter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KarmaProfile {

    private final Karma plugin;

    private final Player player;
    private double karma;
    private Alignment alignment;

    private final List<HistoryEntry> history;

    public KarmaProfile(Karma plugin, Player player) {
        this.player = player;
        this.plugin = plugin;
        karma = plugin.getPreferences().getStartingScore();
        history = new ArrayList<>();

        for (Alignment alignment : plugin.getAlignments()) {
            if (!(alignment.getLowThreshold() < karma && karma < alignment.getHighThreshold())) continue;
            this.alignment = alignment;
            break;
        }
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public double getKarma() {
        return karma;
    }


    /**
     * Sets a players Karma to the provided amount.
     *
     * @deprecated changing Karma using this method does not trigger events. Use
     * {@link KarmaWriter#setKarma(Karma, KarmaProfile, double, KarmaSource)}
     * Rounds up/down to one decimal place.
     *
     * @param score = Karma score to set to
     */
    @Deprecated
    public void setKarma(double score) {
        this.karma = (double) Math.round(score * 10) / 10;
    }

    public Player getPlayer() {
        return player;
    }

    public List<HistoryEntry> getHistory() {
        return history;
    }

    public void addHistoryEntry(Date date, KarmaSource source, double change) {
        int maxSize = plugin.getPreferences().getHistorySize();
        if (history.size() >= maxSize) {
            history.remove(history.get(0));
        }
        history.add(new HistoryEntry(date, source, (double) Math.round(change * 10) / 10));
    }


    /**
     * Class which contains a player's karma history data.
     *
     */
    public static class HistoryEntry {

        private final Date date;
        private final KarmaSource source;
        private final double amount;

        private HistoryEntry(Date date, KarmaSource source, double amount) {
            this.date = date;
            this.source = source;
            this.amount = amount;
        }

        public Date getDate() {
            return date;
        }

        public KarmaSource getSource() {
            return source;
        }

        public double getAmount() {
            return amount;
        }
    }
}
