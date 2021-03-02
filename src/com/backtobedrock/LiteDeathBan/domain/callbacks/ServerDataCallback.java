package com.backtobedrock.LiteDeathBan.domain.callbacks;

import com.backtobedrock.LiteDeathBan.domain.ServerData;

public interface ServerDataCallback {
    void onQueryDoneServerData(ServerData data);
}
