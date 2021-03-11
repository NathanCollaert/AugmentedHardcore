package com.backtobedrock.LiteDeathBan.mappers.player;

import com.backtobedrock.LiteDeathBan.domain.callbacks.IPlayerDataCallback;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
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
    public void getByPlayer(OfflinePlayer player, IPlayerDataCallback callback) {
        //TODO: implement
    }

    @Override
    public PlayerData getByPlayerSync(OfflinePlayer player) {
        //TODO: implement
        return null;
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
