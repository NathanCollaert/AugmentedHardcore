package com.backtobedrock.LiteDeathBan.utils;

import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.enums.DamageCauseType;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EventUtils {
    public static boolean isEntityDamageEventFromPlayer(EntityDamageEvent damageEvent) {
        if (damageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
            Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Player) {
                return true;
            } else if (damager instanceof Projectile) {
                Projectile d = (Projectile) entityDamageByEntityEvent.getDamager();
                return d.getShooter() instanceof Player;
            } else if (damager instanceof AreaEffectCloud) {
                AreaEffectCloud d = (AreaEffectCloud) entityDamageByEntityEvent.getDamager();
                return d.getSource() instanceof Player;
            } else if (damager instanceof TNTPrimed) {
                TNTPrimed d = (TNTPrimed) entityDamageByEntityEvent.getDamager();
                return d.getSource() instanceof Player;
            }
        }
        return false;
    }

    public static Killer getDamageEventKiller(EntityDamageEvent damageEvent) {
        if (damageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
            Entity damager = entityDamageByEntityEvent.getDamager();
            if (damager instanceof Projectile) {
                Projectile d = (Projectile) entityDamageByEntityEvent.getDamager();
                if (d.getShooter() instanceof Entity) {
                    Entity source = (Entity) d.getShooter();
                    return new Killer(source.getName(), source.getCustomName(), source.getType());
                }
            } else if (damager instanceof AreaEffectCloud) {
                AreaEffectCloud d = (AreaEffectCloud) entityDamageByEntityEvent.getDamager();
                if (d.getSource() instanceof Entity) {
                    Entity source = (Entity) d.getSource();
                    return new Killer(source.getName(), source.getCustomName(), source.getType());
                }
            } else if (damager instanceof TNTPrimed) {
                TNTPrimed d = (TNTPrimed) entityDamageByEntityEvent.getDamager();
                if (d.getSource() != null) {
                    Entity source = d.getSource();
                    return new Killer(source.getName(), source.getCustomName(), source.getType());
                }
            } else if (damager instanceof FallingBlock) {
                FallingBlock d = (FallingBlock) entityDamageByEntityEvent.getDamager();
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
}
