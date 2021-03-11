package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.guis.CustomHolder;
import com.backtobedrock.LiteDeathBan.guis.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CustomInventoryClickListener extends AbstractEventListener {
    @EventHandler
    public void onCustomInventoryClick(InventoryClickEvent event) {
        //Check if the inventory is custom
        if (!(event.getView().getTopInventory().getHolder() instanceof CustomHolder))
            return;

        //Cancel the event
        event.setCancelled(true);

        //Check if who clicked is a Player
        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();

        //Check if the item the player clicked on is valid
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return;

        //Get our CustomHolder
        CustomHolder customHolder = (CustomHolder) event.getView().getTopInventory().getHolder();

        //Check if the clicked slot is any icon
        Icon icon = customHolder.getIcon(event.getRawSlot());
        if (icon == null)
            return;

        //Execute all the actions
        icon.getClickActions().forEach((clickAction) -> {
            clickAction.execute(player);
        });
    }
}
