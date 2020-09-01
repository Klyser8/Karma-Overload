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

import static com.github.klyser8.karmaoverload.util.MathUtil.isDouble;
import static me.mattstudios.mf.base.components.MfUtil.color;

@Command("karma")
public class RemoveKarmaCommand extends CommandBase {

    private final KarmaOverload plugin;
    private final Sound errorSound;
    private final Sound sound;
    public RemoveKarmaCommand(KarmaOverload plugin) {
        this.plugin = plugin;
        this.errorSound = new Sound("block.note_block.bass", 1.0f, 1.0f);
        this.sound = new Sound("ui.button.click", 1.0f, 1.5f);
    }

    @SubCommand("remove")
    @Permission("karma.command.remove")
    @Completion({"#empty", "#players", "#empty"})
    public void removeCommand(CommandSender sender, Double amount, String targetName, @Optional String reason) {
        if (sender == null) return;
        LanguageHandler lang = plugin.getLanguageHandler();
        Player target;
        target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            sender.sendMessage(lang.getMessage(Message.ERROR_UNKNOWN_PLAYER));
            if (sender instanceof Player) errorSound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
            return;
        }
        KarmaProfile profile = plugin.getProfileProvider().getProfile(target);

        if (!isDouble(String.valueOf(amount)) || amount <= 0) {
            sender.sendMessage(lang.getMessage(Message.ERROR_INVALID_NUMBER));
            if (sender instanceof Player) errorSound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
            return;
        }
        if (sender.getName().equalsIgnoreCase(targetName)) {
            sender.sendMessage(lang.getMessage(Message.SCORE_REMOVED_SUCCESSFULLY_SELF).replace("<NUMBER>", String.valueOf(amount)));
        } else {
            sender.sendMessage(lang.getMessage(Message.SCORE_REMOVED_SUCCESSFULLY).replace("<PLAYER>", targetName)
                    .replace("<NUMBER>", String.valueOf(amount)));
        }
        KarmaWriter.changeKarma(plugin, profile, -amount, KarmaSource.COMMAND);
        if (sender instanceof Player) sound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
        if (reason == null) return;
        reason = reason.replaceAll("_", " ");
        target.sendMessage(color(lang.getMessage(Message.SCORE_REMOVED_REASON).replace("<AMOUNT>", amount.toString()).replace("<REASON>", reason)));
    }

}
