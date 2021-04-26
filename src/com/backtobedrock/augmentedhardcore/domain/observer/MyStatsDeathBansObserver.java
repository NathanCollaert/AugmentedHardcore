package com.backtobedrock.augmentedhardcore.domain.observer;

import com.backtobedrock.augmentedhardcore.guis.GuiMyStats;

public class MyStatsDeathBansObserver implements IObserver {
    private final GuiMyStats gui;

    public MyStatsDeathBansObserver(GuiMyStats gui) {
        this.gui = gui;
    }

    @Override
    public void update() {
        this.gui.updateBansInformation(true);
    }
}
