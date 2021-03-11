package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractEventListener implements Listener {
    protected final LiteDeathBan plugin;

    public AbstractEventListener() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }
}
