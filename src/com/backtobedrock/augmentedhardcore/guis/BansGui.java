package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.GuiSortType;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BansGui extends AbstractPaginatedGui {
    private final PlayerData playerData;

    public BansGui(OfflinePlayer player, PlayerData playerData) {
        super(new CustomHolder(playerData.getBanCount(), true, String.format("%s Death Bans", player.getName())), playerData.getBanCount());
        this.playerData = playerData;
    }

    @Override
    protected void setData() {
        Map<String, String> placeholders = new HashMap<>();
        List<Icon> icons = this.playerData.getBans().entrySet().stream().map(e -> {

            return new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getBanDisplay().getItem(), placeholders), Collections.emptyList());
        }).collect(Collectors.toList());
        super.setData(icons, GuiSortType.CENTERED);
    }
}
