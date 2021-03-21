package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ConfirmReviveClickAction extends AbstractClickAction {
    private final Player reviver;
    private final PlayerData reviverData;
    private final OfflinePlayer reviving;
    private final PlayerData revivingData;

    public ConfirmReviveClickAction(Player reviver, PlayerData reviverData, OfflinePlayer reviving, PlayerData revivingData) {
        this.reviver = reviver;
        this.reviverData = reviverData;
        this.reviving = reviving;
        this.revivingData = revivingData;
    }

    @Override
    public void execute(Player player) {
        this.reviverData.onReviving(this.reviver, this.reviving, this.revivingData);
        player.closeInventory();
    }
}
