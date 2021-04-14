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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuiRevive extends AbstractGui {
    private final PlayerData reviverData;
    private final OfflinePlayer reviving;
    private final Map<String, String> placeholders;

    public GuiRevive(PlayerData reviverData, OfflinePlayer reviving) throws ExecutionException, InterruptedException {
        super(new CustomHolder(54, String.format("Reviving %s", reviving.getName())));
        this.reviverData = reviverData;
        this.reviving = reviving;
        this.placeholders = new HashMap<String, String>() {{
            put("player", reviving.getName());
        }};
        this.initialize();
    }

    @Override
    protected void initialize() throws ExecutionException, InterruptedException {
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

    public void updatePlayerHead(boolean update) throws ExecutionException, InterruptedException {
        this.setIcon(13, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getLoadingDisplay().getItem(), Collections.emptyList()), false);
        this.plugin.getPlayerRepository().getByPlayer(this.reviving).thenAcceptAsync(playerData -> {
            List<String> lore = new ArrayList<>();
            lore.add(String.format("%s current statistics:", this.reviving.getName()));
            lore.add(String.format("    • Lives left: %d", playerData.getLives()));
            BanEntry banEntry = BanUtils.isBanned(playerData);
            if (banEntry != null) {
                lore.add(String.format("    • Death banned for another: %s", MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(banEntry.getExpiration().getTime() - new Date().getTime(), TimeUnit.MILLISECONDS), false, false)));
            }
            this.setIcon(13, new Icon(InventoryUtils.createPlayerSkull(this.reviving.getName(), lore, this.reviving), Collections.emptyList()), update);
        }).get();
    }

    public void updateConfirmation(boolean update) throws ExecutionException, InterruptedException {
        this.setIcon(42, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getLoadingDisplay().getItem(), Collections.emptyList()), false);
        this.plugin.getPlayerRepository().getByPlayer(this.reviving).thenAcceptAsync(playerData -> this.setIcon(42, new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getConfirmationDisplay().getItem(), this.placeholders), Collections.singletonList(new ClickActionConfirmRevive(this.reviverData, playerData))), update)).get();
    }
}
