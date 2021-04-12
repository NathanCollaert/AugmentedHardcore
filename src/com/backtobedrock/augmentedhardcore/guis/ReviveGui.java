package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.guis.clickActions.CloseInventoryClickAction;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ConfirmReviveClickAction;
import com.backtobedrock.augmentedhardcore.utils.BanUtils;
import com.backtobedrock.augmentedhardcore.utils.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.BanEntry;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReviveGui extends AbstractGui {
    private final Player reviver;
    private final OfflinePlayer reviving;
    private final Map<String, String> placeholders;

    public ReviveGui(Player reviver, OfflinePlayer reviving) {
        super(new CustomHolder(54, String.format("Reviving %s", reviving.getName())));
        this.reviver = reviver;
        this.reviving = reviving;
        this.placeholders = new HashMap<String, String>() {{
            put("player", reviving.getName());
        }};
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
        this.setIcon(38, new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getCancellationDisplay().getItem(), this.placeholders), Collections.singletonList(new CloseInventoryClickAction())), update);
    }

    public void updatePlayerHead(boolean update) {
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
        });
    }

    public void updateConfirmation(boolean update) {
        this.setIcon(42, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getLoadingDisplay().getItem(), Collections.emptyList()), false);
        this.plugin.getPlayerRepository().getByPlayer(this.reviving).thenAcceptAsync(playerData -> this.setIcon(42, new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getConfirmationDisplay().getItem(), this.placeholders), Collections.singletonList(new ConfirmReviveClickAction(this.reviver, playerData))), update));
    }
}
