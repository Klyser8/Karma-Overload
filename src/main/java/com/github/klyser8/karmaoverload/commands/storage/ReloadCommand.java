package com.github.klyser8.karmaoverload.commands.storage;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.KarmaWriter;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.language.Message;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("karma")
public class ReloadCommand extends CommandBase {

    private final Karma plugin;
    private final Sound sound;
    public ReloadCommand(Karma plugin) {
        this.plugin = plugin;
        this.sound = new Sound("ui.button.click", 1.0f, 1.5f);
    }

    @SubCommand("reload")
    @Completion("#empty")
    @Permission("karma.command.reload")
    public void reloadCommand(CommandSender sender) {
        plugin.getAlignmentFactory().setup();
        plugin.getPreferences().loadPreferences();
        sender.sendMessage(plugin.getLanguageHandler().getMessage(Message.RELOAD));
        for (Player player : Bukkit.getOnlinePlayers()) {
            KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
            profile.setAlignment(null); //Replacing old alignment object with a new, updated one for each player.
            KarmaWriter.updateAlignment(plugin, profile, false);
        }
        if (!(sender instanceof Player) || !plugin.getPreferences().isCommandSoundsEnabled()) return;
        sound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
    }

}
