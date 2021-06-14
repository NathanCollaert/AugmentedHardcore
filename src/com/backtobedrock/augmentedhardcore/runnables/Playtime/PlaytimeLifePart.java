package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;

public class PlaytimeLifePart extends AbstractPlaytime {
    public PlaytimeLifePart(PlayerData playerData, Player player) {
        super(playerData, player);
    }

    @Override
    protected void timerTask() {
        this.playerData.decreaseTimeTillNextLifePart(this.period, this.player);
    }
}
