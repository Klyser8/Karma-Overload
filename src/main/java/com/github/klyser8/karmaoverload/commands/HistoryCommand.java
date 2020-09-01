package com.github.klyser8.karmaoverload.commands;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.language.LanguageHandler;
import com.github.klyser8.karmaoverload.language.Message;
import com.github.klyser8.karmaoverload.util.TableGenerator;
import me.mattstudios.mf.annotations.*;
import me.mattstudios.mf.annotations.Optional;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

import static me.mattstudios.mf.base.components.MfUtil.color;

@Command("karma")
public class HistoryCommand extends CommandBase {

    private final KarmaOverload plugin;
    private final Sound errorSound;
    private final Sound sound;
    public HistoryCommand(KarmaOverload plugin) {
        this.plugin = plugin;
        this.errorSound = new Sound("block.note_block.bass", 1.0f, 1.0f);
        this.sound = new Sound("ui.button.click", 1.0f, 1.5f);
    }

    @SubCommand("history")
    @Permission("karma.command.history")
    public void historyCommand(CommandSender sender, @Optional String targetName) {
        LanguageHandler lang = plugin.getLanguageHandler();
        Player target;
        if (targetName == null) {
            target = (Player) sender;
            targetName = sender.getName();
        } else {
            target = Bukkit.getPlayerExact(targetName);
        }
        if (target == null) {
            sender.sendMessage(lang.getMessage(Message.ERROR_UNKNOWN_PLAYER));
            if (sender instanceof Player) errorSound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
            return;
        }
        KarmaProfile profile = plugin.getProfileProvider().getProfile(target);
        TableGenerator generator = new TableGenerator(TableGenerator.TableAlignment.CENTER, TableGenerator.TableAlignment.CENTER, TableGenerator.TableAlignment.CENTER);
        generator.addRow(color("&8-----------------"), color("&8-------"), color("&8------"));
        generator.addRow(color("&d" + lang.getMessage(Message.DATE) + "/" + lang.getMessage(Message.TIME) + ""),
                color("&d" + lang.getMessage(Message.SOURCE) + ""), color("&d" + lang.getMessage(Message.AMOUNT) + ""));
        generator.addRow(color("&8-----------------"), color("&8-------"), color("&8------"));
        for (KarmaProfile.HistoryEntry entry : profile.getHistory()) {
            String strAmount;
            if (entry.getAmount() > 0) strAmount = color("&a+" + entry.getAmount());
            else strAmount = color("&c" + entry.getAmount());
            generator.addRow(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entry.getDate()), entry.getSource().toString(), strAmount);
        }
        List<String> rows;
        if (sender instanceof Player) {
            rows = generator.generate(TableGenerator.Receiver.CLIENT, true, true);
        } else {
            rows = generator.generate(TableGenerator.Receiver.CONSOLE, true, true);
        }
        for (String row : rows) {
            sender.sendMessage(color(row));
        }
        if (sender instanceof Player) sound.play(((Player) sender).getLocation(), SoundCategory.MASTER, (Player) sender);
    }

}
