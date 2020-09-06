package com.github.klyser8.karmaoverload.karma;

import com.github.klyser8.karmaoverload.Karma;
import com.github.klyser8.karmaoverload.api.KarmaWriter;
import com.github.klyser8.karmaoverload.api.events.KarmaGainEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaLossEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaPreActionEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaPreEffectEvent;
import com.github.klyser8.karmaoverload.karma.actions.*;
import com.github.klyser8.karmaoverload.storage.DebugLevel;
import com.github.klyser8.karmaoverload.storage.Preferences;
import com.github.klyser8.karmaoverload.util.RandomUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.github.klyser8.karmaoverload.karma.actions.KarmaActionType.*;
import static com.github.klyser8.karmaoverload.util.MathUtil.calculateChance;
import static com.github.klyser8.karmaoverload.util.RandomUtil.isVersion;

public class ActionListener implements Listener {

    private final Karma plugin;
    public ActionListener(Karma plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;
        LivingEntity entity = event.getEntity();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        KarmaActionType actionType = null;
        if (entity instanceof Tameable || (isVersion("1.13") && entity.getType() == EntityType.VILLAGER) ||
                (!isVersion("1.13") && entity instanceof AbstractVillager)) {
            if (alignment.getKarmaActions().containsKey(FRIENDLY_MOB_KILLING)) {
                actionType = FRIENDLY_MOB_KILLING;
            }
        } else if (entity instanceof Animals) {
            if (alignment.getKarmaActions().containsKey(PASSIVE_MOB_KILLING)) {
                actionType = PASSIVE_MOB_KILLING;
            }
        } else if (entity instanceof Monster){
            if (alignment.getKarmaActions().containsKey(HOSTILE_MOB_KILLING)) {
                actionType = HOSTILE_MOB_KILLING;
            }
        }
        if (actionType == null) return;
        GenericAction action = (GenericAction) alignment.getKarmaActions().get(actionType);
;
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, actionType, action);
        Bukkit.getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) return;
        if (!calculateChance(action.getChance())) return;
        if (player.hasPermission(action.getPermission())) {
            KarmaWriter.changeKarma(plugin, profile, action.getAmount(), KarmaSource.MOB);
            if (action.getSound() != null) action.getSound().play(entity.getLocation(), SoundCategory.NEUTRAL, player);
        }
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (victim.getKiller() == null) return;
        Player killer = victim.getKiller();
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(plugin.getProfileProvider().getProfile(killer), PLAYER_KILLING, null);
        Bukkit.getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) return;
        KarmaProfile victimProfile = plugin.getProfileProvider().getProfile(victim);
        KarmaProfile killerProfile = plugin.getProfileProvider().getProfile(killer);
        KarmaWriter.changeKarma(plugin, killerProfile, victimProfile.getAlignment().getKillPenalty(), KarmaSource.PLAYER);
    }

    @EventHandler
    public void onEntityHitByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)) return;
        Entity victim = event.getEntity();
        if (!(victim instanceof Player) && !(victim instanceof Tameable) && victim.getType() != EntityType.VILLAGER &&
                (!isVersion("1.13") && !(victim instanceof AbstractVillager))) return; //Checks if victim is a player, tameable, a villager or instance of abstract villager
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null && victim instanceof NPC) return;
        Player damager;
        if (event.getDamager() instanceof Player) {
            damager = ((Player) event.getDamager());
        } else {
            if (event.getDamager() instanceof ThrowableProjectile) return;
            damager = (Player) ((Projectile) event.getDamager()).getShooter();
        }
        if (damager == null) return;
        KarmaProfile profile = plugin.getProfileProvider().getProfile(damager);
        KarmaActionType type = null;
        KarmaSource source = null;
        SoundCategory category = null;
        if (victim instanceof Player && profile.getAlignment().getKarmaActions().containsKey(PLAYER_HITTING)) {
            type = PLAYER_HITTING;
            source = KarmaSource.PLAYER;
            category = SoundCategory.PLAYERS;
        } else if ((((isVersion("1.13") && victim.getType() == EntityType.VILLAGER) || victim instanceof Tameable ||
                ((!isVersion("1.13") && victim instanceof AbstractVillager))) &&
                profile.getAlignment().getKarmaActions().containsKey(FRIENDLY_MOB_HITTING))) { //If version is 1.13, checks for the Villager entity type. Otherwise it checks for instanceof abstract villager
            type = FRIENDLY_MOB_HITTING;
            source = KarmaSource.MOB;
            category = SoundCategory.NEUTRAL;
        }
        if (type == null) return;
        GenericAction action = (GenericAction) profile.getAlignment().getKarmaActions().get(type);
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, type, action);
        Bukkit.getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) return;
        if (!calculateChance(action.getChance())) return;
        if (!damager.hasPermission(action.getPermission())) return;
        KarmaWriter.changeKarma(plugin, profile, action.getAmount() * event.getFinalDamage(), source);
        if (action.getSound() != null) action.getSound().play(damager.getLocation(), category, damager);
    }

    @EventHandler
    public void onVillagerTrade(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() != InventoryType.MERCHANT) return;
        Player player = (Player) event.getWhoClicked();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        if (!profile.getAlignment().getKarmaActions().containsKey(TRADING)) return;
        GenericAction action = (GenericAction) profile.getAlignment().getKarmaActions().get(TRADING);
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, TRADING, action);
        Bukkit.getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) return;
        if (!player.hasPermission(action.getPermission())) return;
        if (!calculateChance(action.getChance())) return;
        if ((event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.AIR) || event.getSlot() != 2) return;
        KarmaWriter.changeKarma(plugin, profile, action.getAmount(), KarmaSource.TRADE);
        if (action.getSound() != null) action.getSound().play(player.getLocation(), SoundCategory.NEUTRAL, player);
    }

    @EventHandler
    public void onPiglinTrade(EntityDropItemEvent event) {
        if (!isVersion("1.16")) return;
        if (!(event.getEntity() instanceof Piglin)) return;
        for (Entity entity : event.getEntity().getNearbyEntities(10, 10, 10)) {
            if (!(entity instanceof Player)) continue;
            Player player = (Player) entity;
            KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
            Alignment alignment = profile.getAlignment();
            if (!alignment.getKarmaActions().containsKey(BARTERING)) continue;
            GenericAction action = (GenericAction) alignment.getKarmaActions().get(BARTERING);
            KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, BARTERING, action);
            Bukkit.getPluginManager().callEvent(actionEvent);
            if (actionEvent.isCancelled()) return;
            if (!calculateChance(action.getChance())) return;
            if (!player.hasPermission(action.getPermission())) continue;
            KarmaWriter.changeKarma(plugin, profile, action.getAmount(), KarmaSource.BARTER);
            if (action.getSound() != null) action.getSound().play(event.getItemDrop().getLocation(), SoundCategory.BLOCKS, player);
        }
    }

    @EventHandler
    public void onTameEvent(EntityTameEvent event) {
        Player player = (Player) event.getOwner();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaActions().containsKey(TAMING)) return;
        GenericAction action = (GenericAction) alignment.getKarmaActions().get(TAMING);
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, TAMING, action);
        Bukkit.getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) return;
        if (!calculateChance(action.getChance())) return;
        if (!player.hasPermission(action.getPermission())) return;
        KarmaWriter.changeKarma(plugin, profile, action.getAmount(), KarmaSource.TAME);
        if (action.getSound() != null) action.getSound().play(event.getEntity().getLocation(), SoundCategory.NEUTRAL, player);
    }

    @EventHandler
    public void onFeedEvent(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Animals)) return;
        Animals animal = (Animals) event.getRightClicked();
        Player player = event.getPlayer();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaActions().containsKey(FEEDING)) return;
        FeedingAction action = (FeedingAction) alignment.getKarmaActions().get(FEEDING);
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, FEEDING, action);
        Bukkit.getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) return;
        if (!calculateChance(action.getChance())) return;
        if (!player.hasPermission(action.getPermission())) return;

        ItemStack offItem = player.getInventory().getItemInOffHand();
        ItemStack mainItem = player.getInventory().getItemInMainHand();
        if (offItem.getType() == Material.SADDLE || mainItem.getType() == Material.SADDLE) return;
        int offHandAmount = player.getInventory().getItemInOffHand().getAmount();
        int mainHandAmount = player.getInventory().getItemInMainHand().getAmount();
        if (!animal.isAdult() && !action.isBabiesEnabled()) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if ((offItem.getAmount() < offHandAmount && event.getHand() == EquipmentSlot.OFF_HAND) ||
            (mainItem.getAmount() < mainHandAmount && event.getHand() == EquipmentSlot.HAND)) {
                KarmaWriter.changeKarma(plugin, profile, action.getAmount(), KarmaSource.FEED);
                if (action.getSound() != null) action.getSound().play(animal.getLocation(), SoundCategory.NEUTRAL, player);
            }
        }, 1);
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        Material foodType = event.getItem().getType();
        if (!alignment.getKarmaActions().containsKey(EATING)) return;
        GenericMaterialListAction action = (GenericMaterialListAction) alignment.getKarmaActions().get(EATING);
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, EATING, action);
        Bukkit.getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) return;
        GenericMaterialListAction.KarmaMaterialData food = action.getMaterialsMap().get(foodType);
        if (food == null) return;
        if (!calculateChance(food.getChance())) return;
        if (!player.hasPermission(food.getPermission())) return;
        KarmaWriter.changeKarma(plugin, profile, food.getAmount(), KarmaSource.FOOD);
        if (food.getSound() != null) food.getSound().play(player.getLocation(), SoundCategory.PLAYERS, player);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        handleBlockEvent(player, event.getBlockPlaced().getType(), BLOCK_PLACING);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        handleBlockEvent(player, event.getBucket(), BLOCK_PLACING);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleBlockEvent(event.getPlayer(), event.getBlock().getType(), BLOCK_BREAKING);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        handleBlockEvent(player, event.getBlockClicked().getType(), BLOCK_BREAKING);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaActions().containsKey(MESSAGING)) return;
        MessagingAction action = (MessagingAction) alignment.getKarmaActions().get(MESSAGING);
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, MESSAGING, action);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(actionEvent));
        if (actionEvent.isCancelled()) return;
        for (String string : action.getMessageMap().keySet()) {
            if (!message.toLowerCase().contains(string.toLowerCase())) continue;
            MessagingAction.MessageData data = action.getMessageMap().get(string);
            if (!calculateChance(data.getChance())) continue;
            if (!player.hasPermission(data.getPermission())) continue;
            Bukkit.getScheduler().runTask(plugin, () -> KarmaWriter.changeKarma(plugin, profile, data.getAmount(), KarmaSource.CHAT));
            if (data.getSound() != null) data.getSound().play(player.getLocation(), SoundCategory.MASTER, player);
            return;
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        String eventKey = event.getAdvancement().getKey().toString();
        System.out.println(eventKey);
        Player player = event.getPlayer();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        if (profile == null) {
            RandomUtil.errorMessage(plugin, player.getName() + " has not gained/lost Karma for already completed advancements. This is a one time error, and is here" +
                    " just for logging reasons.", DebugLevel.LOW);
            return;
        }
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaActions().containsKey(ADVANCEMENT)) return;
        AdvancementAction action = (AdvancementAction) alignment.getKarmaActions().get(ADVANCEMENT);
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, ADVANCEMENT, action);
        Bukkit.getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) return;
        for (String key : action.getAdvancementMap().keySet()) {
            if (!eventKey.equalsIgnoreCase(key)) continue;
            AdvancementAction.AdvancementData data = action.getAdvancementMap().get(key);
            if (!calculateChance(data.getChance())) return;
            if (!player.hasPermission(data.getPermission())) return;
            KarmaWriter.changeKarma(plugin, profile, data.getAmount(), KarmaSource.ADVANCEMENT);
            if (data.getSound() != null) data.getSound().play(player.getLocation(), SoundCategory.MASTER, player);
            return;
        }
    }

    @EventHandler
    public void onKarmaGain(KarmaGainEvent event) {
        KarmaProfile profile = event.getProfile();
        List<KarmaProfile.HistoryEntry> history = profile.getHistory();
        if (event.getSource() == KarmaSource.COMMAND || event.getSource() == KarmaSource.VOTING) return;
        if (history.size() <= 1) return;
        if (history.get(history.size() - 1).getSource() != event.getSource() || history.get(history.size() - 2).getSource() != event.getSource()) return;
        event.setGainedKarma(event.getGainedKarma() * event.getProfile().getAlignment().getGainRepeatMultiplier());
    }

    @EventHandler
    public void onKarmaLoss(KarmaLossEvent event) {
        KarmaProfile profile = event.getProfile();
        List<KarmaProfile.HistoryEntry> history = profile.getHistory();
        if (event.getSource() == KarmaSource.COMMAND || event.getSource() == KarmaSource.VOTING) return;
        if (history.size() <= 1) return;
        if (history.get(history.size() - 1).getSource() != event.getSource() || history.get(history.size() - 2).getSource() != event.getSource()) return;
        event.setLostKarma(event.getLostKarma() * event.getProfile().getAlignment().getLossRepeatMultiplier());
    }

    @EventHandler
    public void onPreAction(KarmaPreActionEvent event) {
        World world = event.getProfile().getPlayer().getWorld();
        Preferences pref = plugin.getPreferences();
        if ((!pref.isWorldListEnabler() && pref.getWorldList().contains(world)) || (pref.isWorldListEnabler() && !pref.getWorldList().contains(world)) ||
            !pref.getEnabledGameModes().contains(event.getProfile().getPlayer().getGameMode())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreEffect(KarmaPreEffectEvent event) {
        World world = event.getProfile().getPlayer().getWorld();
        Preferences pref = plugin.getPreferences();
        if ((!pref.isWorldListEnabler() && pref.getWorldList().contains(world)) || (pref.isWorldListEnabler() && !pref.getWorldList().contains(world)) ||
                !pref.getEnabledGameModes().contains(event.getProfile().getPlayer().getGameMode())) {
            event.setCancelled(true);
        }
    }

    private void handleBlockEvent(Player player, Material type, KarmaActionType actionType) {
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaActions().containsKey(actionType)) return;
        GenericMaterialListAction action = (GenericMaterialListAction) alignment.getKarmaActions().get(actionType);
        KarmaPreActionEvent actionEvent = new KarmaPreActionEvent(profile, actionType, action);
        Bukkit.getPluginManager().callEvent(actionEvent);
        if (actionEvent.isCancelled()) return;
        GenericMaterialListAction.KarmaMaterialData block = action.getMaterialsMap().get(type);
        if (block == null) return;
        if (!calculateChance(block.getChance())) return;
        if (!player.hasPermission(block.getPermission())) return;
        KarmaWriter.changeKarma(plugin, profile, block.getAmount(), KarmaSource.BLOCK);
        if (block.getSound() != null) block.getSound().play(player.getLocation(), SoundCategory.BLOCKS, player);
    }
}
