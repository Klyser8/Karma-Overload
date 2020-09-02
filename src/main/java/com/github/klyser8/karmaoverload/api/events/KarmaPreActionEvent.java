package com.github.klyser8.karmaoverload.api.events;

import com.github.klyser8.karmaoverload.api.KarmaAction;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.actions.KarmaActionType;
import com.github.klyser8.karmaoverload.karma.effects.KarmaEffectType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called right before a {@link KarmaAction} takes place.
 *
 * Cancelling this event will prevent the Action from changing the
 * player's Karma score.
 */
public class KarmaPreActionEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    private final KarmaProfile profile;
    private final KarmaActionType type;
    private final KarmaAction action;

    private static final HandlerList HANDLERS = new HandlerList();

    public KarmaPreActionEvent(KarmaProfile profile, KarmaActionType type, KarmaAction action) {
        this.profile = profile;
        this.type = type;
        this.action = action;
    }

    public KarmaProfile getProfile() {
        return profile;
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

    public KarmaActionType getType() {
        return type;
    }

    public KarmaAction getAction() {
        return action;
    }
}
