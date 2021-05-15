package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.guis.GuiPlayerDeathBans;
import com.backtobedrock.augmentedhardcore.utilities.PlayerUtils;
import org.bukkit.entity.Player;

public class ClickActionOpenBansGui extends AbstractClickAction {

    private final PlayerData playerData;

    public ClickActionOpenBansGui(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public void execute(Player player) {
        PlayerUtils.openInventory(player, new GuiPlayerDeathBans(this.playerData));
    }
}
