package com.github.klyser8.karmaoverload.language;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.storage.DebugLevel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.github.klyser8.karmaoverload.util.RandomUtil.debugMessage;
import static me.mattstudios.mf.base.components.MfUtil.color;

public class LanguageHandler {

    private final Karma plugin;

    public LanguageHandler(Karma plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration lang;
    private File langFile;

    public void setup() {
        File languageFolder = new File(plugin.getDataFolder(), "lang");
        if(!languageFolder.exists()) {
            languageFolder.mkdir();
        }
        List<String> languages = Arrays.asList("english", "italian", "spanish", "russian", "vietnamese", "simplified_chinese", "french", "dutch");
        for (String language : languages) {
            if (!new File(plugin.getDataFolder().getPath() + File.separator + "lang/" + language + ".yml").exists())
                plugin.saveResource("lang/" + language + ".yml", false);
        }
        setupLanguage();
    }

    private void setupLanguage() {
        String languageFile;
        String configLang = plugin.getPreferences().getLanguage();
        if (configLang == null) return;
        switch (configLang.toLowerCase()) {
            default: languageFile = "english.yml";
                break;
            case "italian": languageFile = "italian.yml";
                break;
            case "spanish": languageFile = "spanish.yml";
                break;
            case "russian": languageFile = "russian.yml";
                break;
            case "vietnamese": languageFile = "vietnamese.yml";
                break;
            case "simplified chinese": languageFile = "simplified_chinese.yml";
                break;
            case "french": languageFile = "french.yml";
                break;
            case "dutch": languageFile = "dutch.yml";
                break;
        }
        langFile = new File(plugin.getDataFolder() + File.separator + "lang", languageFile);
        debugMessage(plugin, "Language file: " + langFile.toString(), DebugLevel.LOW);
        lang = YamlConfiguration.loadConfiguration(langFile);
    }

    public FileConfiguration getLang() {
        if (lang == null) {
            Bukkit.getServer().getLogger().severe("Could not retrieve " + plugin.getPreferences().getLanguage() + ".yml!");
            return null;
        }
        return lang;
    }

    public String getMessage(Message message) {
        return color(Objects.requireNonNull(lang.getString(message.toString()))
                .replace("<LOW_LIMIT>", plugin.getPreferences().getLowLimit() + "")
                .replace("<HIGH_LIMIT>", plugin.getPreferences().getHighLimit() + ""));
    }
}