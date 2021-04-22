package com.backtobedrock.augmentedhardcore.repositories;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.StorageType;
import com.backtobedrock.augmentedhardcore.mappers.player.IPlayerMapper;
import com.backtobedrock.augmentedhardcore.mappers.player.MySQLPlayerMapper;
import com.backtobedrock.augmentedhardcore.mappers.player.YAMLPlayerMapper;
import com.backtobedrock.augmentedhardcore.runnables.ClearCache;
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
        this.playerCache.values().forEach(PlayerData::onReload);
    }

    private void initializeMapper() {
        if (this.plugin.getConfigurations().getDataConfiguration().getStorageType() == StorageType.MYSQL) {
            this.mapper = MySQLPlayerMapper.getInstance();
        } else {
            this.mapper = new YAMLPlayerMapper();
        }
    }

    public CompletableFuture<PlayerData> getByPlayer(OfflinePlayer player) {
        if (!this.playerCache.containsKey(player.getUniqueId())) {
            return this.mapper.getByPlayer(player).thenApplyAsync(playerData -> this.getFromDataAndCache(player, playerData));
        } else {
            return CompletableFuture.supplyAsync(() -> player).thenApplyAsync(this::getFromCache).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }
    }

    public PlayerData getByPlayerSync(OfflinePlayer player) {
        if (!this.playerCache.containsKey(player.getUniqueId())) {
            return this.getFromDataAndCache(player, this.mapper.getByPlayerSync(player));
        } else {
            return this.getFromCache(player);
        }
    }

    private PlayerData getFromDataAndCache(OfflinePlayer player, PlayerData playerData) {
        if (playerData == null) {
            playerData = new PlayerData(player);
            if (player.hasPlayedBefore())
                this.mapper.insertPlayerDataAsync(playerData);
        }
        this.playerCache.put(player.getUniqueId(), playerData);

        if (!player.isOnline()) {
            new ClearCache(player).runTaskLater(this.plugin, 6000);
        }

        return this.getFromCache(player);
    }

    private PlayerData getFromCache(OfflinePlayer player) {
        return this.playerCache.get(player.getUniqueId());
    }

    public void updatePlayerData(PlayerData data) {
        this.mapper.updatePlayerData(data);
    }

    public void deletePlayerData(OfflinePlayer player) {
        this.mapper.deletePlayerData(player);
    }

    public void removeFromPlayerCache(OfflinePlayer player) {
        this.playerCache.remove(player.getUniqueId());
    }
}
