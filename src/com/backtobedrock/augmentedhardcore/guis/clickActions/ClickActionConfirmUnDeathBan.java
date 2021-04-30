package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.commands.CommandUnDeathBan;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ClickActionConfirmUnDeathBan extends AbstractClickAction {
    private final OfflinePlayer target;

    public ClickActionConfirmUnDeathBan(OfflinePlayer target) {
        this.target = target;
    }

    @Override
    public void execute(Player player) {
        new CommandUnDeathBan(player, this.target).run();
    }
}
