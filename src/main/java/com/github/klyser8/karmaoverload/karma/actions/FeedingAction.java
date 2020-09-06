package com.github.klyser8.karmaoverload.karma.actions;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.Sound;

public class FeedingAction extends GenericAction {

    private final boolean babiesEnabled;

    public FeedingAction(Karma plugin, double amount, double chance, boolean babiesEnabled, String permission, Sound sound) {
        super(plugin, amount, chance, permission, sound);
        this.babiesEnabled = babiesEnabled;
    }

    public boolean isBabiesEnabled() {
        return babiesEnabled;
    }
}
