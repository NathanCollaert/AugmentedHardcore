package com.backtobedrock.LiteDeathBan.mappers.player;

import com.backtobedrock.LiteDeathBan.domain.PlayerData;
import com.backtobedrock.LiteDeathBan.domain.callbacks.PlayerDataCallback;
import org.bukkit.OfflinePlayer;

public class MySQLPlayerMapper implements IPlayerMapper {
    @Override
    public void insertPlayerDataAsync(OfflinePlayer player, PlayerData data) {
        //TODO: implement
    }

    @Override
    public void insertPlayerDataSync(OfflinePlayer player, PlayerData data) {
        //TODO: implement
    }

    @Override
    public void getByPlayer(OfflinePlayer player, PlayerDataCallback playerDataCallback) {
        //TODO: implement
    }

    @Override
    public void updatePlayerData(OfflinePlayer player, PlayerData data) {
        //TODO: implement
    }

    @Override
    public void deletePlayerData(OfflinePlayer player) {
        //TODO: implement
    }
}
