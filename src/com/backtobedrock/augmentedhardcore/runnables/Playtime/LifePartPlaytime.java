package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;

public class LifePartPlaytime extends AbstractPlaytime {
    public LifePartPlaytime(Player player, PlayerData playerData) {
        super(player, playerData);
    }

    @Override
    protected void timerTask() {
        this.data.decreaseTimeTillNextLifePart(this.player, this.period);
    }
}
