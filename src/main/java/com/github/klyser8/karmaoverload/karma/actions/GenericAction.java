package com.github.klyser8.karmaoverload.karma.actions;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaAction;
import com.github.klyser8.karmaoverload.api.Sound;

public class GenericAction extends KarmaAction {

    private final Sound sound;

    public GenericAction(KarmaOverload plugin, double amount, double chance, String permission, Sound sound) {
        super(plugin, amount, chance, permission);
        this.sound = sound;
    }

    public Sound getSound() {
        return sound;
    }

}
