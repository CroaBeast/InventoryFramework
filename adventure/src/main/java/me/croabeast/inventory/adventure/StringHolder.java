package me.croabeast.inventory.adventure;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper of a legacy string value.
 * {@link org.bukkit.ChatColor} based formatting is used.
 *
 * @since 0.1.0
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringHolder extends TextHolder {
    
    /**
     * Cached instance which wraps an empty {@link String}.
     */
    @NotNull
    private static final StringHolder EMPTY = StringHolder.of("");
    
    /**
     * Wraps the specified legacy string.
     *
     * @param value the value to wrap
     * @return an instance that wraps the specified value
     * @since 0.1.0
     */
    @NotNull
    public static StringHolder of(@NotNull String value) {
        Validate.notNull(value, "value mustn't be null");
        return new StringHolder(value);
    }
    
    /**
     * Gets an instance that contains no characters.
     *
     * @return an instance without any characters
     * @since 0.1.0
     */
    @NotNull
    public static StringHolder empty() {
        return EMPTY;
    }
    
    /**
     * The legacy string this instance wraps.
     */
    @NotNull
    private final String value;
        
    @NotNull
    public String toString() {
        return getClass().getSimpleName() + "{" + value + "}";
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        return other != null && getClass() == other.getClass()
                && Objects.equals(value, ((StringHolder) other).value);
    }
    
    @NotNull
    public String asLegacyString() {
        return value;
    }
    
    @NotNull
    public Inventory asInventoryTitle(InventoryHolder holder, InventoryType type) {
        //noinspection deprecation
        return Bukkit.createInventory(holder, type, value);
    }
    
    @NotNull
    public Inventory asInventoryTitle(InventoryHolder holder, int size) {
        //noinspection deprecation
        return Bukkit.createInventory(holder, size, value);
    }

    @NotNull
    public Merchant asMerchantTitle() {
        //noinspection deprecation
        return Bukkit.createMerchant(value);
    }

    @Override
    public void asItemDisplayName(ItemMeta meta) {
        //noinspection deprecation
        meta.setDisplayName(value);
    }
    
    @Override
    public void asItemLoreAtEnd(ItemMeta meta) {
        //noinspection deprecation
        List<String> lore = meta.hasLore()
                ? Objects.requireNonNull(meta.getLore())
                : new ArrayList<>();
        lore.add(value);
        //noinspection deprecation
        meta.setLore(lore);
    }
}
