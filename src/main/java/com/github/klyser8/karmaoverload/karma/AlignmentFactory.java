package com.github.klyser8.karmaoverload.karma;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.KarmaAction;
import com.github.klyser8.karmaoverload.api.Sound;
import com.github.klyser8.karmaoverload.karma.actions.*;
import com.github.klyser8.karmaoverload.karma.effects.*;
import com.github.klyser8.karmaoverload.api.KarmaEffect;
import com.github.klyser8.karmaoverload.storage.DebugLevel;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

import static com.github.klyser8.karmaoverload.karma.effects.KarmaEffectType.*;
import static com.github.klyser8.karmaoverload.util.RandomUtil.debugMessage;

/**
 * Parses and creates the plugin's alignments. These can be found in
 * the 'alignments' folder, each yaml file inside being an alignment.
 * This class should mostly be left alone, as creating new instances
 * of it could break the plugin.
 */
public class AlignmentFactory {

    private final KarmaOverload plugin;

    public AlignmentFactory(KarmaOverload plugin) {
        this.plugin = plugin;
    }

    /**
     * Used to parse lines such as "PASSIVE: amount = 1.0; interval = 300", and collect
     * the values needed to create karma Effects and Actions.
     *
     * Static for ease of access.
     * @param line any string found inside any alignment.yml file
     * @return a map storing values necessary to the Karma effect/action
     */
    public static Map<String, String> parseLine(String line) {
        line = line.replace(" ", "");
        line = line.replace("}", "");
        Map<String, String> effectMap = new HashMap<>();
        line = line.replace(" ", "");
        String[] values = line.split(";");
        for (String value : values) {
            effectMap.put(value.substring(0, value.indexOf('=')), value.substring(value.indexOf('=') + 1));
        }
        if (!effectMap.containsKey("chance")) effectMap.put("chance", "1.0");
        return effectMap;
    }

    public void setup() {
        File alignFolder = new File(plugin.getDataFolder(), "alignments");
        if(!alignFolder.exists()) {
            alignFolder.mkdir();
            List<String> defaultAlignments = Arrays.asList("best", "pure", "kind", "nice", "neutral", "rude", "mean", "vile", "evil");
            for (String alignment : defaultAlignments) {
                plugin.saveResource("alignments/" + alignment + ".yml", false);
            }
        }
        createAlignments();
    }

    private void createAlignments() {
        plugin.getAlignments().clear();
        File dir = new File(plugin.getDataFolder().getPath() + "/alignments");
        File[] alignmentListing = dir.listFiles();
        if (alignmentListing == null) throw new NullPointerException("No alignment.yml files found!");
        for (File alignFile : alignmentListing) {
            if (!alignFile.getName().contains(".yml")) continue;
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(alignFile);
            String colorString = yaml.getString("Color");
            if (colorString == null) throw new NullPointerException(alignFile + " has no color!");
            Alignment alignment = new Alignment(yaml.getInt("Thresholds.Low"), yaml.getInt("Thresholds.High"),
                    yaml.getString("Name"), createColor(colorString), Sound.fromString(yaml.getString("Sound")), yaml.getBoolean("Particles"),
                    yaml.getDouble("Kill Penalty"), yaml.getDouble("Karma Limit.Amount"), yaml.getDouble("Karma Gain Repeat Multiplier"),
                    yaml.getDouble("Karma Loss Repeat Multiplier"), createKarmaActions(yaml), createKarmaEffects(yaml), yaml.getStringList("Commands"));
            plugin.getAlignments().add(alignment);
            debugMessage(plugin, "Created alignment: " + alignment.getName(), DebugLevel.LOW);
        }
    }

    private ChatColor createColor(String colorString) {
        ChatColor color;
        if (colorString.length() == 2) color = ChatColor.getByChar(colorString.charAt(1));
        else color = ChatColor.of(colorString);
        return color;
    }

