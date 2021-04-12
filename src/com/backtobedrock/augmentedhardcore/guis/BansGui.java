package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.GuiSortType;
import com.backtobedrock.augmentedhardcore.utils.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BansGui extends AbstractPaginatedGui {
    private final PlayerData playerData;

    public BansGui(PlayerData playerData) {
        super(new CustomHolder(playerData.getBanCount(), String.format("%s Death Bans", playerData.getPlayer().getName())), playerData.getBanCount());
        this.playerData = playerData;
        this.initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        this.setPlayerHead(false);
        this.setData();
    }

    @Override
    protected void setData() {
        super.setData();

        Map<String, String> placeholders = new HashMap<>();
        List<Icon> icons = new ArrayList<>();
        List<Map.Entry<Integer, Ban>> currentData = new ArrayList<>(this.playerData.getBans().entrySet()).subList((this.currentPage - 1) * 28, Math.min(this.currentPage * 28, this.playerData.getBanCount()));

        //loading icons
        for (int i = 0; i < currentData.size(); i++) {
            icons.add(new Icon(this.plugin.getConfigurations().getGuisConfiguration().getLoadingDisplay().getItem(), Collections.emptyList()));
            if (icons.size() == 7) {
                this.customHolder.addRow(icons);
                icons.clear();
            }
        }
        if (!icons.isEmpty()) {
            this.customHolder.addRow(icons);
        }
        this.customHolder.setCurrentRow(1);
        icons.clear();

        //load data async
        CompletableFuture.runAsync(() -> {
            currentData.forEach(e -> {
                placeholders.put("ban_number", e.getKey().toString());
                placeholders.putAll(e.getValue().getPlaceholdersReplacements());
                icons.add(new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getBanDisplay().getItem(), placeholders), Collections.emptyList()));
                if (icons.size() == 7) {
                    this.customHolder.addRow(icons);
                    icons.clear();
                }
            });

            if (!icons.isEmpty()) {
                this.customHolder.addRow(icons);
            }

            this.customHolder.updateInvent();
        });
    }

    private void setPlayerHead(boolean update) {
        this.setIcon(4, new Icon(InventoryUtils.createPlayerSkull(this.playerData.getPlayer().getName(), Collections.emptyList(), this.playerData.getPlayer()), Collections.emptyList()), update);
    }
}
