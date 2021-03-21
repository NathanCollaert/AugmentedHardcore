package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.guis.clickActions.CloseInventoryClickAction;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ConfirmReviveClickAction;
import com.backtobedrock.augmentedhardcore.utils.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReviveGui extends AbstractGui {
    private final Player reviver;
    private final PlayerData reviverData;
    private final OfflinePlayer reviving;
    private final PlayerData revivingData;
    private final ServerData serverData;

    public ReviveGui(Player reviver, PlayerData reviverData, OfflinePlayer reviving, PlayerData revivingData, ServerData serverData) {
        super(new CustomHolder(21, true, String.format("Reviving %s", reviving.getName())));
        this.reviver = reviver;
        this.reviverData = reviverData;
        this.reviving = reviving;
        this.revivingData = revivingData;
        this.serverData = serverData;
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
        List<String> lore = new ArrayList<>();
        lore.add(String.format("%s current statistics:", this.reviving.getName()));
        lore.add(String.format("    • Lives left: %d", this.revivingData.getLives()));
        if (this.revivingData.isBanned(this.reviving)) {
            Ban ban = serverData.getBan(this.reviving);
            if (ban != null)
                lore.add(String.format("    • Death banned for another: %s", MessageUtils.getTimeFromTicks((int) ChronoUnit.SECONDS.between(LocalDateTime.now(), ban.getExpirationDate()) * 20, false, false)));
        }
        this.customHolder.setIcon(22, new Icon(InventoryUtils.createPlayerSkull(this.reviving.getName(), lore, this.reviving), Collections.emptyList()));
        if (update)
            this.customHolder.updateIcon(22);
    }

    public void updateConfirmation(boolean update) {
        //TODO: get from config
        this.customHolder.setIcon(24, new Icon(InventoryUtils.createPlayerSkull("", Arrays.asList(), this.reviving), Collections.singletonList(new ConfirmReviveClickAction(this.reviver, this.reviverData, this.reviving, this.revivingData))));
        if (update)
            this.customHolder.updateIcon(24);
    }
}