    private Map<KarmaActionType, KarmaAction> createKarmaActions(YamlConfiguration yaml) {
        Map<KarmaActionType, KarmaAction> karmaActions = new HashMap<>();
        ConfigurationSection section = yaml.getConfigurationSection("Actions");
        if (section == null) return karmaActions;
        for (String action : section.getKeys(false)) {
            KarmaActionType type = KarmaActionType.valueOf(action);
            Map<String, String> values = parseLine(section.getString(action));
            debugMessage(plugin, "Action found in " + yaml.getName() + ": " + type.toString(), DebugLevel.LOW);
            switch (type) {
                case PASSIVE:
                    karmaActions.put(KarmaActionType.PASSIVE,
                            new PassiveKarmaAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    Integer.parseInt(values.get("interval")), values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case FRIENDLY_MOB_KILLING:
                    karmaActions.put(KarmaActionType.FRIENDLY_MOB_KILLING,
                            new GenericAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case PASSIVE_MOB_KILLING:
                    karmaActions.put(KarmaActionType.PASSIVE_MOB_KILLING,
                            new GenericAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case HOSTILE_MOB_KILLING:
                    karmaActions.put(KarmaActionType.HOSTILE_MOB_KILLING,
                            new GenericAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case PLAYER_HITTING:
                    karmaActions.put(KarmaActionType.PLAYER_HITTING,
                            new GenericAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case FRIENDLY_MOB_HITTING:
                    karmaActions.put(KarmaActionType.FRIENDLY_MOB_HITTING,
                            new GenericAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case TRADING:
                    karmaActions.put(KarmaActionType.TRADING,
                            new GenericAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case BARTERING:
                    karmaActions.put(KarmaActionType.BARTERING,
                            new GenericAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case TAMING:
                    karmaActions.put(KarmaActionType.TAMING,
                            new GenericAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case FEEDING:
                    karmaActions.put(KarmaActionType.FEEDING,
                            new FeedingAction(plugin, Double.parseDouble(values.get("amount")), Double.parseDouble(values.get("chance")),
                                    Boolean.parseBoolean(values.get("babiesEnabled")), values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case EATING:
                    karmaActions.put(KarmaActionType.EATING,
                            new GenericMaterialListAction(plugin, section.getStringList("EATING")));
                    break;
                case BLOCK_PLACING:
                    karmaActions.put(KarmaActionType.BLOCK_PLACING,
                            new GenericMaterialListAction(plugin, section.getStringList("BLOCK_PLACING")));
                    break;
                case BLOCK_BREAKING:
                    karmaActions.put(KarmaActionType.BLOCK_BREAKING,
                            new GenericMaterialListAction(plugin, section.getStringList("BLOCK_BREAKING")));
                    break;
                case MESSAGING:
                    karmaActions.put(KarmaActionType.MESSAGING,
                            new MessagingAction(plugin, section.getStringList("MESSAGING")));
                    break;
                case ADVANCEMENT:
                    karmaActions.put(KarmaActionType.ADVANCEMENT,
                            new AdvancementAction(plugin, section.getStringList("ADVANCEMENT")));
                    break;
            }
        }
        return karmaActions;
    }

    private Map<KarmaEffectType, KarmaEffect> createKarmaEffects(YamlConfiguration yaml) {
        Map<KarmaEffectType, KarmaEffect> karmaEffects = new HashMap<>();
        ConfigurationSection section = yaml.getConfigurationSection("Effects");
        if (section == null) return karmaEffects;
        for (String effect : section.getKeys(false)) {
            KarmaEffectType type = KarmaEffectType.valueOf(effect);
            Map<String, String> values = parseLine(section.getString(effect));
            debugMessage(plugin, "Effect found in " + yaml.getName() + ": " + type.toString(), DebugLevel.LOW);
            switch (type) {
                case EXP_MULTIPLIER:
                    karmaEffects.put(EXP_MULTIPLIER,
                        new ExperienceMultiplierEffect(plugin, Double.parseDouble(values.get("chance")), Double.parseDouble(values.get("mult")),
                                values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case KARMA_MULTIPLIER:
                    karmaEffects.put(KARMA_MULTIPLIER,
                        new KarmaMultiplierEffect(plugin, Double.parseDouble(values.get("chance")), Double.parseDouble(values.get("gainMult")),
                                Double.parseDouble(values.get("lossMult")), values.get("permission"), Sound.fromString(values.get("gainSound")),
                                Sound.fromString(values.get("lossSound"))));
                    break;
                case LIGHTNING:
                    karmaEffects.put(LIGHTNING,
                            new LightningEffect(plugin, Double.parseDouble(values.get("chance")), values.get("permission"),
                                    Sound.fromString(values.get("prepareSound")), Sound.fromString(values.get("strikeSound"))));
                    break;
                case MOB_ANGER:
                    karmaEffects.put(MOB_ANGER,
                            new MobAngerEffect(plugin, Double.parseDouble(values.get("chance")), Integer.parseInt(values.get("radius")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case LOOT_GENERATION:
                    karmaEffects.put(LOOT_GENERATION,
                            new LootGenerationEffect(plugin, Double.parseDouble(values.get("chance")), Double.parseDouble(values.get("mult")),
                                    values.get("permission"), Sound.fromString(values.get("sound"))));
                    break;
                case MINERAL_REGEN:
                    karmaEffects.put(MINERAL_REGEN,
                            new MiningEffect(plugin, section.getStringList("MINERAL_REGEN"), true));
                    break;
                case MINERAL_FAIL:
                    karmaEffects.put(MINERAL_FAIL,
                            new MiningEffect(plugin, section.getStringList("MINERAL_FAIL"), false));
                    break;
            }
        }

        return karmaEffects;
    }

}
