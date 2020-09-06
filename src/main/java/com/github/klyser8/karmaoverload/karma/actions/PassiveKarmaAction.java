package com.github.klyser8.karmaoverload.karma.actions;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.KarmaAction;
import com.github.klyser8.karmaoverload.api.KarmaWriter;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.KarmaSource;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PassiveKarmaAction extends KarmaAction {

    private final int interval;

    private final Sound sound;

    private PassiveRunnable runnable;

    public PassiveKarmaAction(Karma plugin, double amount, double chance, int interval, String permission, Sound sound) {
        super(plugin, amount, chance, permission);
        this.interval = interval;
        this.sound = sound;
    }

    public void startRunnable(Player player) {
        runnable = new PassiveRunnable(player);
        runnable.runTaskTimer(plugin, 20, 20);
    }

    public void stopRunnable() {
        if (runnable == null) return;
        runnable.cancel();
    }

    public double getAmount() {
        return amount;
    }

    public int getInterval() {
        return interval;
    }

    public Sound getSound() {
        return sound;
    }

    private class PassiveRunnable extends BukkitRunnable {

        private final Player player;

        private PassiveRunnable(Player player) {
            this.player = player;
        }

        private int count = 0;

        @Override
        public void run() {
            count++;

            if (count >= interval && player.hasPermission(permission)) {
                KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
                KarmaWriter.changeKarma(plugin, profile, amount, KarmaSource.PASSIVE);
                if (sound != null) sound.play(player.getEyeLocation(), SoundCategory.BLOCKS, player);
                count = 0;
            }
        }

    }
}
