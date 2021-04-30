package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.guis.clickActions.AbstractClickAction;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionCloseInventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractConfirmationGui extends AbstractGui {

    public AbstractConfirmationGui(String title) {
        super(new CustomHolder(54, title));
    }

    @Override
    protected void initialize() {
        this.updateCancellation(false);
        this.setData();
    }

    @Override
    public void setData() {
        this.setAccentColor(Arrays.asList(3, 4, 5, 10, 11, 12, 14, 15, 16, 21, 22, 23));
        this.fillGui(Arrays.asList(37, 39, 41, 43));
    }

    public void updateCancellation(boolean update) {
        this.setIcon(38, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getCancellationDisplay().getItem(), Collections.singletonList(new ClickActionCloseInventory())), update);
    }

    public void updateInfo(Icon icon, boolean update) {
        this.setIcon(13, icon, update);
    }

    public void updateConfirmation(List<AbstractClickAction> actions, boolean update) {
        this.setIcon(42, new Icon(this.plugin.getConfigurations().getGuisConfiguration().getConfirmationDisplay().getItem(), actions), update);
    }
}
