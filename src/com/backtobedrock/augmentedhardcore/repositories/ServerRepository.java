package com.backtobedrock.augmentedhardcore.repositories;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.mappers.server.IServerMapper;
import com.backtobedrock.augmentedhardcore.mappers.server.YAMLServerMapper;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ServerRepository {
    private final AugmentedHardcore plugin;

    private IServerMapper mapper;

    //server cache
    private ServerData serverData = null;

    public ServerRepository() {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.initializeMapper();
        this.getServerData().thenAccept(serverData -> {
            this.plugin.getLogger().log(Level.INFO, String.format("Loaded %d ongoing death %s.", serverData.getTotalOngoingBans(), serverData.getTotalOngoingBans() != 1 ? "bans" : "ban"));
        });
    }

    private void initializeMapper() {
        this.mapper = new YAMLServerMapper();
        //TODO: uncomment
//        if (this.plugin.getConfigurations().getDataConfiguration().getStorageType() == StorageType.MYSQL) {
//            this.mapper = new MySQLServerMapper();
//        } else {
//            this.mapper = new YAMLServerMapper();
//        }
    }

    public CompletableFuture<ServerData> getServerData() {
        if (this.serverData == null) {
            return this.mapper.getServerData().thenApply(this::getFromDataAndCache);
        } else {
            return CompletableFuture.supplyAsync(this::getFromCache);
        }
    }

    public ServerData getServerDataSync() {
        return this.serverData;
    }

    private ServerData getFromDataAndCache(ServerData serverData) {
        if (serverData == null) {
            serverData = new ServerData();
            this.mapper.insertServerDataAsync(serverData);
        }
        this.serverData = serverData;
        return this.serverData;
    }

    private ServerData getFromCache() {
        return this.serverData;
    }

    public void updateServerData(ServerData data) {
        this.mapper.updateServerData(data);
    }
}
