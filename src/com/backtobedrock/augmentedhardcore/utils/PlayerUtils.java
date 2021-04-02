package com.backtobedrock.augmentedhardcore.utils;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerUtils {
    public static void openInventory(Player player, Inventory inventory) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(AugmentedHardcore.class), () -> player.openInventory(inventory));
    }

    public static double setMaxHealth(Player player, double rawAmount) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double amountChanged = 0;
        if (attribute != null) {
            double amount = Math.max(Math.min(rawAmount, plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealth()), plugin.getConfigurations().getMaxHealthConfiguration().getMinHealth());
            amountChanged = amount - attribute.getBaseValue();
            attribute.setBaseValue(amount);
        }
        return Math.max(0, amountChanged);
    }
}
