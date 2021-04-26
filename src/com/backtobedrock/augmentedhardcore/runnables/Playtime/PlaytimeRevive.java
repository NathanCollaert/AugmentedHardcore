package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;

public class PlaytimeRevive extends AbstractPlaytime {
    public PlaytimeRevive(PlayerData playerData) {
        super(playerData);
    }

    @Override
    protected void timerTask() {
        this.playerData.decreaseTimeTillNextRevive(this.period);
    }
}