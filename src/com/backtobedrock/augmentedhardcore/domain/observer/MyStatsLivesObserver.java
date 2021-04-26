package com.backtobedrock.augmentedhardcore.domain.observer;

import com.backtobedrock.augmentedhardcore.guis.GuiMyStats;

public class MyStatsLivesObserver implements IObserver {
    private final GuiMyStats gui;

    public MyStatsLivesObserver(GuiMyStats gui) {
        this.gui = gui;
    }

    @Override
    public void update() {
        this.gui.updateLivesAndLifeParts(true);
    }
}
