package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.guis.BansGui;
import com.backtobedrock.augmentedhardcore.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class OpenBansGuiClickAction extends AbstractClickAction {

    private final PlayerData playerData;

    public OpenBansGuiClickAction(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public void execute(Player player) {
        if (playerData.getBanCount() > 0) {
            PlayerUtils.openInventory(player, new BansGui(playerData).getInventory());
        } else {
            player.sendMessage("§cYou luckily have no death bans yet!");
        }
    }
}
