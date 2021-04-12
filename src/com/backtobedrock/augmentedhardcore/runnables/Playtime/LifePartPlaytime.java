package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;

public class LifePartPlaytime extends AbstractPlaytime {
    public LifePartPlaytime(PlayerData playerData) {
        super(playerData);
    }

    @Override
    protected void timerTask() {
        this.data.decreaseTimeTillNextLifePart(this.period);
    }
}
