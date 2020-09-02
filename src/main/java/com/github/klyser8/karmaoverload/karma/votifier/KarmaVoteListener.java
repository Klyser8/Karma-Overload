package com.github.klyser8.karmaoverload.karma.votifier;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaWriter;
import com.github.klyser8.karmaoverload.karma.Alignment;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.KarmaSource;
import com.github.klyser8.karmaoverload.karma.actions.GenericAction;
import com.github.klyser8.karmaoverload.karma.actions.KarmaActionType;
import com.github.klyser8.karmaoverload.util.MathUtil;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.klyser8.karmaoverload.util.MathUtil.calculateChance;

public class KarmaVoteListener implements Listener {

    private final KarmaOverload plugin;
    public KarmaVoteListener(KarmaOverload plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerVote(VotifierEvent event) {
        Player player = Bukkit.getPlayer(event.getVote().getUsername());
        if (player == null) return;
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaActions().containsKey(KarmaActionType.SERVER_VOTING)) return;
        GenericAction action = (GenericAction) alignment.getKarmaActions().get(KarmaActionType.SERVER_VOTING);
        if (!calculateChance(action.getChance())) return;
        if (player.isOnline()) {
            KarmaWriter.changeKarma(plugin, profile, action.getAmount(), KarmaSource.VOTING);
            if (action.getSound() != null) action.getSound().play(player.getLocation(), player);
        }
    }
}
