package com.backtobedrock.augmentedhardcore.repositories;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.StorageType;
import com.backtobedrock.augmentedhardcore.mappers.player.IPlayerMapper;
import com.backtobedrock.augmentedhardcore.mappers.player.MySQLPlayerMapper;
import com.backtobedrock.augmentedhardcore.mappers.player.YAMLPlayerMapper;
import com.backtobedrock.augmentedhardcore.runnables.ClearCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerRepository {
    private final AugmentedHardcore plugin;

    //player cache
    private final Map<UUID, PlayerData> playerCache;
    private IPlayerMapper mapper;

    public PlayerRepository() {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.playerCache = new HashMap<>();
        this.initializeMapper();
    }

    public void onReload() {
        this.initializeMapper();
        this.playerCache.forEach((key, value) -> value.onReload(Bukkit.getPlayer(key)));
    }

    private void initializeMapper() {
        if (this.plugin.getConfigurations().getDataConfiguration().getStorageType() == StorageType.MYSQL) {
            this.mapper = new MySQLPlayerMapper();
        } else {
            this.mapper = new YAMLPlayerMapper();
        }
    }

    public void insertPlayerDataAsync(OfflinePlayer player, PlayerData data) {
        this.mapper.insertPlayerDataAsync(player, data);
    }

    public CompletableFuture<PlayerData> getByPlayer(OfflinePlayer player) {
        if (!this.playerCache.containsKey(player.getUniqueId())) {
            return this.mapper.getByPlayer(player).thenApply(playerData -> this.getFromDataAndCache(player, playerData));
        } else {
            return CompletableFuture.supplyAsync(() -> player).thenApply(this::getFromCache);
        }
    }

    private PlayerData getFromDataAndCache(OfflinePlayer player, PlayerData playerData) {
        if (playerData == null) {
            playerData = new PlayerData(player);
            if (player.hasPlayedBefore())
                this.insertPlayerDataAsync(player, playerData);
        }
        this.playerCache.put(player.getUniqueId(), playerData);

        if (!player.isOnline())
            new ClearCache(player).runTaskLater(this.plugin, 6000);

        return this.getFromCache(player);
    }

    private PlayerData getFromCache(OfflinePlayer player) {
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
