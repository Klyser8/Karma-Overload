package com.github.klyser8.karmaoverload.storage;

import com.github.klyser8.karmaoverload.karma.KarmaProfile;

import java.util.List;

public class DeserializedKarmaProfile {

        private final List<KarmaProfile.HistoryEntry> history;
        private final double karma;

        public DeserializedKarmaProfile(List<KarmaProfile.HistoryEntry> history, double karma) {
            this.history = history;
            this.karma = karma;
        }

        public List<KarmaProfile.HistoryEntry> getHistory() {
            return history;
        }

        public double getKarma() {
            return karma;
        }
    }