package me.croabeast.inventory.gui.type.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface InventoryBased extends InventoryHolder {

    /**
     * Creates a new inventory of the type of the implementing class.
     *
     * @return the new inventory
     * @since 0.1.0
     */
    @NotNull
    Inventory createInventory();

}
