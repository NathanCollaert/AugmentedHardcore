package com.backtobedrock.LiteDeathBan.mappers.player;

import com.backtobedrock.LiteDeathBan.domain.PlayerData;
import com.backtobedrock.LiteDeathBan.domain.callbacks.PlayerDataCallback;
import org.bukkit.OfflinePlayer;

public interface IPlayerMapper {
    //Create
    void insertPlayerDataAsync(OfflinePlayer player, PlayerData data);

    void insertPlayerDataSync(OfflinePlayer player, PlayerData data);

    //Read
    void getByPlayer(OfflinePlayer player, PlayerDataCallback playerDataCallback);

    //Update
    void updatePlayerData(OfflinePlayer player, PlayerData data);

    //Delete
    void deletePlayerData(OfflinePlayer player);
}
