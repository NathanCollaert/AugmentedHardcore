package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.guis.clickActions.AbstractClickAction;
import com.backtobedrock.augmentedhardcore.guis.clickActions.NextPageClickAction;
import com.backtobedrock.augmentedhardcore.guis.clickActions.PreviousPageClickAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public abstract class AbstractPaginatedGui extends AbstractGui {
    protected int currentPage = 1;
    protected int totalPages;

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
            this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 3, new Icon(this.previousPageDisplayItem(), Arrays.asList(new AbstractClickAction[]{new PreviousPageClickAction(this)})));
        }

        //Current page
        this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 4, new Icon(this.pageInformationDisplayItem(), Collections.emptyList()));

        //Next page button
        if (this.totalPages > 1 && this.currentPage < this.totalPages) {
            this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 5, new Icon(this.nextPageDisplayItem(), Arrays.asList(new AbstractClickAction[]{new NextPageClickAction(this)})));
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

    private ItemStack nextPageDisplayItem() {
        ItemStack item = this.plugin.getConfigurations().getGuisConfiguration().getNextPageDisplay().getItem();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.currentPagePlaceholderReplacement(this.totalPagesPlaceholderReplacement(this.nextPagePlaceholderReplacement(itemMeta.getDisplayName()))));
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    private ItemStack previousPageDisplayItem() {
        ItemStack item = this.plugin.getConfigurations().getGuisConfiguration().getPreviousPageDisplay().getItem();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.currentPagePlaceholderReplacement(this.totalPagesPlaceholderReplacement(this.previousPagePlaceholderReplacement(itemMeta.getDisplayName()))));
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    private ItemStack pageInformationDisplayItem() {
        ItemStack item = this.plugin.getConfigurations().getGuisConfiguration().getPageInformationDisplay().getItem();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(this.currentPagePlaceholderReplacement(this.totalPagesPlaceholderReplacement(itemMeta.getDisplayName())));
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    private String currentPagePlaceholderReplacement(String text) {
        String replacedText = text;
        if (replacedText.contains("%current_page%")) {
            replacedText = replacedText.replaceAll("%current_page%", Integer.toString(this.currentPage));
        }
        return replacedText;
    }

    private String totalPagesPlaceholderReplacement(String text) {
        String replacedText = text;
        if (replacedText.contains("%total_pages%")) {
            replacedText = replacedText.replaceAll("%total_pages%", Integer.toString(this.totalPages));
        }
        return replacedText;
    }

    private String nextPagePlaceholderReplacement(String text) {
        String replacedText = text;
        if (replacedText.contains("%next_page%")) {
            replacedText = replacedText.replaceAll("%next_page%", Integer.toString(this.currentPage++));
        }
        return replacedText;
    }

    private String previousPagePlaceholderReplacement(String text) {
        String replacedText = text;
        if (replacedText.contains("%previous_page%")) {
            replacedText = replacedText.replaceAll("%previous_page%", Integer.toString(this.currentPage--));
        }
        return replacedText;
    }
}
