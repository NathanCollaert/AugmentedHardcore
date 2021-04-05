package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.guis.clickActions.CloseInventoryClickAction;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ConfirmReviveClickAction;
import com.backtobedrock.augmentedhardcore.utils.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReviveGui extends AbstractGui {
    private final OfflinePlayer reviving;
    private final PlayerData revivingData;
    private final Map<String, String> placeholders;

    public ReviveGui(OfflinePlayer reviving, PlayerData revivingData) {
        super(new CustomHolder(54, true, String.format("Reviving %s", reviving.getName())));
        this.reviving = reviving;
        this.revivingData = revivingData;
        this.placeholders = new HashMap<String, String>() {{
            put("%player%", reviving.getName());
        }};
        this.setData();
    }

    @Override
    public void setData() {
        this.updateCancellation(false);
        this.updatePlayerHead(false);
        this.updateConfirmation(false);
        this.setAccentColor(Arrays.asList(3, 4, 5, 10, 11, 12, 14, 15, 16, 21, 22, 23));
        this.fillGui(Arrays.asList(37, 39, 41, 43));
    }

    public void updateCancellation(boolean update) {
        this.setIcon(38, new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getCancellationDisplay().getItem(), this.placeholders), Collections.singletonList(new CloseInventoryClickAction())), update);
    }

    public void updatePlayerHead(boolean update) {
        List<String> lore = new ArrayList<>();
        lore.add(String.format("%s current statistics:", this.reviving.getName()));
        lore.add(String.format("    • Lives left: %d", this.revivingData.getLives()));
        if (this.revivingData.isBanned(this.reviving)) {
            Ban ban = serverData.getBan(this.reviving);
            if (ban != null)
                lore.add(String.format("    • Death banned for another: %s", MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(LocalDateTime.now(), ban.getExpirationDate()), TimeUnit.SECONDS), false, false)));
        }
        this.setIcon(13, new Icon(InventoryUtils.createPlayerSkull(this.reviving.getName(), lore, this.reviving), Collections.emptyList()), update);
    }

    public void updateConfirmation(boolean update) {
        this.setIcon(42, new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getConfirmationDisplay().getItem(), this.placeholders), Collections.singletonList(new ConfirmReviveClickAction(this.reviver, this.reviverData, this.reviving, this.revivingData))), update);
    }
}
