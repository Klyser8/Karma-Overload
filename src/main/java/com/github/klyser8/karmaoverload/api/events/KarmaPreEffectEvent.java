package com.github.klyser8.karmaoverload.api.events;

import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.karma.Alignment;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.effects.KarmaEffectType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called right before a {@link KarmaEffect} takes place.
 *
 * Cancelling this event will prevent the Effect from happening.
 */
public class KarmaPreEffectEvent extends Event implements Cancellable {

    private boolean cancelled = false;
    private final KarmaProfile profile;
    private final KarmaEffectType type;
    private final KarmaEffect effect;

    private static final HandlerList HANDLERS = new HandlerList();

    public KarmaPreEffectEvent(KarmaProfile profile, KarmaEffectType type, KarmaEffect effect) {
        this.profile = profile;
        this.type = type;
        this.effect = effect;
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

    public KarmaEffectType getType() {
        return type;
    }

    public KarmaEffect getEffect() {
        return effect;
    }
}
