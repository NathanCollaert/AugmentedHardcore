package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.guis.clickActions.OpenBansGuiClickAction;
import com.backtobedrock.augmentedhardcore.guis.clickActions.OpenPlayerSelectionAnvilGuiClickAction;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PlayerInfoGui extends AbstractGui {
    private final OfflinePlayer player;
    private final PlayerData playerData;

    public PlayerInfoGui(OfflinePlayer player, PlayerData playerData) {
        super(new CustomHolder(54, true, String.format("%s Information", player.getName())));
        this.player = player;
        this.playerData = playerData;
        this.setData();
    }

    @Override
    protected void setData() {
        this.updateLivesAndLifeParts(false);
        this.updateTimeTillNextMaxHealth(false);
        this.updateRevive(false);
        this.updateTimeTillNextLifePart(false);
        this.updateBansInformation(false);
        this.setAccentColor(Arrays.asList(3, 4, 5, 11, 12, 14, 15, 20, 24, 30, 32, 37, 40, 43, 49));
        this.fillGui(Arrays.asList(13, 21, 22, 23, 31));
    }

    private void updateLivesAndLifeParts(boolean update) {
        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLives() && !this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts()) {
            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLives()) {
            placeholders.put("lives_number", Integer.toString(playerData.getLives()));
            placeholders.put("lives", String.format("%d %s", playerData.getLives(), playerData.getLives() == 1 ? "life" : "lives"));
        }
        if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts()) {
            placeholders.put("life_parts_number", Integer.toString(playerData.getLifeParts()));
            placeholders.put("life_parts", String.format("%d %s", playerData.getLifeParts(), playerData.getLifeParts() == 1 ? "life part" : "life parts"));
        }
        Icon icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getLivesAndLifePartsDisplay().getItem(), placeholders), Collections.emptyList());
        this.setIcon(13, icon, update);
    }

    private void updateTimeTillNextMaxHealth(boolean update) {
        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth() || !this.plugin.getConfigurations().getMaxHealthConfiguration().isGetMaxHealthByPlaytime()) {
            return;
        }

        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("max_health", Double.toString(plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealth()));
            put("min_health", Double.toString(plugin.getConfigurations().getMaxHealthConfiguration().getMinHealth()));
            put("current_max_health", player.getPlayer() == null ? "unknown" : Double.toString(player.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
            put("time_till_next_max_health_long", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextMaxHealth(), false, true));
            put("time_till_next_max_health_short", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextMaxHealth(), false, false));
            put("time_till_next_max_health_digital", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextMaxHealth(), true, false));
        }};
        Icon icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getMaxHealthDisplay().getItem(), placeholders), Collections.emptyList());
        this.setIcon(21, icon, update);
    }

    private void updateRevive(boolean update) {
        if (!this.plugin.getConfigurations().getReviveConfiguration().isUseRevive()) {
            return;
        }

        Icon icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getReviveDisplay().getItem(), Collections.singletonList(new OpenPlayerSelectionAnvilGuiClickAction()));
        this.setIcon(22, icon, update);
    }

    private void updateTimeTillNextLifePart(boolean update) {
        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts() || !this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isGetLifePartsByPlaytime()) {
            return;
        }

        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("max_life_parts", Integer.toString(plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLifeParts()));
            put("life_parts_number", Integer.toString(playerData.getLifeParts()));
            put("life_parts", String.format("%d %s", playerData.getLifeParts(), playerData.getLifeParts() == 1 ? "life part" : "life parts"));
            put("time_till_next_life_part_long", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextLifePart(), false, true));
            put("time_till_next_life_part_short", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextLifePart(), false, false));
            put("time_till_next_life_part_digital", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextLifePart(), true, false));
        }};
        Icon icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getLifePartDisplay().getItem(), placeholders), Collections.emptyList());
        this.setIcon(23, icon, update);
    }

    private void updateBansInformation(boolean update) {
        if (!this.plugin.getConfigurations().getDeathBanConfiguration().isUseDeathBan()) {
            return;
        }

        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("total_bans", Integer.toString(playerData.getBanCount()));
        }};
        Icon icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getPreviousBansDisplay().getItem(), placeholders), Collections.singletonList(new OpenBansGuiClickAction()));
        this.setIcon(31, icon, update);
    }
}
