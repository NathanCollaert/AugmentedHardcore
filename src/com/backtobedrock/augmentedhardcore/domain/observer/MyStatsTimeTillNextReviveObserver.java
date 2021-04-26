package com.backtobedrock.augmentedhardcore.domain.observer;

import com.backtobedrock.augmentedhardcore.guis.GuiMyStats;

public class MyStatsTimeTillNextReviveObserver implements IObserver {
    private final GuiMyStats gui;

    public MyStatsTimeTillNextReviveObserver(GuiMyStats gui) {
        this.gui = gui;
    }

    @Override
    public void update() {
        this.gui.updateRevive(true);
    }
}
