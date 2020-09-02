package com.github.klyser8.karmaoverload.karma;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.events.AlignmentChangeEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaGainEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaLossEvent;
import com.github.klyser8.karmaoverload.storage.DebugLevel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static com.github.klyser8.karmaoverload.util.RandomUtil.debugMessage;

public class LoggingListener implements Listener {

    private final KarmaOverload plugin;
    public LoggingListener(KarmaOverload plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKarmaGain(KarmaGainEvent event) {
        KarmaProfile profile = event.getProfile();
        debugMessage(plugin, profile.getPlayer().getName() + " gained " + event.getGainedKarma() + " karma from " + event.getSource(), DebugLevel.HIGH);
        debugMessage(plugin, profile.getPlayer().getName() + "'s recent karma: " + plugin.getKarmaLimitMap().get(profile.getPlayer()) +
                "/" + profile.getAlignment().getKarmaLimit(), DebugLevel.HIGH);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKarmaLoss(KarmaLossEvent event) {
        KarmaProfile profile = event.getProfile();
        debugMessage(plugin, profile.getPlayer().getName() + " lost " + event.getLostKarma() + " karma from " + event.getSource(), DebugLevel.HIGH);
        debugMessage(plugin, profile.getPlayer().getName() + "'s recent karma: " + plugin.getKarmaLimitMap().get(profile.getPlayer()) +
                "/" + profile.getAlignment().getKarmaLimit(), DebugLevel.HIGH);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAlignmentChange(AlignmentChangeEvent event) {
        Player player = event.getProfile().getPlayer();
        debugMessage(plugin, player.getName() + "'s alignment went from  " + event.getOldAlignment().getName() + " to "
                + event.getNewAlignment().getName(), DebugLevel.HIGH);
    }

}
