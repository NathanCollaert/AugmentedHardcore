package com.backtobedrock.augmentedhardcore.domain.observer;

import com.backtobedrock.augmentedhardcore.guis.GuiMyStats;

public class MyStatsLifePartsObserver implements IObserver{
    private final GuiMyStats gui;

    public MyStatsLifePartsObserver(GuiMyStats gui) {
        this.gui = gui;
    }

    @Override
    public void update() {
        this.gui.updateLivesAndLifeParts(true);
        this.gui.updateTimeTillNextLifePart(true);
    }
}
