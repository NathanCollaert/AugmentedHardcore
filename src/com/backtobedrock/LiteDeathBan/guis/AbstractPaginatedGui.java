package com.backtobedrock.LiteDeathBan.guis;

import com.backtobedrock.LiteDeathBan.guis.clickActions.AbstractClickAction;
import com.backtobedrock.LiteDeathBan.guis.clickActions.NextPageClickAction;
import com.backtobedrock.LiteDeathBan.guis.clickActions.PreviousPageClickAction;
import com.backtobedrock.LiteDeathBan.utils.InventoryUtils;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;

public abstract class AbstractPaginatedGui extends AbstractGui {
    protected int currentPage = 1;
    protected int totalPages = 1;

    public AbstractPaginatedGui(CustomHolder customHolder, int totalPages) {
        super(customHolder);
        this.totalPages = totalPages;
    }

    @Override
    public void initialize() {
        //clear content for new page
        this.customHolder.clearContent();

        super.initialize();
        this.setPageControls();
    }

    protected void setPageControls() {
        //Previous page button
        if (this.totalPages > 1 && this.currentPage > 1) {
            //TODO: get from config
            this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 3, new Icon(InventoryUtils.createItem(Material.STONE_BUTTON, "Previous Page", Collections.emptyList(), 1, false), Arrays.asList(new AbstractClickAction[]{new PreviousPageClickAction(this)})));
        }

        //Current page
        //TODO: get from config
        this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 4, new Icon(InventoryUtils.createItem(Material.PAPER, "Page " + this.currentPage + "/" + this.totalPages, Collections.emptyList(), 1, false), Collections.emptyList()));

        //Next page button
        if (this.totalPages > 1 && this.currentPage < this.totalPages) {
            //TODO: get from config
            this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 5, new Icon(InventoryUtils.createItem(Material.STONE_BUTTON, "Next Page", Collections.emptyList(), 1, false), Arrays.asList(new AbstractClickAction[]{new NextPageClickAction(this)})));
        }
    }

    protected abstract void setData();

    public void nextPage() {
        if (this.currentPage < this.totalPages) {
            this.currentPage++;
        }
    }

    public void previousPage() {
        if (this.currentPage > 1) {
            this.currentPage--;
        }
    }
}
