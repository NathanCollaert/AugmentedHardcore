package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionOpenBansGui;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionOpenPlayerSelectionAnvilGui;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GuiMyStats extends AbstractGui {
    private final PlayerData playerData;
    private final Player sender;
    private final boolean isOther;

    public GuiMyStats(Player sender, PlayerData playerData) {
        super(new CustomHolder(54, String.format("%s Information", playerData.getPlayer().getName())));
        this.playerData = playerData;
        this.sender = sender;
        this.isOther = !sender.getUniqueId().toString().equals(this.playerData.getPlayer().getUniqueId().toString());
        this.initialize();
    }

    @Override
    public void initialize() {
        this.updateLivesAndLifeParts(false);
        this.updateTimeTillNextMaxHealth(false);
        this.updateRevive(false);
        this.updateTimeTillNextLifePart(false);
        this.updateBansInformation(false);
        this.setData();
    }

    @Override
    protected void setData() {
        this.setAccentColor(Arrays.asList(3, 4, 5, 11, 12, 14, 15, 20, 24, 30, 32, 37, 40, 43, 49));
        this.fillGui(Arrays.asList(13, 21, 22, 23, 31));
    }

    public void updateLivesAndLifeParts(boolean update) {
        Icon icon;

        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLives() && !this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts()) {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getNotAvailableDisplay().getItem(), Collections.emptyList());
        } else {
            Map<String, String> placeholders = new HashMap<>();
            if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLives()) {
                placeholders.put("lives_number", Integer.toString(this.playerData.getLives()));
                placeholders.put("lives", String.format("%d %s", this.playerData.getLives(), this.playerData.getLives() == 1 ? "life" : "lives"));
            }
            if (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts()) {
                placeholders.put("life_parts_number", Integer.toString(this.playerData.getLifeParts()));
                placeholders.put("life_parts", String.format("%d %s", this.playerData.getLifeParts(), this.playerData.getLifeParts() == 1 ? "life part" : "life parts"));
            }
            icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getLivesAndLifePartsDisplay().getItem(), placeholders), Collections.emptyList());
        }
        this.setIcon(13, icon, update);
    }

    public void updateTimeTillNextMaxHealth(boolean update) {
        Icon icon;

        if (!this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getNotAvailableDisplay().getItem(), Collections.emptyList());
        } else {
            Map<String, String> placeholders = new HashMap<String, String>() {{
                put("max_health", Double.toString(plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealth()));
                put("min_health", Double.toString(plugin.getConfigurations().getMaxHealthConfiguration().getMinHealth()));
                put("current_max_health", playerData.getPlayer().getPlayer() == null ? "-" : Double.toString(playerData.getPlayer().getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
                put("time_till_next_max_health_long", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextMaxHealth(), TimePattern.LONG));
                put("time_till_next_max_health_short", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextMaxHealth(), TimePattern.SHORT));
                put("time_till_next_max_health_digital", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextMaxHealth(), TimePattern.DIGITAL));
            }};

            icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getMaxHealthDisplay().getItem(), placeholders), Collections.emptyList());
        }
        this.setIcon(21, icon, update);
    }

    public void updateRevive(boolean update) {
        Icon icon;

        if (!this.plugin.getConfigurations().getReviveConfiguration().isUseRevive() || this.isOther) {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getNotAvailableDisplay().getItem(), Collections.emptyList());
        } else if (this.playerData.getTimeTillNextRevive() > 0) {
            Map<String, String> placeholders = new HashMap<String, String>() {{
                put("time_till_next_revive_long", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextRevive(), TimePattern.LONG));
                put("time_till_next_revive_short", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextRevive(), TimePattern.SHORT));
                put("time_till_next_revive_digital", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextRevive(), TimePattern.DIGITAL));
            }};
            icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getReviveOnCooldownDisplay().getItem(), placeholders), Collections.emptyList());
        } else {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getReviveDisplay().getItem(), Collections.singletonList(new ClickActionOpenPlayerSelectionAnvilGui()));
        }

        this.setIcon(22, icon, update);
    }

    public void updateTimeTillNextLifePart(boolean update) {
        Icon icon;

        if (!this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts()) {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getNotAvailableDisplay().getItem(), Collections.emptyList());
        } else {
            Map<String, String> placeholders = new HashMap<String, String>() {{
                put("max_life_parts", Integer.toString(plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLifeParts()));
                put("life_parts_number", Integer.toString(playerData.getLifeParts()));
                put("life_parts", String.format("%d %s", playerData.getLifeParts(), playerData.getLifeParts() == 1 ? "life part" : "life parts"));
                put("time_till_next_life_part_long", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextLifePart(), TimePattern.LONG));
                put("time_till_next_life_part_short", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextLifePart(), TimePattern.SHORT));
                put("time_till_next_life_part_digital", MessageUtils.getTimeFromTicks(playerData.getTimeTillNextLifePart(), TimePattern.DIGITAL));
            }};
            icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getLifePartDisplay().getItem(), placeholders), Collections.emptyList());
        }

        this.setIcon(23, icon, update);
    }

    public void updateBansInformation(boolean update) {
        Icon icon;

        if (!this.plugin.getConfigurations().getDeathBanConfiguration().isUseDeathBan()) {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getNotAvailableDisplay().getItem(), Collections.emptyList());
        } else {
            Map<String, String> placeholders = new HashMap<String, String>() {{
                put("total_death_bans", Integer.toString(playerData.getBanCount()));
            }};
            icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getPreviousBansDisplay().getItem(), placeholders), Collections.singletonList(new ClickActionOpenBansGui(this.playerData)));
        }

        this.setIcon(31, icon, update);
    }

    @Override
    public Inventory getInventory() {
        this.playerData.registerObserver(this.sender, this);
        return super.getInventory();
    }

    public PlayerData getPlayerData() {
        return this.playerData;
    }
}
