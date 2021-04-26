package com.backtobedrock.augmentedhardcore.mappers.player;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

public interface IPlayerMapper {
    //Create
    void insertPlayerDataAsync(PlayerData data);

    void insertPlayerDataSync(PlayerData data);

    //Read
    CompletableFuture<PlayerData> getByPlayer(OfflinePlayer player);

    PlayerData getByPlayerSync(OfflinePlayer player);

    //Update
    void updatePlayerData(PlayerData data);

    //Delete
    void deletePlayerData(OfflinePlayer player);
}
