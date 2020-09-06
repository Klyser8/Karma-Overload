package com.github.klyser8.karmaoverload.karma.actions;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.KarmaAction;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.AlignmentFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagingAction extends KarmaAction {

    private Map<String, MessageData> messageMap;

    public MessagingAction(Karma plugin, List<String> messageCodes) {
        super(plugin, 0, 0, null); //0, 0, null since these will be provided by the MessageData objects.
        messageMap = new HashMap<>();

        for (String msgCode : messageCodes) {
            String[] subString = msgCode.split("\\{");
            String msg = subString[0];
            Map<String, String> actionData = AlignmentFactory.parseLine(subString[1]);
            MessageData karmaMineral = new MessageData(Double.parseDouble(actionData.get("amount")),
                    Double.parseDouble(actionData.get("chance")), actionData.get("permission"), Sound.fromString(actionData.get("sound")));
            messageMap.put(msg, karmaMineral);
        }
        messageMap = Collections.unmodifiableMap(messageMap);
    }

    public Map<String, MessageData> getMessageMap() {
        return messageMap;
    }

    public static class MessageData {

        private final double amount;
        private final double chance;
        private final String permission;
        private final Sound sound;

        private MessageData(double amount, double chance, String permission, Sound sound) {
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
