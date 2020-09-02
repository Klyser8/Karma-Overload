package com.github.klyser8.karmaoverload.karma;

import com.github.klyser8.karmaoverload.KarmaOverload;
import com.github.klyser8.karmaoverload.api.events.AlignmentChangeEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaGainEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaLossEvent;
import com.github.klyser8.karmaoverload.api.events.KarmaPreEffectEvent;
import com.github.klyser8.karmaoverload.karma.effects.*;
import com.github.klyser8.karmaoverload.storage.DebugLevel;
import com.github.klyser8.karmaoverload.storage.Preferences;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

import static com.github.klyser8.karmaoverload.karma.effects.KarmaEffectType.*;
import static com.github.klyser8.karmaoverload.util.MathUtil.calculateChance;
import static com.github.klyser8.karmaoverload.util.RandomUtil.debugMessage;

public class EffectListener implements Listener {

    private final KarmaOverload plugin;
    private final Random random;

    public EffectListener(KarmaOverload plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onExperienceChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaEffects().containsKey(KarmaEffectType.EXP_MULTIPLIER)) return;
        ExperienceMultiplierEffect effect = (ExperienceMultiplierEffect) alignment.getKarmaEffects().get(KarmaEffectType.EXP_MULTIPLIER);
        KarmaPreEffectEvent effectEvent = new KarmaPreEffectEvent(profile, EXP_MULTIPLIER, effect);
        Bukkit.getPluginManager().callEvent(effectEvent);
        if (effectEvent.isCancelled()) return;
        if (effect.getPermission() != null && !player.hasPermission(effect.getPermission())) return;
        if (!calculateChance(effect.getChance())) return;
        event.setAmount((int) Math.round(event.getAmount() * effect.getMultiplier()));
        if (effect.getSound() != null) effect.getSound().play(player.getLocation(), SoundCategory.NEUTRAL, player);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Mob) || event.getEntity().getKiller() == null) return;
        Mob mob = (Mob) event.getEntity();
        Player player = mob.getKiller();
        List<ItemStack> drops = event.getDrops();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaEffects().containsKey(LOOT_GENERATION)) return;
        LootGenerationEffect effect = (LootGenerationEffect) alignment.getKarmaEffects().get(LOOT_GENERATION);
        KarmaPreEffectEvent effectEvent = new KarmaPreEffectEvent(profile, LOOT_GENERATION, effect);
        Bukkit.getPluginManager().callEvent(effectEvent);
        if (effectEvent.isCancelled()) return;
        if (effect.getPermission() != null && !player.hasPermission(effect.getPermission())) return;
        if (!calculateChance(effect.getChance())) return;
        drops = effect.increaseLoot(drops);
        event.getDrops().clear();
        event.getDrops().addAll(drops);
        if (effect.getSound() != null) effect.getSound().play(event.getEntity().getLocation(), SoundCategory.NEUTRAL, player);
    }

    @EventHandler
    public void onMineralMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (player.getInventory().getItemInMainHand().getItemMeta() != null && player.getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) return;
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaEffects().containsKey(KarmaEffectType.MINERAL_REGEN)) return;
        MiningEffect effect = (MiningEffect) alignment.getKarmaEffects().get(KarmaEffectType.MINERAL_REGEN);
        KarmaPreEffectEvent effectEvent = new KarmaPreEffectEvent(profile, MINERAL_REGEN, effect);
        Bukkit.getPluginManager().callEvent(effectEvent);
        if (effectEvent.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getDrops(player.getInventory().getItemInMainHand()).isEmpty()) return;
        Material blockType = block.getType();
        World world = block.getWorld();

        MiningEffect.KarmaMineral mineral = effect.getMineralMap().get(blockType);
        if (mineral == null) return;
        if (mineral.getPermission() != null && !player.hasPermission(mineral.getPermission())) return;
        if (!calculateChance(mineral.getChance())) return;
        if (mineral.getBreakSound() != null) mineral.getBreakSound().play(block.getLocation(), SoundCategory.BLOCKS);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            System.out.println(mineral.getAppearSound());
            if (mineral.getAppearSound() != null) mineral.getAppearSound().play(block.getLocation(), SoundCategory.BLOCKS);
            world.getBlockAt(block.getLocation()).setType(blockType);
        }, 30);
    }

    @EventHandler
    public void onMineralFail(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaEffects().containsKey(MINERAL_FAIL)) return;
        MiningEffect effect = (MiningEffect) alignment.getKarmaEffects().get(MINERAL_FAIL);
        KarmaPreEffectEvent effectEvent = new KarmaPreEffectEvent(profile, MINERAL_FAIL, effect);
        Bukkit.getPluginManager().callEvent(effectEvent);
        if (effectEvent.isCancelled()) return;
        Block block = event.getBlock();
        if (block.getDrops(player.getInventory().getItemInMainHand()).isEmpty()) return;
        Material blockType = block.getType();
        World world = block.getWorld();
        MiningEffect.KarmaMineral mineral = effect.getMineralMap().get(blockType);
        if (mineral == null) return;
        if (mineral.getPermission() != null && !player.hasPermission(mineral.getPermission())) return;
        if (!calculateChance(mineral.getChance())) return;
        event.setDropItems(false);
        if (blockType.toString().contains("NETHER")) world.dropItem(block.getLocation(), new ItemStack(Material.NETHERRACK));
        else world.dropItem(block.getLocation(), new ItemStack(Material.COBBLESTONE));
        if (mineral.getBreakSound() != null) mineral.getBreakSound().play(block.getLocation(), SoundCategory.BLOCKS);
    }

    @EventHandler
    public void onLightningStrike(LightningStrikeEvent event) {
        if (event.getCause() != LightningStrikeEvent.Cause.WEATHER) return;
        for (Player player : event.getWorld().getPlayers()) {
            KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
            Alignment alignment = profile.getAlignment();
            if (!alignment.getKarmaEffects().containsKey(KarmaEffectType.LIGHTNING)) continue;
            LightningEffect effect = (LightningEffect) alignment.getKarmaEffects().get(KarmaEffectType.LIGHTNING);
            KarmaPreEffectEvent effectEvent = new KarmaPreEffectEvent(profile, LIGHTNING, effect);
        Bukkit.getPluginManager().callEvent(effectEvent);
            if (effectEvent.isCancelled()) return;
            if (effect.getPermission() != null && !player.hasPermission(effect.getPermission())) continue;
            if (!calculateChance(effect.getChance())) continue;
            int delay = random.nextInt(100) + 100;
            new BukkitRunnable() {
                int count = 0;
                Location oldPlayerLoc = player.getLocation();
                @Override
                public void run() {
                    if (count == 20) {
                        effect.getPrepareSound().play(player.getLocation().add(0, 25, 0), SoundCategory.WEATHER, player);
                    }
                    if (count == delay - 15) {
                        oldPlayerLoc = player.getLocation();
                    }
                    if (count == delay) {
                        effect.strikePlayer(player, oldPlayerLoc);
                        if (effect.getStrikeSound() != null) effect.getStrikeSound().play(player.getLocation(), SoundCategory.WEATHER, player);
                        cancel();
                    }
                    count++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        Player player = event.getPlayer();
        KarmaProfile profile = plugin.getProfileProvider().getProfile(player);
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaEffects().containsKey(KarmaEffectType.MOB_ANGER)) return;
        MobAngerEffect effect = (MobAngerEffect) alignment.getKarmaEffects().get(KarmaEffectType.MOB_ANGER);
        KarmaPreEffectEvent effectEvent = new KarmaPreEffectEvent(profile, MOB_ANGER, effect);
        Bukkit.getPluginManager().callEvent(effectEvent);
        if (effectEvent.isCancelled()) return;
        if (effect.getPermission() != null && !player.hasPermission(effect.getPermission())) return;
        if (!calculateChance(effect.getChance())) return;
        for (Entity entity : player.getNearbyEntities(effect.getRadius(), effect.getRadius(), effect.getRadius())) {
            if (!(entity instanceof LivingEntity)) continue;
            effect.angerMob((LivingEntity) entity, player);
        }
    }

    @EventHandler
    public void onKarmaGain(KarmaGainEvent event) {
        KarmaProfile profile = event.getProfile();
        Player player = profile.getPlayer();
        if (event.getSource() == KarmaSource.COMMAND || event.getSource() == KarmaSource.VOTING) return;
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaEffects().containsKey(KARMA_MULTIPLIER)) return;
        KarmaMultiplierEffect effect = (KarmaMultiplierEffect) alignment.getKarmaEffects().get(KarmaEffectType.KARMA_MULTIPLIER);
        KarmaPreEffectEvent effectEvent = new KarmaPreEffectEvent(profile, KARMA_MULTIPLIER, effect);
        Bukkit.getPluginManager().callEvent(effectEvent);
        if (effectEvent.isCancelled()) return;
        if (effect.getPermission() != null && !player.hasPermission(effect.getPermission())) return;
        if (!calculateChance(effect.getChance())) return;
        event.setGainedKarma(event.getGainedKarma() * effect.getGainMultiplier());
        if (effect.getGainSound() != null) effect.getGainSound().play(player.getLocation(), SoundCategory.NEUTRAL, player);
    }

    @EventHandler
    public void onKarmaLoss(KarmaLossEvent event) {
        KarmaProfile profile = event.getProfile();
        Player player = profile.getPlayer();
        if (event.getSource() == KarmaSource.COMMAND || event.getSource() == KarmaSource.VOTING) return;
        Alignment alignment = profile.getAlignment();
        if (!alignment.getKarmaEffects().containsKey(KARMA_MULTIPLIER)) return;
        KarmaMultiplierEffect effect = (KarmaMultiplierEffect) alignment.getKarmaEffects().get(KarmaEffectType.KARMA_MULTIPLIER);
        KarmaPreEffectEvent effectEvent = new KarmaPreEffectEvent(profile, KARMA_MULTIPLIER, effect);
        Bukkit.getPluginManager().callEvent(effectEvent);
        if (effectEvent.isCancelled()) return;
        if (effect.getPermission() != null && !player.hasPermission(effect.getPermission())) return;
        if (!calculateChance(effect.getChance())) return;
        event.setLostKarma(event.getLostKarma() * effect.getLossMultiplier());
        if (effect.getLossSound() != null) effect.getLossSound().play(player.getLocation(), SoundCategory.NEUTRAL, player);
    }

    @EventHandler
    public void onAlignmentChange(AlignmentChangeEvent event) {
        KarmaProfile profile = event.getProfile();
        Player player = profile.getPlayer();
        for (String command : event.getNewAlignment().getCommands()) {
            command = command.toLowerCase().replaceAll("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
        if (event.getNewAlignment().getAlignSound() != null) event.getNewAlignment().getAlignSound().play(player.getLocation(), SoundCategory.PLAYERS);
        if (event.getNewAlignment().isParticles()) createHelix(profile, event.getNewAlignment());
    }

    @EventHandler
    public void onPreEffect(KarmaPreEffectEvent event) {
        World world = event.getProfile().getPlayer().getWorld();
        Preferences pref = plugin.getPreferences();
        if (!pref.isWorldListEnabler() && pref.getWorldList().contains(world) || pref.isWorldListEnabler() && !pref.getWorldList().contains(world)) {
            event.setCancelled(true);
        }
    }

    private void createHelix(KarmaProfile profile, Alignment alignment) {
        Location loc = profile.getPlayer().getLocation();
        if (loc.getWorld() == null) return;
        new BukkitRunnable() {
            double radius = 1.0;
            double y = 0;
            @Override
            public void run() {
                y+=0.5;
                radius-=0.02;
                double x1 = radius * Math.sin(y);
                double z1 = radius * Math.cos(y);
                Location particleLoc = new Location(loc.getWorld(), loc.getX() + x1, loc.getY() + y, loc.getZ() + z1);
                loc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc,
                        20,0.1, 0.1, 0.1, new Particle.DustOptions(Color.fromRGB(alignment.getColor().getColor().getRed(),
                                alignment.getColor().getColor().getGreen(), alignment.getColor().getColor().getBlue()), 1.5f));
                loc.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 5, 0.05, 0.05, 0.05, 0);

                double x2 = radius * Math.sin(y + Math.PI);
                double z2 = radius * Math.cos(y + Math.PI);
                particleLoc = new Location(loc.getWorld(), loc.getX() + x2, loc.getY() + y, loc.getZ() + z2);
                loc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc,
                        20,0.1, 0.1, 0.1, new Particle.DustOptions(Color.fromRGB(alignment.getColor().getColor().getRed(),
                                alignment.getColor().getColor().getGreen(), alignment.getColor().getColor().getBlue()), 1.5f));
                loc.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 5, 0.05, 0.05, 0.05, 0);
                if (y > 25) {
                    loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, particleLoc, 250, 0, 0, 0, 0.25);
                    loc.getWorld().playSound(particleLoc, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST_FAR, 2.0f, 1.0f);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

}
