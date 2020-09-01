package com.github.klyser8.karmaoverload.karma.effects;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.api.Sound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LightningEffect extends KarmaEffect {

    private final Sound prepareSound;
    private final Sound strikeSound;

    public LightningEffect(KarmaOverload plugin, double chance, String permission, Sound prepareSound, Sound strikeSound) {
        super(plugin, chance, permission);
        this.prepareSound = prepareSound;
        this.strikeSound = strikeSound;
    }

    public void strikePlayer(Player player, Location loc) {
        World world = player.getWorld();
        if (world.getHighestBlockAt(player.getLocation()).getY() > player.getEyeLocation().getY() && !world.getHighestBlockAt(player.getLocation()).isPassable()) return;
        world.strikeLightning(loc);
    }

    public Sound getPrepareSound() {
        return prepareSound;
    }

    public Sound getStrikeSound() {
        return strikeSound;
    }
}
