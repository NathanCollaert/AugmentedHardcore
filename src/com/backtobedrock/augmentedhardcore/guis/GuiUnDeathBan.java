package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionConfirmUnDeathBan;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class GuiUnDeathBan extends AbstractConfirmationGui {
    private final OfflinePlayer target;
    private final ItemStack item;

    public GuiUnDeathBan(OfflinePlayer target, ItemStack item) {
        super(String.format("Undeathbanning %s", target.getName()));
        this.target = target;
        this.item = item;
        this.initialize();
    }

    @Override
    protected void initialize() {
        this.updateInfo(false);
        this.updateConfirmation(false);
        super.initialize();
    }

    public void updateInfo(boolean update) {
        super.updateInfo(new Icon(this.item, Collections.emptyList()), update);
    }

    public void updateConfirmation(boolean update) {
        super.updateConfirmation(Collections.singletonList(new ClickActionConfirmUnDeathBan(this.target)), update);
    }
}
