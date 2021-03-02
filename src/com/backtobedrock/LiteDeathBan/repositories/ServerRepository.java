package com.backtobedrock.LiteDeathBan.repositories;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.ServerData;
import com.backtobedrock.LiteDeathBan.domain.callbacks.ServerDataCallback;
import com.backtobedrock.LiteDeathBan.mappers.server.IServerMapper;
import com.backtobedrock.LiteDeathBan.mappers.server.MySQLServerMapper;
import com.backtobedrock.LiteDeathBan.mappers.server.YAMLServerMapper;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ServerRepository {
    private final LiteDeathBan plugin;

    //server cache
    private ServerData serverData = null;
    private IServerMapper mapper;

    public ServerRepository() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.initializeMapper();
        this.getServerData(data -> {
            this.plugin.getLogger().log(Level.INFO, String.format("Loaded %d ongoing bans.", data.getTotalOngoingBans()));
        });
    }

    private void initializeMapper() {
        switch (this.plugin.getConfiguration().getDataConfiguration().getStorageType()) {
            case MYSQL:
                this.mapper = new MySQLServerMapper();
                break;
            default:
                this.mapper = new YAMLServerMapper();
                break;
        }
    }

    public void insertServerData(ServerData data) {
        this.mapper.insertServerDataAsync(data);
    }

    public void getServerData(ServerDataCallback callback) {
        if (this.serverData == null) {
            this.mapper.getServerData(data -> {
                ServerData sd = data;
                if (sd == null) {
                    sd = new ServerData();
                    this.mapper.insertServerDataAsync(sd);
                }
                this.serverData = sd;

                callback.onQueryDoneServerData(this.serverData);
            });
        } else {
            callback.onQueryDoneServerData(this.serverData);
        }
    }

    public void updateServerData(ServerData data) {
        this.mapper.updateServerData(data);
    }

    public void deleteServerData() {
        this.mapper.deleteServerData();
    }
}
