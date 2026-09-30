package me.croabeast.inventory.nms;

import me.croabeast.inventory.adventure.TextHolder;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * An inventory whose menu needs a version-specific implementation.
 *
 * @since 0.1.0
 */
public interface CustomInventory {

    /**
     * Creates the inventory. Inventories that cannot show a title, like beacons, ignore it.
     *
     * @param title the title of the inventory
     * @return the inventory
     * @since 0.1.0
     */
    @NotNull
    Inventory createInventory(@NotNull TextHolder title);
}
