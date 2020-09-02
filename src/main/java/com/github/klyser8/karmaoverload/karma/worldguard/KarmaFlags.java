package com.github.klyser8.karmaoverload.karma.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;

import java.util.ArrayList;
import java.util.List;

public class KarmaFlags {

    public static final List<StateFlag> FLAG_LIST = new ArrayList<>();

    public static final StateFlag ACTION_PASSIVE = register(new StateFlag("karma-action-passive", true));
    public static final StateFlag ACTION_FRIENDLY_MOB_KILLING = register(new StateFlag("karma-action-friendly-mob-killing", true));
    public static final StateFlag ACTION_PASSIVE_MOB_KILLING = register(new StateFlag("karma-action-passive-mob-killing", true));
    public static final StateFlag ACTION_HOSTILE_MOB_KILLING = register(new StateFlag("karma-action-hostile-mob-killing", true));
    public static final StateFlag ACTION_PLAYER_HITTING = register(new StateFlag("karma-action-player-hitting", true));
    public static final StateFlag ACTION_PLAYER_KILLING = register(new StateFlag("karma-action-player-killing", true));
    public static final StateFlag ACTION_FRIENDLY_MOB_HITTING = register(new StateFlag("karma-action-friendly-mob-hitting", true));
    public static final StateFlag ACTION_TRADING = register(new StateFlag("karma-action-trading", true));
    public static final StateFlag ACTION_BARTERING = register(new StateFlag("karma-action-bartering", true));
    public static final StateFlag ACTION_TAMING = register(new StateFlag("karma-action-taming", true));
    public static final StateFlag ACTION_FEEDING = register(new StateFlag("karma-action-feeding", true));
    public static final StateFlag ACTION_SERVER_VOTING = register(new StateFlag("karma-action-server-voting", true));
    public static final StateFlag ACTION_EATING = register(new StateFlag("karma-action-eating", true));
    public static final StateFlag ACTION_BLOCK_PLACING = register(new StateFlag("karma-action-block-placing", true));
    public static final StateFlag ACTION_BLOCK_BREAKING = register(new StateFlag("karma-action-block-breaking", true));
    public static final StateFlag ACTION_MESSAGING = register(new StateFlag("karma-action-messaging", true));
    public static final StateFlag ACTION_ADVANCEMENT = register(new StateFlag("karma-action-advancement", true));

    public static final StateFlag EFFECT_EXP_MULTIPLIER = register(new StateFlag("karma-effect-exp-multiplier", true));
    public static final StateFlag EFFECT_KARMA_MULTIPLIER = register(new StateFlag("karma-effect-karma-multiplier", true));
    public static final StateFlag EFFECT_LOOT_GENERATION = register(new StateFlag("karma-effect-loot-generation", true));
    public static final StateFlag EFFECT_LIGHTNING = register(new StateFlag("karma-effect-lightning", true));
    public static final StateFlag EFFECT_MOB_ANGER = register(new StateFlag("karma-effect-mob-anger", true));
    public static final StateFlag EFFECT_MINERAL_REGEN = register(new StateFlag("karma-effect-mineral-regen", true));
    public static final StateFlag EFFECT_MINERAL_FAIL = register(new StateFlag("karma-effect-mineral-fail", true));

    /*public void setupWorldGuardFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("karma-gain", true);
            registry.register(flag);
            Flags
            KARMA_GAIN = flag;
        } catch (FlagConflictException exception) {

            Flag<?> existing = registry.get("karma-gain");
            if (existing instanceof StateFlag) {
                KARMA_GAIN = (StateFlag) existing;
            } else {
                throw exception;
            }
        }
    }*/

    private static StateFlag register(final StateFlag flag) throws FlagConflictException {
        WorldGuard.getInstance().getFlagRegistry().register(flag);
        FLAG_LIST.add(flag);
        return flag;
    }

    /**
     * Dummy method to call that initialises the class.
     */
    public static void registerAll() {
    }

}
