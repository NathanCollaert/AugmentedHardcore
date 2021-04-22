package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionCloseInventory;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionConfirmRevive;
import com.backtobedrock.augmentedhardcore.utils.BanUtils;
import com.backtobedrock.augmentedhardcore.utils.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.BanEntry;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GuiRevive extends AbstractGui {
    private final PlayerData reviverData;
    private final OfflinePlayer reviving;
    private PlayerData revivingData;
    private final Map<String, String> placeholders;

    public GuiRevive(PlayerData reviverData, OfflinePlayer reviving) {
        super(new CustomHolder(54, String.format("Reviving %s", reviving.getName())));
        this.reviverData = reviverData;
        this.reviving = reviving;
        this.placeholders = new HashMap<String, String>() {{
            put("player", reviving.getName());
        }};
        this.plugin.getPlayerRepository().getByPlayer(this.reviving).thenAcceptAsync(playerData -> {
            this.revivingData = playerData;
            this.updatePlayerHead(true);
            this.updateConfirmation(true);
        });
        this.initialize();
    }

    @Override
    protected void initialize() {
        this.updateCancellation(false);
        this.updatePlayerHead(true);
        this.updateConfirmation(true);
        this.setData();
    }

    @Override
    public void setData() {
        this.setAccentColor(Arrays.asList(3, 4, 5, 10, 11, 12, 14, 15, 16, 21, 22, 23));
        this.fillGui(Arrays.asList(37, 39, 41, 43));
    }

    public void updateCancellation(boolean update) {
        this.setIcon(38, new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getCancellationDisplay().getItem(), this.placeholders), Collections.singletonList(new ClickActionCloseInventory())), update);
    }

    public void updatePlayerHead(boolean update) {
        Icon icon;
        if (this.revivingData != null) {
            List<String> lore = new ArrayList<>();
            lore.add(String.format("%s current statistics:", this.reviving.getName()));
            lore.add(String.format("    • Lives left: %d", this.revivingData.getLives()));
            BanEntry banEntry = BanUtils.isBanned(this.revivingData);
            if (banEntry != null) {
                lore.add(String.format("    • Death banned for another: %s", MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(banEntry.getExpiration().getTime() - new Date().getTime(), TimeUnit.MILLISECONDS), false, false)));
            }
            icon = new Icon(InventoryUtils.createPlayerSkull(this.reviving.getName(), lore, this.reviving), Collections.emptyList());
        } else {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getLoadingDisplay().getItem(), Collections.emptyList());
        }
        this.setIcon(13, icon, update);
    }

    public void updateConfirmation(boolean update) {
        Icon icon;
        if (this.revivingData != null) {
            icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getConfirmationDisplay().getItem(), this.placeholders), Collections.singletonList(new ClickActionConfirmRevive(this.reviverData, this.revivingData)));
        } else {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getLoadingDisplay().getItem(), Collections.emptyList());
        }
        this.setIcon(42, icon, update);
    }
}
