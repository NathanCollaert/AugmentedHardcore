package com.backtobedrock.LiteDeathBan.mappers.player;

import com.backtobedrock.LiteDeathBan.domain.callbacks.IPlayerDataCallback;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
import org.bukkit.OfflinePlayer;

public interface IPlayerMapper {
    //Create
    void insertPlayerDataAsync(OfflinePlayer player, PlayerData data);

    void insertPlayerDataSync(OfflinePlayer player, PlayerData data);

    //Read
    void getByPlayer(OfflinePlayer player, IPlayerDataCallback callback);

    PlayerData getByPlayerSync(OfflinePlayer player);

    //Update
    void updatePlayerData(OfflinePlayer player, PlayerData data);

    //Delete
    void deletePlayerData(OfflinePlayer player);
}
