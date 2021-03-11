package com.backtobedrock.LiteDeathBan.domain.configurationDomain;

import com.backtobedrock.LiteDeathBan.domain.Connection;
import com.backtobedrock.LiteDeathBan.domain.enums.StorageType;
import com.backtobedrock.LiteDeathBan.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;

public class DataConfiguration {
    private final StorageType storageType;
    private final Connection connection;

    public DataConfiguration(StorageType storageType, Connection connection) {
        this.storageType = storageType;
        this.connection = connection;
    }

    public static DataConfiguration deserialize(ConfigurationSection section) {
        //configurations
        StorageType cStorageType = ConfigUtils.getStorageType("StorageType", section.getString("StorageType", "YAML"));
        ConfigurationSection connectionSection = section.getConfigurationSection("Connection");
        Connection cConnection = (cStorageType == StorageType.MYSQL && connectionSection != null) ? Connection.deserialize(connectionSection) : null;

        if (cStorageType == StorageType.MYSQL && cConnection == null) {
            return null;
        }

        return new DataConfiguration(cStorageType, cConnection);
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public Connection getConnection() {
        return connection;
    }
}
