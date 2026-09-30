package me.croabeast.inventory.nms.v1_21_1.util;

import lombok.experimental.UtilityClass;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class for custom inventories
 *
 * @since 0.1.0
 */
@UtilityClass
public class CustomInventoryUtil {

    /**
     * Converts an array of Bukkit items into a non-null list of NMS items. The returned list is modifiable. If no items
     * were specified, this returns an empty list.
     *
     * @param items the items to convert
     * @return a list of converted items
     * @since 0.1.0
     */
    @NotNull
    public NonNullList<ItemStack> convertToNMSItems(@Nullable org.bukkit.inventory.ItemStack @NotNull [] items) {
        NonNullList<ItemStack> nmsItems = NonNullList.create();

        for (org.bukkit.inventory.ItemStack item : items) {
            nmsItems.add(CraftItemStack.asNMSCopy(item));
        }

        return nmsItems;
    }
}
