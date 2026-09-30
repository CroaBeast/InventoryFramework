package me.croabeast.inventory.nms.v1_14;

import me.croabeast.inventory.nms.CustomInventory;
import me.croabeast.inventory.adventure.TextHolder;
import me.croabeast.inventory.nms.v1_14.util.TextHolderUtil;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryLoom;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Internal loom inventory for 1.14.
 *
 * @since 0.1.0
 */
public class LoomInventoryImpl implements CustomInventory {

    @NotNull
    public Inventory createInventory(@NotNull TextHolder title) {
        InventoryCraftResult resultSlot = new InventoryCraftResult() {
            @Override
            public void setItem(int slot, @NotNull ItemStack itemStack) {
                if (slot == 0 && !itemStack.isEmpty() && !(itemStack.getItem() instanceof ItemBanner))
                    throw new IllegalArgumentException("Only banners can be placed in the result slot");

                super.setItem(slot, itemStack);
            }
        };

        IInventory container = new InventoryViewProvider() {
            @NotNull
            public Container createMenu(int containerId, @Nullable PlayerInventory inventory,
                                        @NotNull EntityHuman player) {
                return new ContainerLoomImpl(containerId, player, this, resultSlot);
            }

            @NotNull
            public IChatBaseComponent getScoreboardDisplayName() {
                return TextHolderUtil.toComponent(title);
            }

            @Override
            public void setItem(int slot, @NotNull ItemStack itemStack) {
                if (slot == 0 && !itemStack.isEmpty() && !(itemStack.getItem() instanceof ItemBanner))
                    throw new IllegalArgumentException("Only banners can be placed in the banner slot");

                super.setItem(slot, itemStack);
            }
        };

        return new CraftInventoryLoom(container, resultSlot) {
            @NotNull
            public InventoryType getType() {
                return InventoryType.LOOM;
            }

            @Override
            public IInventory getInventory() {
                return container;
            }
        };
    }

    /**
     * This is a nice hack to get CraftBukkit to create custom inventories. By providing a container that is also a menu
     * provider, CraftBukkit will allow us to create a custom menu, rather than picking one of the built-in options.
     * That way, we can provide a menu with custom behaviour.
     *
     * @since 0.1.0
     */
    private abstract static class InventoryViewProvider extends InventorySubcontainer implements ITileInventory {

        /**
         * Creates a new inventory view provider with three slots.
         *
         * @since 0.1.0
         */
        public InventoryViewProvider() {
            super(3);
        }
    }

    /**
     * A custom container loom
     *
     * @since 0.1.0
     */
    private static class ContainerLoomImpl extends ContainerLoom {

        /**
         * The human entity viewing this menu.
         */
        @NotNull
        private final HumanEntity humanEntity;

        /**
         * The container for the items slots.
         */
        @NotNull
        private final InventorySubcontainer itemsSlots;

        /**
         * The container for the result slot.
         */
        @NotNull
        private final InventoryCraftResult resultSlot;

        /**
         * The corresponding Bukkit view. Will be not null after the first call to {@link #getBukkitView()} and null
         * prior.
         */
        @Nullable
        private CraftInventoryView bukkitEntity;

        /**
         * Creates a new custom smithing table container for the specified player
         *
         * @param containerId the container id
         * @param player the player
         * @param itemsSlots the item slots
         * @param resultSlot the result slot
         * @since 0.1.0
         */
        public ContainerLoomImpl(
                int containerId,
                @NotNull EntityHuman player,
                @NotNull InventorySubcontainer itemsSlots,
                @NotNull InventoryCraftResult resultSlot
        ) {
            super(containerId, player.inventory, ContainerAccess.at(player.world, BlockPosition.ZERO));

            this.humanEntity = player.getBukkitEntity();
            this.itemsSlots = itemsSlots;
            this.resultSlot = resultSlot;

            super.checkReachable = false;

            InventoryLargeChest container = new InventoryLargeChest(itemsSlots, resultSlot);

            updateSlot(0, container);
            updateSlot(1, container);
            updateSlot(2, container);
            updateSlot(3, container);
        }

        @Override
        public CraftInventoryView getBukkitView() {
            if (this.bukkitEntity != null)
                return this.bukkitEntity;

            org.bukkit.inventory.LoomInventory inventory = new CraftInventoryLoom(this.itemsSlots, this.resultSlot);

            this.bukkitEntity = new CraftInventoryView(this.humanEntity, inventory, this);

            return this.bukkitEntity;
        }

        @Override
        public boolean canUse(@Nullable EntityHuman nmsPlayer) {
            return true;
        }

        @Override
        public void a(@NotNull IInventory container) {}

        @Override
        public void b(@NotNull EntityHuman nmsPlayer) {}

        @Override
        public boolean a(@NotNull EntityHuman player, int buttonId) {
            return false;
        }

        /**
         * Updates the current slot at the specified index to a new slot. The new slot will have the same slot, x, y,
         * and index as the original. The container of the new slot will be set to the value specified.
         *
         * @param slotIndex the slot index to update
         * @param container the container of the new slot
         * @since 0.1.0
         */
        private void updateSlot(int slotIndex, @NotNull IInventory container) {
            Slot slot = super.slots.get(slotIndex);

            Slot newSlot = new Slot(container, slotIndex, slot.e, slot.f);
            newSlot.rawSlotIndex = slot.rawSlotIndex;

            super.slots.set(slotIndex, newSlot);
        }
    }
}
