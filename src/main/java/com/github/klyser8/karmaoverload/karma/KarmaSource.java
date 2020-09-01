package com.github.klyser8.karmaoverload.karma;

public enum KarmaSource {

    //When karma is changed via a command.
    COMMAND,
    //When karma is changed by hitting/killing a mob.
    MOB,
    //When karma is changed by hitting/killing a player
    PLAYER,
    //When karma is changed by doing 'nothing'.
    PASSIVE,
    //When karma is changed by completing an advancement.
    ADVANCEMENT,
    //When karma is changed by placing or destroying a block.
    BLOCK,
    //When karma is changed by trading with a villager.
    TRADE,
    //When karma is changed by bartering with piglins.
    BARTER,
    //When karma is changed by taming an animal.
    TAME,
    //When karma is changed by feeding an animal.
    FEED,
    //When karma is changed by eating.
    FOOD,
    //When karma is changed by voting the server.
    VOTING,
    //When karma is changed by sending chat messages.
    CHAT,
    //When karma is changed by other, unlisted means.
    MISC

}
