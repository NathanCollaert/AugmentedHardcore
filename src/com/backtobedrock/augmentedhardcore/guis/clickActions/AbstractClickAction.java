package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractClickAction {

    protected final AugmentedHardcore plugin;

    public AbstractClickAction() {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
    }

    public abstract void execute(Player player);
}
