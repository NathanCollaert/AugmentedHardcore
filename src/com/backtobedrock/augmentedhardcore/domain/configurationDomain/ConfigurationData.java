package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import com.backtobedrock.augmentedhardcore.domain.Database;
import com.backtobedrock.augmentedhardcore.domain.enums.StorageType;
import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigurationData {
    private final StorageType storageType;
    private final Database database;

    public ConfigurationData(StorageType storageType, Database database) {
        this.storageType = storageType;
        this.database = database;
    }

    public static ConfigurationData deserialize(ConfigurationSection section) {
        //configurations
        StorageType cStorageType = ConfigUtils.getStorageType("StorageType", section.getString("StorageType", "YAML"));
        ConfigurationSection connectionSection = section.getConfigurationSection("Connection");
        Database cConnection = (cStorageType == StorageType.MYSQL && connectionSection != null) ? Database.deserialize(connectionSection) : null;

        if (cStorageType == StorageType.MYSQL && cConnection == null) {
            return null;
        }

        return new ConfigurationData(cStorageType, cConnection);
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public Database getDatabase() {
        return database;
    }
}
