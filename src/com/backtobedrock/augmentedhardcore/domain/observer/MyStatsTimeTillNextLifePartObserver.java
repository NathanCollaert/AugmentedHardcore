package com.backtobedrock.augmentedhardcore.domain.observer;

import com.backtobedrock.augmentedhardcore.guis.GuiMyStats;

public class MyStatsTimeTillNextLifePartObserver implements IObserver {
    private final GuiMyStats gui;

    public MyStatsTimeTillNextLifePartObserver(GuiMyStats gui) {
        this.gui = gui;
    }

    @Override
    public void update() {
        this.gui.updateTimeTillNextLifePart(true);
    }
}
