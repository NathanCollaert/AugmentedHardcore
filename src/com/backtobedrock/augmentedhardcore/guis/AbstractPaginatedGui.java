package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionNextPage;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionPreviousPage;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPaginatedGui extends AbstractGui {
    protected int currentPage = 1;
    protected int totalPages;

    public AbstractPaginatedGui(CustomHolder customHolder, int dataCount) {
        super(customHolder);
        this.totalPages = (int) Math.ceil((double) dataCount / 28);
    }

    @Override
    public void initialize() {
        //clear content for new page
        this.customHolder.reset();
        super.initialize();
    }

    @Override
    protected void setData() {
        //Previous page button
        if (this.totalPages > 1 && this.currentPage > 1) {
            this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 3, new Icon(this.previousPageDisplayItem(), Collections.singletonList(new ClickActionPreviousPage(this))));
        }

        //Current page
        this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 4, new Icon(this.pageInformationDisplayItem(), Collections.emptyList()));

        //Next page button
        if (this.totalPages > 1 && this.currentPage < this.totalPages) {
            this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 5, new Icon(this.nextPageDisplayItem(), Collections.singletonList(new ClickActionNextPage(this))));
        }
    }

    public void nextPage() {
        if (this.currentPage < this.totalPages) {
            this.currentPage++;
            this.initialize();
            this.customHolder.updateInvent();
        }
    }

    public void previousPage() {
        if (this.currentPage > 1) {
            this.currentPage--;
            this.initialize();
            this.customHolder.updateInvent();
        }
    }

    private ItemStack nextPageDisplayItem() {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("current_page", Integer.toString(currentPage));
            put("total_pages", Integer.toString(totalPages));
            put("next_page", Integer.toString(currentPage + 1));
        }};
        return MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getNextPageDisplay().getItem(), placeholders);
    }

    private ItemStack previousPageDisplayItem() {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("current_page", Integer.toString(currentPage));
            put("total_pages", Integer.toString(totalPages));
            put("previous_page", Integer.toString(currentPage - 1));
        }};
        return MessageUtils.replaceItemLorePlacholders(this.plugin.getConfigurations().getGuisConfiguration().getPreviousPageDisplay().getItem(), placeholders);
    }

    private ItemStack pageInformationDisplayItem() {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("current_page", Integer.toString(currentPage));
            put("total_pages", Integer.toString(totalPages));
        }};
        return MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getPageInformationDisplay().getItem(), placeholders);
    }
}
