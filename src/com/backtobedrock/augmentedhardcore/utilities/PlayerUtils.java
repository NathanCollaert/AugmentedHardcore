package com.backtobedrock.augmentedhardcore.utilities;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.guis.AbstractGui;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerUtils {
    public static void openInventory(Player player, AbstractGui gui) {
        JavaPlugin.getPlugin(AugmentedHardcore.class).addToGuis(player, gui);
        Inventory inventory = gui.getInventory();
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(AugmentedHardcore.class), () -> player.openInventory(inventory));
    }

    public static void setMaxHealth(Player player, double rawAmount) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            if (plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) {
                rawAmount = Math.max(Math.min(rawAmount, plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealth()), plugin.getConfigurations().getMaxHealthConfiguration().getMinHealth());
            }
            double amountChanged = rawAmount - attribute.getBaseValue();
            double health = player.getHealth();
            if (amountChanged < 0 && health < attribute.getBaseValue()) {
                amountChanged = Math.min(0, amountChanged + attribute.getBaseValue() - health);
            }
            attribute.setBaseValue(rawAmount);
            if (health + amountChanged > 0) {
                double finalAmountChanged = amountChanged;
                Bukkit.getScheduler().runTask(plugin, () -> player.setHealth(health + finalAmountChanged));
            }
        }
    }

    public static double getMaxHealth(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            return attribute.getBaseValue();
        }
        return 1;
    }

    public static String getPlayerIP(Player player) {
        if (player.getAddress() != null) {
            return player.getAddress().getAddress().toString().replaceFirst("/", "");
        }
        return "";
    }
}
