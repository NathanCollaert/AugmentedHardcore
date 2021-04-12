package com.backtobedrock.augmentedhardcore.mappers.player;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

public class MySQLPlayerMapper implements IPlayerMapper {
    @Override
    public void insertPlayerDataAsync(PlayerData data) {
        //TODO: implement
    }

    @Override
    public void insertPlayerDataSync(PlayerData data) {
        //TODO: implement
    }

    @Override
    public CompletableFuture<PlayerData> getByPlayer(OfflinePlayer player) {
        //TODO: implement
        return null;
    }

    @Override
    public PlayerData getByPlayerSync(OfflinePlayer player) {
        //TODO: implement
        return null;
    }

    @Override
    public void updatePlayerData(PlayerData data) {
        //TODO: implement
    }

    @Override
    public void deletePlayerData(OfflinePlayer player) {
        //TODO: implement
    }
}
