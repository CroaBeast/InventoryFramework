package me.croabeast.inventory.util;

import me.croabeast.inventory.inventoryview.abstraction.AbstractInventoryViewUtil;
import me.croabeast.inventory.util.version.Version;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class for working with {@link InventoryView}s across different definitions.
 *
 * @since 0.10.16
 */
public class InventoryViewUtil {

    /**
     * The underlying implementation.
     */
    @Nullable
    private static AbstractInventoryViewUtil IMPLEMENTATION;

    /**
     * Gets the instance of this class to use for the current version.
     *
     * @return an instance of a utility class
     * @since 0.10.16
     */
    @NotNull
    @Contract(pure = true)
    public static AbstractInventoryViewUtil getInstance() {
        if (IMPLEMENTATION == null) {
            if (Version.getVersion().isInventoryViewInterface()) {
                IMPLEMENTATION = me.croabeast.inventory.inventoryview.interface_.InventoryViewUtil.getInstance();
            } else {
                IMPLEMENTATION = me.croabeast.inventory.inventoryview.abstractclass.InventoryViewUtil.getInstance();
            }
        }

        return IMPLEMENTATION;
    }
}
