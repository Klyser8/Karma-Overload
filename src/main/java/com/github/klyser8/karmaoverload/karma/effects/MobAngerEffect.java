package com.github.klyser8.karmaoverload.karma.effects;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.api.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;

import static com.github.klyser8.karmaoverload.util.RandomUtil.isVersion;

public class MobAngerEffect extends KarmaEffect {

    private final Sound sound;
    private final int radius;

    public MobAngerEffect(Karma plugin, double chance, int radius, String permission, Sound angerSound) {
        super(plugin, chance, permission);
        this.radius = radius;
        this.sound = angerSound;
    }

    public void angerMob(LivingEntity entity, Player target) {
        boolean soundNull = sound == null;
        if (entity instanceof PigZombie) {
            PigZombie pigZombie = (PigZombie) entity;
            if (!pigZombie.isAngry() && !soundNull) sound.play(pigZombie.getEyeLocation(), SoundCategory.HOSTILE);
            pigZombie.setTarget(target);
            pigZombie.setAnger(pigZombie.getAnger() + 1);
        } else if ((isVersion("1.15") || isVersion("1.16")) && entity instanceof Bee) {
            Bee bee = (Bee) entity;
            if (bee.getAnger() == 0 && !soundNull) sound.play(bee.getEyeLocation(), SoundCategory.HOSTILE);
            bee.setAnger(bee.getAnger() + 1);
            bee.setTarget(target);
        } else if (entity instanceof Wolf) {
            Wolf wolf = (Wolf) entity;
            if (!wolf.isAngry() && !soundNull) sound.play(wolf.getEyeLocation(), SoundCategory.HOSTILE);
            wolf.setAngry(true);
            wolf.setTarget(target);
        } else if (entity instanceof IronGolem) {
            IronGolem golem = (IronGolem) entity;
            if (!(golem.getTarget() instanceof Player) && !soundNull) sound.play(golem.getEyeLocation(), SoundCategory.HOSTILE);
            golem.setTarget(target);
        } else if (entity instanceof Snowman) {
            Snowman snowman = (Snowman) entity;
            snowman.setTarget(target);
        } else if (entity instanceof Enderman) {
            Enderman enderman = (Enderman) entity;
            if (!(enderman.getTarget() instanceof Player) && !soundNull) sound.play(enderman.getEyeLocation(), SoundCategory.HOSTILE);
            enderman.setTarget(target);
        }
    }

    public Sound getSound() {
        return sound;
    }

    public int getRadius() {
        return radius;
    }
}
