package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public abstract class AbstractGui {
    protected final AugmentedHardcore plugin;
    protected final CustomHolder customHolder;

    public AbstractGui(CustomHolder customHolder) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.customHolder = customHolder;
        this.initialize();
    }

    protected void initialize() {
        for (int i = 0; i < this.customHolder.getSize(); i++) {
            int calc = i % 9;
            if (i < 9 || i >= (this.customHolder.getRowAmount() - 1) * 9 || calc == 0 || calc == 8) {
                this.customHolder.setIcon(i, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getBorderDisplay().getItem(), Collections.emptyList()));
            }
        }
    }

    protected abstract void setData();

    public Inventory getInventory() {
        return this.customHolder.getInventory();
    }
}
