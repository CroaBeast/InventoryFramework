package me.croabeast.inventory.util;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryViewUtilTest {

    @Test
    void testEveryMethodResolvesAndDispatches() {
        List<String> calls = new ArrayList<>();
        HumanEntity player = (HumanEntity) Proxy.newProxyInstance(
                HumanEntity.class.getClassLoader(),
                new Class<?>[] {HumanEntity.class},
                (proxy, method, args) -> {
                    calls.add(method.getName());
                    return null;
                }
        );
        View view = new View(player);

        assertNull(InventoryViewUtil.getTopInventory(view));
        assertNull(InventoryViewUtil.getBottomInventory(view));
        assertNull(InventoryViewUtil.getInventory(view, InventoryView.OUTSIDE));
        assertEquals(InventoryType.SlotType.OUTSIDE, InventoryViewUtil.getSlotType(view, InventoryView.OUTSIDE));
        assertEquals("title", InventoryViewUtil.getTitle(view));

        InventoryViewUtil.setCursor(view, null);
        assertNull(InventoryViewUtil.getCursor(view));
        assertEquals(2, calls.size());
        assertEquals("setItemOnCursor", calls.get(0));
        assertEquals("getItemOnCursor", calls.get(1));
    }

    private static class View extends InventoryView {

        private final HumanEntity player;

        private View(HumanEntity player) {
            this.player = player;
        }

        @Override
        public Inventory getTopInventory() {
            return null;
        }

        @Override
        public Inventory getBottomInventory() {
            return null;
        }

        @Override
        public HumanEntity getPlayer() {
            return player;
        }

        @Override
        public InventoryType getType() {
            return InventoryType.CHEST;
        }

        @Override
        public String getTitle() {
            return "title";
        }

        @Override
        public String getOriginalTitle() {
            return "title";
        }

        @Override
        public void setTitle(String title) {}
    }
}
