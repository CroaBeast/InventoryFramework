package me.croabeast.inventory.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Calls {@link InventoryView} methods on every version. InventoryView is an abstract class before 1.21 and an
 * interface since, so a direct call compiled against one breaks on the other; method handles resolve on both.
 *
 * @since 0.1.0
 */
@UtilityClass
public class InventoryViewUtil {

    private final MethodHandle TOP_INVENTORY = find("getTopInventory", Inventory.class);
    private final MethodHandle BOTTOM_INVENTORY = find("getBottomInventory", Inventory.class);
    private final MethodHandle GET_CURSOR = find("getCursor", ItemStack.class);
    private final MethodHandle SET_CURSOR = find("setCursor", void.class, ItemStack.class);
    private final MethodHandle INVENTORY = find("getInventory", Inventory.class, int.class);
    private final MethodHandle SLOT_TYPE = find("getSlotType", InventoryType.SlotType.class, int.class);
    private final MethodHandle TITLE = find("getTitle", String.class);

    /**
     * Gets the top inventory of the view.
     *
     * @param view the view
     * @return the top inventory
     * @since 0.1.0
     */
    @NotNull
    @SneakyThrows
    public Inventory getTopInventory(@NotNull InventoryView view) {
        return (Inventory) TOP_INVENTORY.invokeExact(view);
    }

    /**
     * Gets the bottom inventory of the view.
     *
     * @param view the view
     * @return the bottom inventory
     * @since 0.1.0
     */
    @NotNull
    @SneakyThrows
    public Inventory getBottomInventory(@NotNull InventoryView view) {
        return (Inventory) BOTTOM_INVENTORY.invokeExact(view);
    }

    /**
     * Gets the item on the cursor of the view.
     *
     * @param view the view
     * @return the cursor item
     * @since 0.1.0
     */
    @Nullable
    @SneakyThrows
    public ItemStack getCursor(@NotNull InventoryView view) {
        return (ItemStack) GET_CURSOR.invokeExact(view);
    }

    /**
     * Sets the item on the cursor of the view.
     *
     * @param view the view
     * @param item the new cursor item
     * @since 0.1.0
     */
    @SneakyThrows
    public void setCursor(@NotNull InventoryView view, @Nullable ItemStack item) {
        SET_CURSOR.invokeExact(view, item);
    }

    /**
     * Gets the inventory of the view that contains the raw slot.
     *
     * @param view the view
     * @param slot the raw slot
     * @return the inventory, or null if the slot is outside the view
     * @since 0.1.0
     */
    @Nullable
    @SneakyThrows
    public Inventory getInventory(@NotNull InventoryView view, int slot) {
        return (Inventory) INVENTORY.invokeExact(view, slot);
    }

    /**
     * Gets the type of the raw slot in the view.
     *
     * @param view the view
     * @param slot the raw slot
     * @return the slot type
     * @since 0.1.0
     */
    @NotNull
    @SneakyThrows
    public InventoryType.SlotType getSlotType(@NotNull InventoryView view, int slot) {
        return (InventoryType.SlotType) SLOT_TYPE.invokeExact(view, slot);
    }

    /**
     * Gets the title of the view.
     *
     * @param view the view
     * @return the title
     * @since 0.1.0
     */
    @NotNull
    @SneakyThrows
    public String getTitle(@NotNull InventoryView view) {
        return (String) TITLE.invokeExact(view);
    }

    /**
     * Resolves a public method of {@link InventoryView}, whichever kind of type it is on this server.
     *
     * @param name the method name
     * @param returnType the return type
     * @param parameters the parameter types
     * @return the method handle
     * @since 0.1.0
     */
    @NotNull
    @SneakyThrows
    private MethodHandle find(
            @NotNull String name,
            @NotNull Class<?> returnType,
            @NotNull Class<?> @NotNull ... parameters
    ) {
        return MethodHandles.publicLookup()
                .findVirtual(InventoryView.class, name, MethodType.methodType(returnType, parameters));
    }
}
