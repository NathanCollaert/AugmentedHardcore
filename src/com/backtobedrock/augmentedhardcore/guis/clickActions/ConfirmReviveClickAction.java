package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;

public class ConfirmReviveClickAction extends AbstractClickAction {
    private final PlayerData reviverData;
    private final PlayerData revivingData;

    public ConfirmReviveClickAction(PlayerData reviverData, PlayerData revivingData) {
        this.reviverData = reviverData;
        this.revivingData = revivingData;
    }

    @Override
    public void execute(Player player) {
        this.reviverData.onReviving(this.revivingData);
        player.closeInventory();
    }
}
