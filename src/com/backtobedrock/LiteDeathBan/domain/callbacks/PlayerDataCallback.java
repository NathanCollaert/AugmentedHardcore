package com.backtobedrock.LiteDeathBan.domain.callbacks;

import com.backtobedrock.LiteDeathBan.domain.PlayerData;

public interface PlayerDataCallback {
    void onQueryDonePlayerData(PlayerData data);
}
