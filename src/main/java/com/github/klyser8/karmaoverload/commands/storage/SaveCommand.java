package com.github.klyser8.karmaoverload.commands.storage;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.language.Message;
import com.github.klyser8.karmaoverload.storage.Preferences;
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
public class SaveCommand extends CommandBase {

    private final Karma plugin;
    private final Sound sound;
    public SaveCommand(Karma plugin) {
        this.plugin = plugin;
        this.sound = new Sound("ui.button.click", 1.0f, 1.5f);
    }

    @SubCommand("save")
    @Completion("#empty")
    @Permission("karma.command.save")
    public void saveCommand(CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
            if (plugin.getPreferences().getStorageType() == Preferences.JSON_STORAGE) plugin.getProfileWriter().saveProfileGson(profile);
            if (plugin.getPreferences().getStorageType() == Preferences.MYSQL_STORAGE) plugin.getProfileWriter().saveProfileDB(profile);
        }
        sender.sendMessage(plugin.getLanguageHandler().getMessage(Message.SAVE));
        if (!(sender instanceof Player) || !plugin.getPreferences().isCommandSoundsEnabled()) return;
        sound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
    }

}
