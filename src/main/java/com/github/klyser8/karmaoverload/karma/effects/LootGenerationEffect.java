package com.github.klyser8.karmaoverload.karma.effects;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.api.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootGenerationEffect extends KarmaEffect {

    private final double lootMultiplier;
    private final Sound sound;

    public LootGenerationEffect(KarmaOverload plugin, double chance, double lootMultiplier, String permission, Sound lootSound) {
        super(plugin, chance, permission);
        this.lootMultiplier = lootMultiplier;
        this.sound = lootSound;
    }

    public Sound getSound() {
        return sound;
    }

    public double getLootMultiplier() {
        return lootMultiplier;
    }

    public List<ItemStack> increaseLoot(List<ItemStack> loot) {
        List<ItemStack> updatedLoot = new ArrayList<>();
        for (ItemStack item : loot) {
            if (item.getAmount() * lootMultiplier > item.getMaxStackSize()) continue;
            item.setAmount((int) (item.getAmount() * lootMultiplier));
            updatedLoot.add(item);
        }
        return updatedLoot;
    }
}
