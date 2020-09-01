package com.github.klyser8.karmaoverload.api.events;

import com.github.klyser8.karmaoverload.karma.Alignment;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AlignmentChangeEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    private final KarmaProfile profile;
    private final Alignment oldAlignment;
    private final Alignment newAlignment;

    private static final HandlerList HANDLERS = new HandlerList();

    public AlignmentChangeEvent(KarmaProfile profile, Alignment oldAlignment, Alignment newAlignment) {
        this.profile = profile;
        this.oldAlignment = oldAlignment;
        this.newAlignment = newAlignment;
    }

    public KarmaProfile getProfile() {
        return profile;
    }

    public Alignment getOldAlignment() {
        return oldAlignment;
    }

    public Alignment getNewAlignment() {
        return newAlignment;
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
}
