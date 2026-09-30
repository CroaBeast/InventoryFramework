package me.croabeast.inventory.builder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.croabeast.inventory.adventure.TextHolder;
import me.croabeast.inventory.gui.GuiItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * A fluent builder for {@link GuiItem}s.
 * <p>
 * Names and lore given as strings are used as they are, so they must already be colored with legacy section codes.
 * Use the {@link TextHolder} overloads for Adventure components.
 * </p>
 * <pre><code>
 * GuiItem item = ItemBuilder.of(Material.DIAMOND)
 *     .setName("§bShiny Diamond")
 *     .setLore("§7Click to claim.")
 *     .setAction(event -&gt; claim(event.getWhoClicked()))
 *     .build();
 * </code></pre>
 *
 * @since 0.1.0
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemBuilder {

    /**
     * The item being built. It is a copy, so the stack given to {@link #of(ItemStack)} is never changed.
     */
    @NotNull
    private final ItemStack item;

    /**
     * The action run when the built item is clicked, or {@code null} for none.
     */
    @Nullable
    @Setter
    @Accessors(chain = true)
    private Consumer<? super InventoryClickEvent> action;

    /**
     * Makes clicks on the built item cancel the event and do nothing else.
     *
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder cancelClicks() {
        return setAction(event -> event.setCancelled(true));
    }

    /**
     * Sets the stack size of the item.
     *
     * @param amount the stack size
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Applies a change to the item stack itself.
     *
     * @param consumer the change to apply
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder modifyItem(@NotNull Consumer<? super ItemStack> consumer) {
        consumer.accept(item);
        return this;
    }

    /**
     * Applies a change to the item meta and writes it back. Does nothing for items without meta, such as air.
     *
     * @param consumer the change to apply
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder modifyMeta(@NotNull Consumer<? super ItemMeta> consumer) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return this;

        consumer.accept(meta);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the display name, already colored
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder setName(@NotNull String name) {
        return modifyMeta(meta -> meta.setDisplayName(name));
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the display name
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder setName(@NotNull TextHolder name) {
        return modifyMeta(name::asItemDisplayName);
    }

    /**
     * Replaces the lore of the item. The given list is copied, not kept.
     *
     * @param lore the lore lines, already colored
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder setLore(@NotNull List<String> lore) {
        List<String> copy = new ArrayList<>(lore);
        return modifyMeta(meta -> meta.setLore(copy));
    }

    /**
     * Replaces the lore of the item.
     *
     * @param lore the lore lines, already colored
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder setLore(@NotNull String... lore) {
        return setLore(Arrays.asList(lore));
    }

    /**
     * Appends a line to the lore of the item.
     *
     * @param line the line to append
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder addLore(@NotNull TextHolder line) {
        return modifyMeta(line::asItemLoreAtEnd);
    }

    /**
     * Adds item flags, for example to hide attributes or enchantments.
     *
     * @param flags the flags to add
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ItemBuilder addFlags(@NotNull ItemFlag... flags) {
        return modifyMeta(meta -> meta.addItemFlags(flags));
    }

    /**
     * Returns a copy of the item as it is now.
     *
     * @return the item stack
     * @since 0.1.0
     */
    @NotNull
    public ItemStack buildStack() {
        return item.clone();
    }

    /**
     * Builds the gui item. The builder can be reused afterwards; every call returns a new item.
     *
     * @param plugin the plugin that owns the item
     * @return the gui item
     * @since 0.1.0
     */
    @NotNull
    public GuiItem build(@NotNull Plugin plugin) {
        return new GuiItem(item.clone(), action, plugin);
    }

    /**
     * Builds the gui item, owned by the plugin that loaded this class.
     *
     * @return the gui item
     * @since 0.1.0
     */
    @NotNull
    public GuiItem build() {
        return build(JavaPlugin.getProvidingPlugin(ItemBuilder.class));
    }

    /**
     * Starts a builder from a copy of the given item.
     *
     * @param item the item to copy
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static ItemBuilder of(@NotNull ItemStack item) {
        return new ItemBuilder(item.clone());
    }

    /**
     * Starts a builder for a single item of the given material.
     *
     * @param material the material
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static ItemBuilder of(@NotNull Material material) {
        return new ItemBuilder(new ItemStack(material));
    }
}
