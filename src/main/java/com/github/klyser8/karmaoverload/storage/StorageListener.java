package com.github.klyser8.karmaoverload.storage;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.karma.Alignment;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.actions.KarmaActionType;
import com.github.klyser8.karmaoverload.karma.actions.PassiveKarmaAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StorageListener implements Listener {

    private final KarmaOverload plugin;

    public StorageListener(KarmaOverload plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getProfileProvider().createProfile(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaActions().containsKey(KarmaActionType.PASSIVE)) return;
        PassiveKarmaAction action = (PassiveKarmaAction) alignment.getKarmaActions().get(KarmaActionType.PASSIVE);
        if (alignment.getKarmaActions().containsKey(KarmaActionType.PASSIVE)) action.stopRunnable();

        if (plugin.getPreferences().getStorageType() == Preferences.JSON_STORAGE) {
            plugin.getProfileWriter().saveProfileGson(plugin.getProfileProvider().getProfile(event.getPlayer()));
        } else if (plugin.getPreferences().getStorageType() == Preferences.MYSQL_STORAGE) {
            plugin.getProfileWriter().saveProfileDB(plugin.getProfileProvider().getProfile(event.getPlayer()));
        }
        plugin.getProfileProvider().removeProfile(player);
    }

}
