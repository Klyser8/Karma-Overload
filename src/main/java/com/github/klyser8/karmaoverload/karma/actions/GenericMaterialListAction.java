package com.github.klyser8.karmaoverload.karma.actions;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaAction;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.AlignmentFactory;
import org.bukkit.Material;

import java.util.*;

public class GenericMaterialListAction extends KarmaAction {

    private Map<Material, KarmaMaterialData> materialsMap;

    public GenericMaterialListAction(KarmaOverload plugin, List<String> codes) {
        super(plugin, 0, 0, null); //0, 0, null since these will be provided by the KarmaMaterialData objects.
        materialsMap = new HashMap<>();
        for (String foodCode : codes) {
            String[] subString = foodCode.split("\\{");
            Material material = Material.valueOf(subString[0]);
            Map<String, String> effectData = AlignmentFactory.parseLine(subString[1]);
            KarmaMaterialData data = new KarmaMaterialData(Double.parseDouble(effectData.get("amount")), Double.parseDouble(effectData.get("chance")),
                    Sound.fromString(effectData.get("sound")), effectData.get("permission"));
            materialsMap.put(material, data);
        }
        materialsMap = Collections.unmodifiableMap(materialsMap);
    }

    public Map<Material, KarmaMaterialData> getMaterialsMap() {
        return materialsMap;
    }

    public static class KarmaMaterialData {

        private final double amount;
        private final double chance;
        private final Sound sound;
        private final String permission;

        private KarmaMaterialData(double amount, double chance, Sound sound, String permission) {
            this.amount = amount;
            this.chance = chance;
            this.sound = sound;
            this.permission = permission;
        }

        public double getAmount() {
            return amount;
        }

        public double getChance() {
            return chance;
        }

        public Sound getSound() {
            return sound;
        }

        public String getPermission() {
            return permission;
        }
    }
}
