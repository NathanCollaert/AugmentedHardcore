package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractEventListener implements Listener {
    protected final AugmentedHardcore plugin;

    public AbstractEventListener() {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
    }

    public abstract boolean isEnabled();
}
