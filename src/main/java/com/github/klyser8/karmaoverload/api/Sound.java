package com.github.klyser8.karmaoverload.api;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.List;

public class Sound {

    private final String soundName;
    private float pitch;
    private float volume;

    public Sound(String name, float volume, float pitch) {
        this.soundName = name;
        this.pitch = pitch;
        this.volume = volume;
    }

    public String getSoundName() {
        return soundName;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void play(Location loc, SoundCategory category, List<Player> players) {
        for (Player player : players) {
            player.playSound(loc, soundName, category, volume, pitch);
        }
    }

    public void play(Location loc, List<Player> players) {
        for (Player player : players) {
            player.playSound(loc, soundName, volume, pitch);
        }
    }

    public void play(Location loc, SoundCategory category, Player player) {
        player.playSound(loc, soundName, category, volume, pitch);
    }

    public void play(Location loc, Player player) {
        player.playSound(loc, soundName, volume, pitch);
    }

    public void play(Location loc, SoundCategory category) {
        if (loc.getWorld() == null) return;
        loc.getWorld().playSound(loc, soundName, category, volume, pitch);
    }

    public void play(Location loc) {
        if (loc.getWorld() == null) return;
        loc.getWorld().playSound(loc, soundName, volume, pitch);
    }

    public static Sound fromString(String str) {
        if (str == null) return null;
        String[] split = str.replace(")", "").replace(" ", "").split("[(,]");
        String name = split[0];
        float volume = Float.parseFloat(split[1]);
        float pitch = Float.parseFloat(split[2]);
        return new Sound(name, volume, pitch);
    }
}
