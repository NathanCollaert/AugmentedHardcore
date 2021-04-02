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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReviveGui extends AbstractGui {
    private final Player reviver;
    private final PlayerData reviverData;
    private final OfflinePlayer reviving;
    private final PlayerData revivingData;
    private final ServerData serverData;

    public ReviveGui(Player reviver, PlayerData reviverData, OfflinePlayer reviving, PlayerData revivingData, ServerData serverData) {
        super(new CustomHolder(54, true, String.format("Reviving %s", reviving.getName())));
        this.reviver = reviver;
        this.reviverData = reviverData;
        this.reviving = reviving;
        this.revivingData = revivingData;
        this.serverData = serverData;
        this.setData();
    }

    @Override
    public void setData() {
        this.setAccentBorder();
        this.updateCancellation(false);
        this.updatePlayerHead(false);
        this.updateConfirmation(false);
        this.fillGui(Arrays.asList(37, 39, 41, 43));
    }

    private void setAccentBorder() {
        Arrays.asList(3, 4, 5, 10, 11, 12, 14, 15, 16, 21, 22, 23).forEach(e -> this.customHolder.setIcon(e, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getAccentDisplay().getItem(), Collections.emptyList())));
    }

    public void updateCancellation(boolean update) {
        this.customHolder.setIcon(38, new Icon(this.getDisplayItem(this.plugin.getConfigurations().getGuisConfiguration().getCancellationDisplay().getItem()), Collections.singletonList(new CloseInventoryClickAction())));
        if (update) {
            this.customHolder.updateIcon(38);
        }
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
        this.customHolder.setIcon(13, new Icon(InventoryUtils.createPlayerSkull(this.reviving.getName(), lore, this.reviving), Collections.emptyList()));
        if (update) {
            this.customHolder.updateIcon(13);
        }
    }

    public void updateConfirmation(boolean update) {
        this.customHolder.setIcon(42, new Icon(this.getDisplayItem(this.plugin.getConfigurations().getGuisConfiguration().getConfirmationDisplay().getItem()), Collections.singletonList(new ConfirmReviveClickAction(this.reviver, this.reviverData, this.reviving, this.revivingData))));
        if (update) {
            this.customHolder.updateIcon(42);
        }
    }

    private ItemStack getDisplayItem(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.playerPlaceholderReplacement(itemMeta.getDisplayName()));
            itemMeta.setLore(itemMeta.getLore().stream().map(this::playerPlaceholderReplacement).collect(Collectors.toList()));
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    private String playerPlaceholderReplacement(String text) {
        String replacedText = text;
        if (replacedText.contains("%player%")) {
            replacedText = replacedText.replaceAll("%player%", this.reviving.getName());
        }
        return replacedText;
    }
}
