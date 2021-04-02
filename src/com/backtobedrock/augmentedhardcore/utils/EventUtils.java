package com.backtobedrock.augmentedhardcore.utils;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCauseType;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class EventUtils {
    public static boolean isEntityDamageEventFromPlayer(EntityDamageEvent damageEvent) {
        if (damageEvent instanceof EntityDamageByEntityEvent) {
            return isEntityDamageByEntityEventFromPlayer((EntityDamageByEntityEvent) damageEvent);
        }
        return false;
    }

    public static boolean isEntityDamageByEntityEventFromPlayer(EntityDamageByEntityEvent damageEvent) {
        Entity damager = damageEvent.getDamager();
        if (damager instanceof Player) {
            return true;
        } else if (damager instanceof Projectile) {
            Projectile d = (Projectile) damager;
            return d.getShooter() instanceof Player;
        } else if (damager instanceof AreaEffectCloud) {
            AreaEffectCloud d = (AreaEffectCloud) damager;
            return d.getSource() instanceof Player;
        } else if (damager instanceof TNTPrimed) {
            TNTPrimed d = (TNTPrimed) damager;
            return d.getSource() instanceof Player;
        }
        return false;
    }

    public static boolean isEntityDamageByEntityEventFromMonster(EntityDamageByEntityEvent damageEvent) {
        Entity damager = damageEvent.getDamager();
        if (damager.getType().isAlive() && !(damager instanceof Player)) {
            return true;
        } else if (damager instanceof Projectile) {
            Projectile d = (Projectile) damager;
            return d.getShooter() instanceof LivingEntity && !(d.getShooter() instanceof Player);
        } else if (damager instanceof AreaEffectCloud) {
            AreaEffectCloud d = (AreaEffectCloud) damager;
            return d.getSource() instanceof LivingEntity && !(d.getSource() instanceof Player);
        } else if (damager instanceof TNTPrimed) {
            TNTPrimed d = (TNTPrimed) damager;
            return d.getSource() instanceof LivingEntity && !(d.getSource() instanceof Player);
        }
        return false;
    }

    public static Killer getDamageEventKiller(EntityDamageEvent damageEvent) {
        if (damageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
            Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Projectile) {
                Projectile d = (Projectile) damager;
                if (d.getShooter() instanceof Entity) {
                    Entity source = (Entity) d.getShooter();
                    return new Killer(source.getName(), source.getCustomName(), source.getType());
                }
            } else if (damager instanceof AreaEffectCloud) {
                AreaEffectCloud d = (AreaEffectCloud) damager;
                if (d.getSource() instanceof Entity) {
                    Entity source = (Entity) d.getSource();
                    return new Killer(source.getName(), source.getCustomName(), source.getType());
                }
            } else if (damager instanceof TNTPrimed) {
                TNTPrimed d = (TNTPrimed) damager;
                if (d.getSource() != null) {
                    Entity source = d.getSource();
                    return new Killer(source.getName(), source.getCustomName(), source.getType());
                }
            } else if (damager instanceof FallingBlock) {
                FallingBlock d = (FallingBlock) damager;
                return new Killer("falling " + d.getBlockData().getMaterial().name().toLowerCase().replaceAll("_", " "), null, d.getType());
            } else {
                return new Killer(damager.getName(), damager.getCustomName(), damager.getType());
            }
        } else if (damageEvent instanceof EntityDamageByBlockEvent) {
            EntityDamageByBlockEvent entityDamageByBlockEvent = (EntityDamageByBlockEvent) damageEvent;
            Block damager = entityDamageByBlockEvent.getDamager();
            if (damager != null) {
                return new Killer(damager.getType().name().toLowerCase().replaceAll("_", " "), null, null);
            }
        }
        return null;
    }

    public static DamageCauseType getDamageCauseTypeFromEntityDamageEvent(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
            return DamageCauseType.ENTITY;
        } else if (entityDamageEvent instanceof EntityDamageByBlockEvent) {
            return DamageCauseType.BLOCK;
        } else {
            return DamageCauseType.ENVIRONMENT;
        }
    }

    public static DamageCause getDamageCauseFromDamageEvent(PlayerData playerData, EntityDamageEvent event) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        final boolean causeIsPlayer = isEntityDamageEventFromPlayer(event);
        String finalDamageCause;

        if (playerData.isCombatLogged() && playerData.isReviving()) {
            //check if player combat or monster combat
            DamageCause cause = causeIsPlayer ? DamageCause.PLAYER_COMBAT_LOG : DamageCause.COMBAT_LOG;

            //return which bantime is highest
            int bantime = plugin.getConfigurations().getBanTimesConfiguration().getBanTimes().get(cause.name()).getBanTime();
            return plugin.getConfigurations().getBanTimesConfiguration().getBanTimes().get(DamageCause.REVIVE.name()).getBanTime() > bantime
                    ? DamageCause.REVIVE
                    : cause;
        } else if (playerData.isCombatLogged()) {
            return causeIsPlayer ? DamageCause.PLAYER_COMBAT_LOG : DamageCause.COMBAT_LOG;
        } else if (playerData.isReviving()) {
            return DamageCause.REVIVE;
        }

        finalDamageCause = causeIsPlayer ? "PLAYER_" + event.getCause().toString().toUpperCase() : event.getCause().toString().toUpperCase();

        try {
            return DamageCause.valueOf(finalDamageCause);
        } catch (IllegalArgumentException exception) {
            plugin.getLogger().log(Level.SEVERE, String.format("%s was not a known damage type, please report this to the plugin author for more information.", finalDamageCause));
            return null;
        }
    }
}
