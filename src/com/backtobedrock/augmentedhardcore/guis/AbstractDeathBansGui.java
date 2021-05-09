package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import org.bukkit.OfflinePlayer;
import org.javatuples.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractDeathBansGui extends AbstractPaginatedGui {
    protected final Map<Pair<OfflinePlayer, Integer>, Ban> bans = new HashMap<>();

    public AbstractDeathBansGui(String title, int dataSize) {
        super(new CustomHolder(dataSize, title), dataSize);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.setDataIcon(false);
        this.setData();
    }

    public abstract void setDataIcon(boolean update);

    public void setData() {
        super.setData();

        List<Icon> icons = new ArrayList<>();
        List<Map.Entry<Pair<OfflinePlayer, Integer>, Ban>> currentData = new ArrayList<>(this.bans.entrySet()).subList((this.currentPage - 1) * 28, Math.min(this.currentPage * 28, this.bans.size()));

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
        Map<String, String> placeholders = new HashMap<>();
        CompletableFuture.runAsync(() -> {
            currentData.forEach(e -> {
                placeholders.put("ban_number", e.getKey().getValue1().toString());
                placeholders.putAll(e.getValue().getPlaceholdersReplacements());
                icons.add(this.getIcon(e.getKey().getValue0(), placeholders));
                if (icons.size() == 7) {
                    this.customHolder.addRow(icons);
                    icons.clear();
                }
            });

            if (!icons.isEmpty()) {
                this.customHolder.addRow(icons);
            }

            this.customHolder.updateInvent();
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public abstract Icon getIcon(OfflinePlayer player, Map<String, String> placeholders);
}
