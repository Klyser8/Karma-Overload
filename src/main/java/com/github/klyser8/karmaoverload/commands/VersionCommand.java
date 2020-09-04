package com.github.klyser8.karmaoverload.commands;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.language.Message;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("karma")
public class VersionCommand extends CommandBase {

    private final KarmaOverload plugin;
    private final Sound sound;
    public VersionCommand(KarmaOverload plugin) {
        this.plugin = plugin;
        this.sound = new Sound("ui.button.click", 1.0f, 1.5f);
    }

    @SubCommand("version")
    @Permission("karma.command.version")
    public void versionCommand(CommandSender sender) {
        sender.sendMessage(plugin.getLanguageHandler().getMessage(Message.VERSION).replace("<VERSION>", plugin.getDescription().getVersion()));
        if (sender instanceof Player && plugin.getPreferences().isCommandSoundsEnabled()) sound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
    }

}
