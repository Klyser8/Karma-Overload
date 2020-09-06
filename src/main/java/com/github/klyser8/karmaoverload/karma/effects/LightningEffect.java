package com.github.klyser8.karmaoverload.karma.effects;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.api.Sound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LightningEffect extends KarmaEffect {

    private final Sound prepareSound;
    private final Sound strikeSound;

    public LightningEffect(Karma plugin, double chance, String permission, Sound prepareSound, Sound strikeSound) {
        super(plugin, chance, permission);
        this.prepareSound = prepareSound;
        this.strikeSound = strikeSound;
    }

    public void strikePlayer(Player player) {
        World world = player.getWorld();
        world.strikeLightning(world.getHighestBlockAt(player.getLocation()).getLocation());
    }

    public Sound getPrepareSound() {
        return prepareSound;
    }

    public Sound getStrikeSound() {
        return strikeSound;
    }
}
