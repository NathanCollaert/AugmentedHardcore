package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.domain.enums.GuiSortType;
import com.backtobedrock.augmentedhardcore.guis.clickActions.NextPageClickAction;
import com.backtobedrock.augmentedhardcore.guis.clickActions.PreviousPageClickAction;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class AbstractPaginatedGui extends AbstractGui {
    protected int currentPage = 1;
    protected int totalPages;

    public AbstractPaginatedGui(CustomHolder customHolder, int dataCount) {
        super(customHolder);
        this.totalPages = dataCount / 28;
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
            this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 3, new Icon(this.previousPageDisplayItem(), Collections.singletonList(new PreviousPageClickAction(this))));
        }

        //Current page
        this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 4, new Icon(this.pageInformationDisplayItem(), Collections.emptyList()));

        //Next page button
        if (this.totalPages > 1 && this.currentPage < this.totalPages) {
            this.customHolder.setIcon((this.customHolder.getRowAmount() - 1) * 9 + 5, new Icon(this.nextPageDisplayItem(), Collections.singletonList(new NextPageClickAction(this))));
        }
    }

    protected void setData(List<Icon> data, GuiSortType sortType) {
        List<Icon> icons = new ArrayList<>();
        new ArrayList<>(data).subList((this.currentPage - 1) * 28, Math.min(this.currentPage * 28, data.size())).forEach(e -> {
            switch (sortType) {
                case CENTERED:
                    icons.add(e);
                    if (icons.size() == 7) {
                        this.customHolder.addRow(icons);
                        icons.clear();
                    }
                    break;
                case LEFTTORIGHT:
                    this.customHolder.addIcon(e);
                    break;
            }
        });

        if (!icons.isEmpty()) {
            this.customHolder.addRow(icons);
        }
    }

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
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("current_page", Integer.toString(currentPage));
            put("total_pages", Integer.toString(totalPages));
            put("next_page", Integer.toString(currentPage++));
        }};
        return MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getNextPageDisplay().getItem(), placeholders);
    }

    private ItemStack previousPageDisplayItem() {
        Map<String, String> placeholders = new HashMap<String, String>() {{
            put("current_page", Integer.toString(currentPage));
            put("total_pages", Integer.toString(totalPages));
            put("previous_page", Integer.toString(currentPage--));
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
