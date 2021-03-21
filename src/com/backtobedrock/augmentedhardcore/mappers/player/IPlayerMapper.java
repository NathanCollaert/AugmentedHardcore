package com.backtobedrock.augmentedhardcore.mappers.player;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

public interface IPlayerMapper {
    //Create
    void insertPlayerDataAsync(OfflinePlayer player, PlayerData data);

    void insertPlayerDataSync(OfflinePlayer player, PlayerData data);

    //Read
    CompletableFuture<PlayerData> getByPlayer(OfflinePlayer player);

    PlayerData getByPlayerSync(OfflinePlayer player);

    //Update
    void updatePlayerData(OfflinePlayer player, PlayerData data);

    //Delete
    void deletePlayerData(OfflinePlayer player);
}
