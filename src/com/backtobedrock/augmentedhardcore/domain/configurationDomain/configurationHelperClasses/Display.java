package com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import com.backtobedrock.augmentedhardcore.utils.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class Display {
    private final Material material;
    private final String name;
    private final List<String> lore;
    private final int amount;

    public Display(Material material, String name, List<String> lore, int amount) {
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.amount = amount;
    }

    public static Display deserialize(String id, ConfigurationSection section) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        Material cMaterial = ConfigUtils.getMaterial(id + ".Material", section.getString("Material"));
        String cName = section.getString("Name");
        List<String> cLore = section.getStringList("Lore");
        int cAmount = ConfigUtils.checkMinMax(id + ".Amount", section.getInt("Amount", 1), 1, Integer.MAX_VALUE);

        if (cName == null) {
            plugin.getLogger().log(Level.SEVERE, id + ".Name: %s is not a valid name.");
            return null;
        }

        if (cAmount == -10 || cMaterial == null) {
            return null;
        }

        return new Display(cMaterial, cName, cLore, cAmount);
    }

    public ItemStack getItem() {
        return MessageUtils.replaceItemNameAndLorePlaceholders(InventoryUtils.createItem(this.material, this.getName(), this.getLore(), this.amount, false), new HashMap<>());
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLore() {
        return this.lore;
    }
}
