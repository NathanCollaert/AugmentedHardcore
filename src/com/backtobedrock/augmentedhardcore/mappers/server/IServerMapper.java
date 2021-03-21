package com.backtobedrock.augmentedhardcore.mappers.server;

import com.backtobedrock.augmentedhardcore.domain.data.ServerData;

import java.util.concurrent.CompletableFuture;

public interface IServerMapper {
    //Create
    void insertServerDataAsync(ServerData data);

    void insertServerDataSync(ServerData data);

    //Read
    CompletableFuture<ServerData> getServerData();

    //Update
    void updateServerData(ServerData data);

    //Delete
    void deleteServerData();
}