package com.backtobedrock.augmentedhardcore.utils;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.guis.AbstractGui;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerUtils {
    public static void openInventory(Player player, AbstractGui gui) {
        JavaPlugin.getPlugin(AugmentedHardcore.class).addToGuis(player, gui);
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(AugmentedHardcore.class), () -> player.openInventory(gui.getInventory()));
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
