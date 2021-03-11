package com.backtobedrock.LiteDeathBan.repositories;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.callbacks.IServerDataCallback;
import com.backtobedrock.LiteDeathBan.domain.data.ServerData;
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
        this.plugin.getLogger().log(Level.INFO, String.format("Loaded %d ongoing bans.", this.getServerDataSync().getTotalOngoingBans()));
    }

    private void initializeMapper() {
        switch (this.plugin.getConfigurations().getDataConfiguration().getStorageType()) {
            case MYSQL:
                this.mapper = new MySQLServerMapper();
                break;
            default:
                this.mapper = new YAMLServerMapper();
                break;
        }
    }

    public void getServerData(IServerDataCallback callback) {
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

    public ServerData getServerDataSync() {
        if (this.serverData == null) {
            ServerData sd = this.mapper.getServerDataSync();
            if (sd == null) {
                sd = new ServerData();
                this.mapper.insertServerDataSync(sd);
            }
            this.serverData = sd;
        }
        return this.serverData;
    }

    public void updateServerData(ServerData data) {
        this.mapper.updateServerData(data);
    }
}
