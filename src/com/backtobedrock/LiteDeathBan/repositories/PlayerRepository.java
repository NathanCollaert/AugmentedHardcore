package com.backtobedrock.LiteDeathBan.repositories;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.callbacks.IPlayerDataCallback;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
import com.backtobedrock.LiteDeathBan.mappers.player.IPlayerMapper;
import com.backtobedrock.LiteDeathBan.mappers.player.MySQLPlayerMapper;
import com.backtobedrock.LiteDeathBan.mappers.player.YAMLPlayerMapper;
import com.backtobedrock.LiteDeathBan.runnables.ClearCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRepository {
    private final LiteDeathBan plugin;

    //player cache
    private final Map<UUID, PlayerData> playerCache;
    private IPlayerMapper mapper;

    public PlayerRepository() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.playerCache = new HashMap<>();
        this.initializeMapper();
    }

    public void onReload() {
        this.initializeMapper();
        this.playerCache.forEach((key, value) -> {
            value.onReload(Bukkit.getPlayer(key));
        });
    }

    private void initializeMapper() {
        switch (this.plugin.getConfigurations().getDataConfiguration().getStorageType()) {
            case MYSQL:
                this.mapper = new MySQLPlayerMapper();
                break;
            default:
                this.mapper = new YAMLPlayerMapper();
                break;
        }
    }

    public void insertPlayerDataAsync(OfflinePlayer player, PlayerData data) {
        this.mapper.insertPlayerDataAsync(player, data);
    }

    public void getByPlayer(OfflinePlayer player, IPlayerDataCallback callback) {
        if (!this.playerCache.containsKey(player.getUniqueId())) {
            this.mapper.getByPlayer(player, data -> {
                PlayerData pd = data;
                if (pd == null) {
                    pd = new PlayerData(player);
                    if (player.hasPlayedBefore())
                        this.insertPlayerDataAsync(player, pd);
                }
                this.playerCache.put(player.getUniqueId(), pd);

                if (!player.isOnline()) {
                    new ClearCache(player).runTaskLater(this.plugin, 6000);
                }

                callback.onQueryDonePlayerData(this.playerCache.get(player.getUniqueId()));
            });
        } else {
            callback.onQueryDonePlayerData(this.playerCache.get(player.getUniqueId()));
        }
    }

    public PlayerData getByPlayerSync(OfflinePlayer player) {
        if (!this.playerCache.containsKey(player.getUniqueId())) {
            PlayerData pd = this.mapper.getByPlayerSync(player);
            if (pd == null) {
                pd = new PlayerData(player);
                if (player.hasPlayedBefore())
                    this.insertPlayerDataAsync(player, pd);
            }
            this.playerCache.put(player.getUniqueId(), pd);

            if (!player.isOnline()) {
                new ClearCache(player).runTaskLater(this.plugin, 6000);
            }
        }
        return this.playerCache.get(player.getUniqueId());
    }

    public void updatePlayerData(OfflinePlayer player, PlayerData data) {
        this.mapper.updatePlayerData(player, data);
    }

    public void deletePlayerData(OfflinePlayer player) {
        this.mapper.deletePlayerData(player);
    }

    public void removeFromPlayerCache(OfflinePlayer player) {
        this.playerCache.remove(player.getUniqueId());
    }
}
