package com.backtobedrock.LiteDeathBan.mappers.server;

import com.backtobedrock.LiteDeathBan.domain.callbacks.IServerDataCallback;
import com.backtobedrock.LiteDeathBan.domain.data.ServerData;

public interface IServerMapper {
    //Create
    void insertServerDataAsync(ServerData data);

    void insertServerDataSync(ServerData data);

    //Read
    void getServerData(IServerDataCallback callback);

    ServerData getServerDataSync();

    //Update
    void updateServerData(ServerData data);

    //Delete
    void deleteServerData();
}
