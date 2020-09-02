package com.github.klyser8.karmaoverload.karma.worldguard;

import com.github.klyser8.karmaoverload.api.events.KarmaPreActionEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaPreEffectEvent;
import com.github.klyser8.karmaoverload.karma.actions.KarmaActionType;
import com.github.klyser8.karmaoverload.karma.effects.KarmaEffectType;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

import static com.github.klyser8.karmaoverload.karma.worldguard.KarmaFlags.*;

public class FlagListener implements Listener {

    private final Map<KarmaActionType, StateFlag> actionTypeStateFlagMap;
    private final Map<KarmaEffectType, StateFlag> effectTypeStateFlagMap;
    public FlagListener() {
        actionTypeStateFlagMap = new HashMap<>();
        effectTypeStateFlagMap = new HashMap<>();
        actionTypeStateFlagMap.put(KarmaActionType.PASSIVE, ACTION_PASSIVE);
        actionTypeStateFlagMap.put(KarmaActionType.EATING, ACTION_EATING);
        actionTypeStateFlagMap.put(KarmaActionType.TAMING, ACTION_TAMING);
        actionTypeStateFlagMap.put(KarmaActionType.FEEDING, ACTION_FEEDING);
        actionTypeStateFlagMap.put(KarmaActionType.TRADING, ACTION_TRADING);
        actionTypeStateFlagMap.put(KarmaActionType.BARTERING, ACTION_BARTERING);
        actionTypeStateFlagMap.put(KarmaActionType.MESSAGING, ACTION_MESSAGING);
        actionTypeStateFlagMap.put(KarmaActionType.ADVANCEMENT, ACTION_ADVANCEMENT);
        actionTypeStateFlagMap.put(KarmaActionType.BLOCK_PLACING, ACTION_BLOCK_PLACING);
        actionTypeStateFlagMap.put(KarmaActionType.BLOCK_BREAKING, ACTION_BLOCK_BREAKING);
        actionTypeStateFlagMap.put(KarmaActionType.SERVER_VOTING, ACTION_SERVER_VOTING);
        actionTypeStateFlagMap.put(KarmaActionType.PLAYER_HITTING, ACTION_PLAYER_HITTING);
        actionTypeStateFlagMap.put(KarmaActionType.PLAYER_KILLING, ACTION_PLAYER_KILLING);
        actionTypeStateFlagMap.put(KarmaActionType.HOSTILE_MOB_KILLING, ACTION_HOSTILE_MOB_KILLING);
        actionTypeStateFlagMap.put(KarmaActionType.PASSIVE_MOB_KILLING, ACTION_PASSIVE_MOB_KILLING);
        actionTypeStateFlagMap.put(KarmaActionType.FRIENDLY_MOB_HITTING, ACTION_FRIENDLY_MOB_HITTING);
        actionTypeStateFlagMap.put(KarmaActionType.FRIENDLY_MOB_KILLING, ACTION_FRIENDLY_MOB_KILLING);

        effectTypeStateFlagMap.put(KarmaEffectType.EXP_MULTIPLIER, EFFECT_EXP_MULTIPLIER);
        effectTypeStateFlagMap.put(KarmaEffectType.KARMA_MULTIPLIER, EFFECT_KARMA_MULTIPLIER);
        effectTypeStateFlagMap.put(KarmaEffectType.LOOT_GENERATION, EFFECT_LOOT_GENERATION);
        effectTypeStateFlagMap.put(KarmaEffectType.LIGHTNING, EFFECT_LIGHTNING);
        effectTypeStateFlagMap.put(KarmaEffectType.MOB_ANGER, EFFECT_MOB_ANGER);
        effectTypeStateFlagMap.put(KarmaEffectType.MINERAL_REGEN, EFFECT_MINERAL_REGEN);
        effectTypeStateFlagMap.put(KarmaEffectType.MINERAL_FAIL, EFFECT_MINERAL_FAIL);
    }

    @EventHandler
    public void onKarmaPreAction(KarmaPreActionEvent event) {
        Player player = event.getProfile().getPlayer();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet regionSet = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (!regionSet.testState(localPlayer, actionTypeStateFlagMap.get(event.getType()))) event.setCancelled(true);
    }

    @EventHandler
    public void onKarmaPreEffect(KarmaPreEffectEvent event) {
        Player player = event.getProfile().getPlayer();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet regionSet = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (!regionSet.testState(localPlayer, effectTypeStateFlagMap.get(event.getType()))) event.setCancelled(true);
    }

}
