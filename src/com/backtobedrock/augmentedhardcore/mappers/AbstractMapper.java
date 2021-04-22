package com.backtobedrock.augmentedhardcore.mappers;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Database;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractMapper {
    protected final AugmentedHardcore plugin;
    protected final Database database;

    public AbstractMapper() {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.database = this.plugin.getConfigurations().getDataConfiguration().getDatabase();
    }
}
