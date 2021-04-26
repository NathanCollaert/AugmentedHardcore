package com.backtobedrock.augmentedhardcore.domain.observer;

import com.backtobedrock.augmentedhardcore.guis.GuiMyStats;

public class MyStatsMaxHealthObserver implements IObserver {
    private final GuiMyStats gui;

    public MyStatsMaxHealthObserver(GuiMyStats gui) {
        this.gui = gui;
    }

    @Override
    public void update() {
        this.gui.updateTimeTillNextMaxHealth(true);
    }
}
