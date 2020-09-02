package com.github.klyser8.karmaoverload.karma.effects;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.AlignmentFactory;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import org.bukkit.Material;

import java.util.*;

public class MiningEffect extends KarmaEffect {

    private Map<Material, KarmaMineral> mineralMap;

    public MiningEffect(final KarmaOverload plugin, List<String> materialCodes, boolean doubleSound) {
        super(plugin, 0, null); //0, 0, null since these will be provided by the KarmaMineral objects.
        this.plugin = plugin;
        mineralMap = new HashMap<>();

        for (String matCode : materialCodes) {
            String[] subString = matCode.split("\\{");
            Material material = Material.valueOf(subString[0]);
            Map<String, String> effectData;
            KarmaMineral karmaMineral;
            if (doubleSound) {
                effectData = AlignmentFactory.parseLine(subString[1]);
                karmaMineral = new KarmaMineral(Double.parseDouble(effectData.get("chance")),
                        Sound.fromString(effectData.get("breakSound")), Sound.fromString(effectData.get("appearSound")), effectData.get("permission"));
            } else {
                effectData = AlignmentFactory.parseLine(subString[1]);
                karmaMineral = new KarmaMineral(Double.parseDouble(effectData.get("chance")),
                        Sound.fromString(effectData.get("sound")), null, effectData.get("permission"));
            }
            mineralMap.put(material, karmaMineral);
        }
        mineralMap = Collections.unmodifiableMap(mineralMap);
    }

    public Map<Material, KarmaMineral> getMineralMap() {
        return mineralMap;
    }

    public static class KarmaMineral {

        private final double chance;
        private final Sound breakSound;
        private final Sound appearSound;
        private final String permission;

        public KarmaMineral(double chance, Sound breakSound, Sound appearSound, String permission) {
            this.chance = chance;
            this.breakSound = breakSound;
            this.appearSound = appearSound;
            this.permission = permission;
        }

        public double getChance() {
            return chance;
        }

        public String getPermission() {
            return permission;
        }

        public Sound getBreakSound() {
            return breakSound;
        }

        public Sound getAppearSound() {
            return appearSound;
        }
    }

}
