package com.github.klyser8.karmaoverload.karma.actions;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaAction;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.AlignmentFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancementAction extends KarmaAction {

    private Map<String, AdvancementData> advancementMap;

    public AdvancementAction(KarmaOverload plugin, List<String> messageCodes) {
        super(plugin, 0, 0, null); //0, 0, null since these will be provided by the MessageData objects.
        advancementMap = new HashMap<>();

        for (String msgCode : messageCodes) {
            String[] subString = msgCode.split("\\{");
            String msg = subString[0];
            Map<String, String> actionData = AlignmentFactory.parseLine(subString[1]);
            AdvancementData karmaMineral = new AdvancementData(Double.parseDouble(actionData.get("amount")),
                    Double.parseDouble(actionData.get("chance")), actionData.get("permission"), Sound.fromString(actionData.get("sound")));
            advancementMap.put(msg, karmaMineral);
        }
        advancementMap = Collections.unmodifiableMap(advancementMap);
    }

    public Map<String, AdvancementData> getAdvancementMap() {
        return advancementMap;
    }

    public static class AdvancementData {

        private final double amount;
        private final double chance;
        private final String permission;
        private final Sound sound;

        private AdvancementData(double amount, double chance, String permission, Sound sound) {
            this.amount = amount;
            this.chance = chance;
            this.permission = permission;
            this.sound = sound;
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
