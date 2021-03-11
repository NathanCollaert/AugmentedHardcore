package com.backtobedrock.LiteDeathBan.guis;

import com.backtobedrock.LiteDeathBan.guis.clickActions.CloseInventoryClickAction;
import com.backtobedrock.LiteDeathBan.guis.clickActions.ConfirmReviveClickAction;
import com.backtobedrock.LiteDeathBan.utils.InventoryUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class ReviveGui extends AbstractGui {
    private final Player reviver;
    private final OfflinePlayer reviving;

    public ReviveGui(Player reviver, OfflinePlayer reviving) {
        super(new CustomHolder(21, true, String.format("Reviving %s", reviving.getName())));
        this.reviving = reviving;
        this.reviver = reviver;
        this.setData();
    }

    @Override
    public void setData() {
        this.updateCancellation(false);
        this.updatePlayerHead(false);
        this.updateConfirmation(false);
    }

    public void updateCancellation(boolean update) {
        //TODO: get from config
        this.customHolder.setIcon(20, new Icon(InventoryUtils.createPlayerSkull("", Arrays.asList(), this.reviving), Collections.singletonList(new CloseInventoryClickAction())));
        if (update)
            this.customHolder.updateIcon(20);
    }

    public void updatePlayerHead(boolean update) {
        //TODO: get from config
        this.customHolder.setIcon(22, new Icon(InventoryUtils.createPlayerSkull("", Arrays.asList(), this.reviving), Collections.emptyList()));
        if (update)
            this.customHolder.updateIcon(22);
    }

    public void updateConfirmation(boolean update) {
        //TODO: get from config
        this.customHolder.setIcon(24, new Icon(InventoryUtils.createPlayerSkull("", Arrays.asList(), this.reviving), Collections.singletonList(new ConfirmReviveClickAction(this.reviver, this.reviving))));
        if (update)
            this.customHolder.updateIcon(24);
    }
}
