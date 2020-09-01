package com.github.klyser8.karmaoverload.commands;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.KarmaProfile;
import com.github.klyser8.karmaoverload.language.LanguageHandler;
import com.github.klyser8.karmaoverload.language.Message;
import com.github.klyser8.karmaoverload.util.TableGenerator;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static me.mattstudios.mf.base.components.MfUtil.color;

@Command("karma")
public class ListCommand extends CommandBase {

    private final KarmaOverload plugin;
    private final Sound sound;
    public ListCommand(KarmaOverload plugin) {
        this.plugin = plugin;
        this.sound = new Sound("ui.button.click", 1.0f, 1.0f);
    }

    @SubCommand("list")
    @Completion("#empty")
    @Permission("karma.command.list")
    public void listCommand(CommandSender sender) {
        Map<KarmaProfile, Double> profileMap = new HashMap<>();
        LanguageHandler lang = plugin.getLanguageHandler();
        for (Player player : Bukkit.getOnlinePlayers()) {
            profileMap.put(plugin.getProfileProvider().getProfile(player), plugin.getProfileProvider().getProfile(player).getKarma());
        }
        Map<KarmaProfile, Double> reverseSortedMap = new LinkedHashMap<>();
        profileMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        TableGenerator generator = new TableGenerator(TableGenerator.TableAlignment.CENTER, TableGenerator.TableAlignment.CENTER, TableGenerator.TableAlignment.CENTER);
        generator.addRow(color("&8--------------"), color("&8-------"), color("&8-----------"));
        generator.addRow(color("&d" + lang.getMessage(Message.PLAYER)), color("&dKarma"), color("&d" + lang.getMessage(Message.ALIGNMENT) + ""));
        generator.addRow(color("&8--------------"), color("&8-------"), color("&8-----------"));
        for (KarmaProfile profile : reverseSortedMap.keySet()) {
            generator.addRow(profile.getPlayer().getName(), profile.getKarma() + "", profile.getAlignment().getColor() + profile.getAlignment().getName());
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

/*    @SubCommand("list")
    @Completion("#empty")
    @Permission("karma.command.list")
    public void listCommand(CommandSender sender) {
        Map<KarmaProfile, Double> map = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
            map.put(profile, profile.getKarma());
        }
        Map<KarmaProfile, Double> reverseSortedMap = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        for (KarmaProfile profile : reverseSortedMap.keySet()) {
            sender.sendMessage(plugin.getLanguageHandler().getMessage(Message.VIEW_LIST_PLAYER)
                    .replace("<PLAYER>", profile.getPlayer().getName())
                    .replace("<SCORE>", String.valueOf(reverseSortedMap.get(profile)))
                    .replace("<ALIGNMENT>", profile.getAlignment().getColor() + profile.getAlignment().getName()));
        }
    }*/

}
