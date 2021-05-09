package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionConfirmRevive;
import com.backtobedrock.augmentedhardcore.utils.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GuiRevive extends AbstractConfirmationGui {
    private final PlayerData reviverData;
    private final OfflinePlayer reviving;
    private PlayerData revivingData;

    public GuiRevive(PlayerData reviverData, OfflinePlayer reviving) {
        super(String.format("Reviving %s", reviving.getName()));
        this.reviverData = reviverData;
        this.reviving = reviving;
        this.plugin.getPlayerRepository().getByPlayer(this.reviving).thenAcceptAsync(playerData -> {
            this.revivingData = playerData;
            this.updateInfo(true);
            this.updateConfirmation(true);
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
        this.initialize();
    }

    @Override
    protected void initialize() {
        this.updateInfo(false);
        this.updateConfirmation(false);
        super.initialize();
    }

    public void updateInfo(boolean update) {
        Icon icon;
        if (this.revivingData != null) {
            Map<String, String> placeholders = new HashMap<String, String>() {{
                put("player", revivingData.getPlayer().getName());
                put("lives_number", Integer.toString(revivingData.getLives()));
            }};
            icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(InventoryUtils.createPlayerSkull(this.plugin.getConfigurations().getGuisConfiguration().getRevivingDisplay().getName(), this.plugin.getConfigurations().getGuisConfiguration().getRevivingDisplay().getLore(), this.reviving), placeholders), Collections.emptyList());
        } else {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getLoadingDisplay().getItem(), Collections.emptyList());
        }
        super.updateInfo(icon, update);
    }

    public void updateConfirmation(boolean update) {
        super.updateConfirmation(Collections.singletonList(new ClickActionConfirmRevive(this.reviverData, this.revivingData)), update);
    }
}
