package com.backtobedrock.LiteDeathBan.guis.clickActions;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractClickAction {

    protected final LiteDeathBan plugin;

    public AbstractClickAction() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }

    public abstract void execute(Player player);
}
