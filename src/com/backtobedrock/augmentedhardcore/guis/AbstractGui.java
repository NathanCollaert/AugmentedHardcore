package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class AbstractGui {
    protected final AugmentedHardcore plugin;
    protected final CustomHolder customHolder;

    public AbstractGui(CustomHolder customHolder) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.customHolder = customHolder;
    }

    protected void initialize() {
        for (int i = 0; i < this.customHolder.getSize(); i++) {
            int calc = i % 9;
            if (i < 9 || i >= (this.customHolder.getRowAmount() - 1) * 9 || calc == 0 || calc == 8) {
                this.customHolder.setIcon(i, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getFillerDisplay().getItem(), Collections.emptyList()));
            }
        }
    }

    protected void fillGui(List<Integer> ignore) {
        for (int i = 0; i < this.customHolder.getSize(); i++) {
            if (!ignore.contains(i) && this.customHolder.getIcon(i) == null) {
                this.customHolder.setIcon(i, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getFillerDisplay().getItem(), Collections.emptyList()));
            }
        }
    }

    protected void setAccentColor(List<Integer> positions) {
        positions.forEach(e -> this.customHolder.setIcon(e, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getAccentDisplay().getItem(), Collections.emptyList())));
    }

    protected void setIcon(int position, Icon icon, boolean update) {
        this.customHolder.setIcon(position, icon);
        if (update) {
            this.customHolder.updateIcon(position);
        }
    }

    protected abstract void setData();

    public Inventory getInventory() {
        return this.customHolder.getInventory();
    }
}
