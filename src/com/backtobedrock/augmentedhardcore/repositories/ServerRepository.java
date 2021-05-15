package com.backtobedrock.augmentedhardcore.repositories;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.StorageType;
import com.backtobedrock.augmentedhardcore.mappers.server.IServerMapper;
import com.backtobedrock.augmentedhardcore.mappers.server.MySQLServerMapper;
import com.backtobedrock.augmentedhardcore.mappers.server.YAMLServerMapper;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.javatuples.Pair;

import java.util.UUID;
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
        this.getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> this.plugin.getLogger().log(Level.INFO, String.format("Loaded %d ongoing death %s.", serverData.getTotalOngoingBans(), serverData.getTotalOngoingBans() != 1 ? "bans" : "ban"))).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    private void initializeMapper() {
        this.mapper = new YAMLServerMapper();
        if (this.plugin.getConfigurations().getDataConfiguration().getStorageType() == StorageType.MYSQL) {
            this.mapper = MySQLServerMapper.getInstance();
        } else {
            this.mapper = new YAMLServerMapper();
        }
    }

    public CompletableFuture<ServerData> getServerData(Server server) {
        if (this.serverData == null) {
            return this.mapper.getServerData(server).thenApplyAsync(this::getFromDataAndCache);
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

    public void removeBanFromServerData(UUID uuid, Pair<Integer, Ban> banPair) {
        this.mapper.deleteBanFromServerData(uuid, banPair);
    }
}
