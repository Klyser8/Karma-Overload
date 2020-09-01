package com.github.klyser8.karmaoverload.storage;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaWriter;
import com.github.klyser8.karmaoverload.karma.Alignment;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.KarmaSource;
import com.github.klyser8.karmaoverload.karma.actions.KarmaActionType;
import com.github.klyser8.karmaoverload.karma.actions.PassiveKarmaAction;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.github.klyser8.karmaoverload.util.RandomUtil.debugMessage;

public class ProfileProvider {

    private final Map<Player, KarmaProfile> profiles;
    private final KarmaOverload plugin;

    public ProfileProvider(KarmaOverload plugin) {
        this.plugin = plugin;
        profiles = new HashMap<>();
        File folder = new File(plugin.getDataFolder() + File.separator + "players");
        if (folder.exists()) return;
        folder.mkdir();
    }

    public void createProfile(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
            File playerFile = new File(plugin.getDataFolder() + File.separator + "players", player.getUniqueId().toString() + ".json");
            KarmaProfile profile = new KarmaProfile(plugin, player);
            if (plugin.getPreferences().getStorageType() == 1) {
                if (playerFile.exists()) {
                    Gson gson = new Gson();
                    try {
                        DeserializedKarmaProfile deserializedProfile = gson.fromJson(new FileReader(playerFile), DeserializedKarmaProfile.class);
                        profile.setKarma(deserializedProfile.getKarma());
                        for (KarmaProfile.HistoryEntry entry : deserializedProfile.getHistory()) {
                            profile.addHistoryEntry(entry.getDate(), entry.getSource(), entry.getAmount());
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                String sqlQuery = "SELECT Score FROM KarmaTable WHERE UUID ='" + player.getUniqueId().toString() + "';";
                String historySQLQuery = "SELECT * FROM KarmaHistory WHERE UUID = '" + player.getUniqueId().toString() + "';";
                try {
                    PreparedStatement statement = plugin.getConnection().prepareStatement(sqlQuery);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        profile.setKarma(resultSet.getDouble("Score"));
                    }

                    statement = plugin.getConnection().prepareStatement(historySQLQuery);
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        profile.addHistoryEntry(resultSet.getTimestamp("EventTime"),
                                KarmaSource.valueOf(resultSet.getString("Source")), resultSet.getDouble("Change"));
                    }
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            }
            KarmaWriter.updateAlignment(plugin, profile, false);
            profiles.put(player, profile);

            Alignment alignment = profile.getAlignment();
            if (alignment.getKarmaActions().containsKey(KarmaActionType.PASSIVE)) {
                PassiveKarmaAction action = (PassiveKarmaAction) alignment.getKarmaActions().get(KarmaActionType.PASSIVE);
                if (player.hasPermission(action.getPermission())) action.startRunnable(player);
            }
            debugMessage(plugin, player.getName() + "'s data loaded successfully", DebugLevel.LOW);
            if (profile.getAlignment().getKarmaLimit() == 0) return;
            if (plugin.getKarmaLimitMap().containsKey(player)) return;
            plugin.getKarmaLimitMap().put(player, 0.0);
        });
    }

    public KarmaProfile getProfile(Player player) {
        return profiles.get(player);
    }

    public void removeProfile(Player player) {
        profiles.remove(player);
    }

}
