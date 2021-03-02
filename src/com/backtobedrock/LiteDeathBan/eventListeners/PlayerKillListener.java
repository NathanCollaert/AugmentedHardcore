package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerKillListener implements Listener {
    private final LiteDeathBan plugin;

    public PlayerKillListener() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }
}
