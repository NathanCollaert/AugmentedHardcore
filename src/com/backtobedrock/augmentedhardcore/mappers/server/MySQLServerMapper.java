package com.backtobedrock.augmentedhardcore.mappers.server;

import com.backtobedrock.augmentedhardcore.domain.data.ServerData;

import java.util.concurrent.CompletableFuture;

public class MySQLServerMapper implements IServerMapper {

    public void insertServerData(ServerData data) {
        //TODO implement
    }

    @Override
    public void insertServerDataAsync(ServerData data) {
        //TODO implement
    }

    @Override
    public void insertServerDataSync(ServerData data) {
        //TODO implement
    }

    @Override
    public CompletableFuture<ServerData> getServerData() {
        //TODO implement
        return null;
    }

    @Override
    public void updateServerData(ServerData data) {
        //TODO implement
    }

    @Override
    public void deleteServerData() {
        //TODO implement
    }
}
