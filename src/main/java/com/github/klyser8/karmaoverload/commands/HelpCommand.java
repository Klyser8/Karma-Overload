package com.github.klyser8.karmaoverload.commands;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.language.LanguageHandler;
import com.github.klyser8.karmaoverload.language.Message;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.mattstudios.mf.base.components.MfUtil.color;

@Command("karma")
public class HelpCommand extends CommandBase {

    private final Karma plugin;
    private final Sound sound;
    public HelpCommand(Karma plugin) {
        this.plugin = plugin;
        this.sound = new Sound("ui.button.click", 1.0f, 1.5f);
    }

    @SubCommand("help")
    @Completion("#empty")
    @Permission("karma.command.help")
    public void helpCommand(CommandSender sender) {
        LanguageHandler lang = plugin.getLanguageHandler();
        sender.sendMessage(color("&5------------------ &rHELP &5------------------"));
        String viewCommand = "";
        if (sender.hasPermission("karma.command.view.others"))
            viewCommand = lang.getMessage(Message.HELP_VIEW)
                    .replace("<VIEW_COMMAND>", color("&d/Karma &5&o[" + lang.getMessage(Message.PLAYER).toLowerCase() + "]"));
        else if (sender.hasPermission("karma.command.view.self"))
            viewCommand = lang.getMessage(Message.HELP_VIEW)
                    .replace("<VIEW_COMMAND>", "/Karma");
        sender.sendMessage(viewCommand);
        if (sender.hasPermission("karma.command.help"))
            sender.sendMessage(lang.getMessage(Message.HELP_HELP)
                    .replace("<HELP_COMMAND>", color("&d/Karma help")));
        if (sender.hasPermission("karma.command.reload"))
            sender.sendMessage(lang.getMessage(Message.HELP_RELOAD)
                    .replace("<RELOAD_COMMAND>", color("&d/Karma reload")));
        if (sender.hasPermission("karma.command.save"))
            sender.sendMessage(lang.getMessage(Message.HELP_SAVE)
                    .replace("<SAVE_COMMAND>", color("&d/Karma save")));
        if (sender.hasPermission("karma.command.add"))
            sender.sendMessage(lang.getMessage(Message.HELP_ADD)
                    .replace("<ADD_COMMAND>", color("&d/Karma add &5<" + lang.getMessage(Message.NUMBER).toLowerCase() +
                            "> <" + lang.getMessage(Message.PLAYER).toLowerCase() + "> &o[" + lang.getMessage(Message.REASON) + "]")));
        if (sender.hasPermission("karma.command.remove"))
            sender.sendMessage(lang.getMessage(Message.HELP_REMOVE)
                    .replace("<REMOVE_COMMAND>", color("&d/Karma remove &5<" + lang.getMessage(Message.NUMBER).toLowerCase() +
                            "> <" + lang.getMessage(Message.PLAYER).toLowerCase() + "> &o[" + lang.getMessage(Message.REASON) + "]")));
        if (sender.hasPermission("karma.command.set"))
            sender.sendMessage(lang.getMessage(Message.HELP_SET)
                    .replace("<SET_COMMAND>", color("&d/Karma set &5<" + lang.getMessage(Message.NUMBER).toLowerCase() +
                            "> <" + lang.getMessage(Message.PLAYER).toLowerCase() + "> &o[" + lang.getMessage(Message.REASON) + "]")));
        if (sender.hasPermission("karma.command.reset"))
            sender.sendMessage(lang.getMessage(Message.HELP_RESET)
                    .replace("<RESET_COMMAND>", color("&d/Karma reset &5<" +
                            lang.getMessage(Message.PLAYER).toLowerCase() + ">")));
        if (sender.hasPermission("karma.command.clear"))
            sender.sendMessage(lang.getMessage(Message.HELP_CLEAR)
                    .replace("<CLEAR_COMMAND>", color("&d/Karma clear")));
        if (sender.hasPermission("karma.command.list"))
            sender.sendMessage(lang.getMessage(Message.HELP_LIST)
                    .replace("<LIST_COMMAND>", color("&d/Karma list")));
        if (sender.hasPermission("karma.command.list"))
            sender.sendMessage(lang.getMessage(Message.HELP_HISTORY)
                    .replace("<HISTORY_COMMAND>", color("&d/Karma history &5&o[" + lang.getMessage(Message.PLAYER).toLowerCase() + "]")));
        if (sender instanceof Player && plugin.getPreferences().isCommandSoundsEnabled()) sound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
    }

}
