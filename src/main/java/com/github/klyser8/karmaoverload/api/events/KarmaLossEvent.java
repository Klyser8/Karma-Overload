package com.github.klyser8.karmaoverload.api.events;

import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.KarmaSource;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KarmaLossEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    private final KarmaProfile profile;
    private final double oldKarma;
    private final double newKarma;
    private double lostKarma;
    private KarmaSource source;

    private static final HandlerList HANDLERS = new HandlerList();

    public KarmaLossEvent(KarmaProfile profile, double oldKarma, double newKarma, double gainedKarma, KarmaSource source) {
        this.profile = profile;
        this.oldKarma = oldKarma;
        this.newKarma = newKarma;
        this.lostKarma = gainedKarma;
        this.source = source;
    }

    public KarmaProfile getProfile() {
        return profile;
    }

    public double getOldKarma() {
        return oldKarma;
    }

    public double getNewKarma() {
        return newKarma;
    }

    public double getLostKarma() {
        return lostKarma;
    }
    public void setLostKarma(double lostKarma) {
        this.lostKarma = lostKarma;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public KarmaSource getSource() {
        return source;
    }

    public void setSource(KarmaSource source) {
        this.source = source;
    }
}
