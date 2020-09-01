package com.github.klyser8.karmaoverload.commands;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.language.LanguageHandler;
import com.github.klyser8.karmaoverload.language.Message;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.mattstudios.mf.base.components.MfUtil.color;

@Command("karma")
public class ViewKarmaCommand extends CommandBase {

    private final KarmaOverload plugin;
    private final Sound errorSound;
    private final Sound sound;
    public ViewKarmaCommand(KarmaOverload plugin) {
        this.plugin = plugin;
        this.errorSound = new Sound("block.note_block.bass", 1.0f, 1.0f);
        this.sound = new Sound("ui.button.click", 1.0f, 1.5f);
    }

    @Default
    public void viewCommand(CommandSender sender, @Optional String targetName) {
        if (sender == null) return;
        LanguageHandler lang = plugin.getLanguageHandler();
        if (targetName == null) {
            if (!sender.hasPermission("karma.command.view.self")) return;
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(lang.getMessage(Message.ERROR_UNKNOWN_PLAYER));
                if (sender instanceof Player) errorSound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
                return;
            }
            KarmaProfile profile = plugin.getProfileProvider().getProfile((Player) sender);
            sender.sendMessage(lang.getMessage(Message.VIEW_SCORE_SELF).replace("<NUMBER>", profile.getKarma() + ""));
            sender.sendMessage(lang.getMessage(Message.VIEW_ALIGNMENT_SELF)
                    .replace("<ALIGNMENT>", profile.getAlignment().getColor() + profile.getAlignment().getName()));
        } else {
            if (!sender.hasPermission("karma.command.view.others")) return;
            Player target = Bukkit.getPlayerExact(targetName);
            KarmaProfile profile = plugin.getProfileProvider().getProfile(target);
            if (target == null) {
                sender.sendMessage(lang.getMessage(Message.ERROR_UNKNOWN_PLAYER));
                if (sender instanceof Player) errorSound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
                return;
            }
            sender.sendMessage(lang.getMessage(Message.VIEW_SCORE)
                    .replace("<PLAYER>", targetName)
                    .replace("<NUMBER>", profile.getKarma() + ""));
            sender.sendMessage(lang.getMessage(Message.VIEW_ALIGNMENT)
                    .replace("<PLAYER>", targetName)
                    .replace("<ALIGNMENT>", profile.getAlignment().getColor() + profile.getAlignment().getName()));
        }
        if (sender instanceof Player) sound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
    }

}
