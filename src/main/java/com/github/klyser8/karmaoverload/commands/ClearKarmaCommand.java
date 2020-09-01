package com.github.klyser8.karmaoverload.commands;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaWriter;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.karma.KarmaSource;
import com.github.klyser8.karmaoverload.language.Message;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("karma")
public class ClearKarmaCommand extends CommandBase {

    private final KarmaOverload plugin;
    private final Sound sound;
    public ClearKarmaCommand(KarmaOverload plugin) {
        this.plugin = plugin;
        this.sound = new Sound("ui.button.click", 1.0f, 1.5f);
    }

    @SubCommand("clear")
    @Permission("karma.command.clear")
    public void clearCommand(CommandSender sender) {
        if (!(sender instanceof Player)) return;
        KarmaProfile profile = plugin.getProfileProvider().getProfile((Player) sender);
        sender.sendMessage(plugin.getLanguageHandler().getMessage(Message.SCORE_CLEARED_SUCCESSFULLY));
        KarmaWriter.setKarma(plugin, profile, plugin.getPreferences().getStartingScore(), KarmaSource.COMMAND);
        sound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
    }

}
