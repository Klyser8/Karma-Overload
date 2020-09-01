package com.github.klyser8.karmaoverload.commands;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaWriter;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.KarmaSource;
import com.github.klyser8.karmaoverload.language.LanguageHandler;
import com.github.klyser8.karmaoverload.language.Message;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.mattstudios.mf.base.components.MfUtil.color;

@Command("karma")
public class ResetKarmaCommand extends CommandBase {

    private final KarmaOverload plugin;
    private final Sound errorSound;
    private final Sound sound;
    public ResetKarmaCommand(KarmaOverload plugin) {
        this.plugin = plugin;
        this.errorSound = new Sound("block.note_block.bass", 1.0f, 1.0f);
        this.sound = new Sound("ui.button.click", 1.0f, 1.5f);
    }

    @SubCommand("reset")
    @Completion({"#players", "#empty"})
    @Permission("karma.command.reset")
    public void resetCommand(CommandSender sender, String player, @Optional String reason) {
        LanguageHandler lang = plugin.getLanguageHandler();
        Player target = Bukkit.getPlayerExact(player);
        if (target == null) {
            sender.sendMessage(lang.getMessage(Message.ERROR_UNKNOWN_PLAYER));
            if (sender instanceof Player) errorSound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
            return;
        }
        KarmaProfile profile = plugin.getProfileProvider().getProfile(target);
        sender.sendMessage(lang.getMessage(Message.SCORE_RESET_SUCCESSFULLY).replace("<PLAYER>", player));
        KarmaWriter.changeKarma(plugin, profile, plugin.getPreferences().getStartingScore() - profile.getKarma(), KarmaSource.COMMAND);
        if (sender instanceof Player) sound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
        if (reason == null) return;
        reason = reason.replaceAll("_", " ");
        target.sendMessage(color(lang.getMessage(Message.SCORE_RESET_REASON).replace("<REASON>", reason)));

    }

}
