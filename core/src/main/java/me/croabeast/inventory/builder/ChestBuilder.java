package me.croabeast.inventory.builder;

import lombok.Getter;
import me.croabeast.inventory.adventure.StringHolder;
import me.croabeast.inventory.adventure.TextHolder;
import me.croabeast.inventory.gui.GuiItem;
import me.croabeast.inventory.gui.type.ChestGui;
import me.croabeast.inventory.pane.OutlinePane;
import me.croabeast.inventory.pane.PaginatedPane;
import me.croabeast.inventory.pane.Pane;
import me.croabeast.inventory.pane.component.PagingButtons;
import me.croabeast.inventory.pane.util.Slot;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.IntConsumer;

/**
 * A fluent builder for paginated {@link ChestGui}s.
 * <p>
 * Every pane is added to a {@link PaginatedPane} that covers the whole chest, so each method that places something
 * takes the page it goes on. Pages are created in order: a pane can be added to an existing page or to the page right
 * after the last one.
 * </p>
 * <pre><code>
 * ChestBuilder menu = ChestBuilder.of(6, "Shop")
 *     .cancelClicks()
 *     .populate(1, 1, 7, 4, items);
 * menu.forEachPage(page -&gt; menu.fill(page, filler).addItem(page, 8, 0, close));
 * menu.setPagingButtons(3, 5, 3, previous, next).show(player);
 * </code></pre>
 *
 * @since 0.1.0
 */
public final class ChestBuilder {

    /**
     * The gui being built.
     */
    @NotNull
    @Getter
    private final ChestGui gui;

    /**
     * The pane holding every page of the gui.
     */
    @NotNull
    @Getter
    private final PaginatedPane pages;

    /**
     * The plugin that owns the gui and the items this builder creates.
     */
    @NotNull
    private final Plugin plugin;

    private ChestBuilder(int rows, @NotNull TextHolder title, @NotNull Plugin plugin) {
        this.gui = new ChestGui(rows, title, plugin);
        this.pages = new PaginatedPane(9, rows);
        this.plugin = plugin;

        gui.addPane(Slot.fromXY(0, 0), pages);
    }

    /**
     * Adds a pane to a page.
     *
     * @param page the page index
     * @param slot the position of the pane
     * @param pane the pane
     * @return this builder
     * @throws IllegalArgumentException if the page is negative or more than one past the last page
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder addPane(int page, @NotNull Slot slot, @NotNull Pane pane) {
        pages.addPane(page, slot, pane);
        return this;
    }

    /**
     * Adds a single item to a page.
     *
     * @param page the page index
     * @param x    the x coordinate
     * @param y    the y coordinate
     * @param item the item
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder addItem(int page, int x, int y, @NotNull GuiItem item) {
        return addItem(page, x, y, item, Pane.Priority.NORMAL);
    }

    /**
     * Adds a single item to a page, drawn by the given priority over or under other panes on the same slot.
     *
     * @param page     the page index
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param item     the item
     * @param priority the priority of the item
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder addItem(int page, int x, int y, @NotNull GuiItem item, @NotNull Pane.Priority priority) {
        OutlinePane pane = new OutlinePane(1, 1, priority);
        pane.addItem(item);
        return addPane(page, Slot.fromXY(x, y), pane);
    }

    /**
     * Fills every slot of a page with the item, under everything else on the page. A single repeating pane is used,
     * not one pane per slot.
     *
     * @param page the page index
     * @param item the filler item
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder fill(int page, @NotNull GuiItem item) {
        OutlinePane pane = new OutlinePane(pages.getLength(), pages.getHeight(), Pane.Priority.LOWEST);
        pane.setRepeat(true);
        pane.addItem(item);
        return addPane(page, Slot.fromXY(0, 0), pane);
    }

    /**
     * Lays the items out in the given area, starting on the first page and adding pages until every item is placed.
     *
     * @param x      the x coordinate of the area
     * @param y      the y coordinate of the area
     * @param length the length of the area
     * @param height the height of the area
     * @param items  the items, in order
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder populate(int x, int y, int length, int height, @NotNull List<? extends GuiItem> items) {
        int perPage = length * height;
        Slot slot = Slot.fromXY(x, y);

        for (int start = 0, page = 0; start < items.size(); start += perPage, page++) {
            OutlinePane pane = new OutlinePane(length, height);
            for (GuiItem item : items.subList(start, Math.min(items.size(), start + perPage)))
                pane.addItem(item);

            pages.addPane(page, slot, pane);
        }

        return this;
    }

    /**
     * Runs the action once for every page that exists, with the page index. Useful to add the same frame to every page
     * after {@link #populate(int, int, int, int, List)}.
     *
     * @param action the action
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder forEachPage(@NotNull IntConsumer action) {
        for (int page = 0, count = pages.getPages(); page < count; page++)
            action.accept(page);
        return this;
    }

    /**
     * Adds previous and next page buttons, shown only when there is a page to go to. They are added to the gui itself,
     * so they work on every page, including pages created afterwards.
     *
     * @param x        the x coordinate of the previous button
     * @param y        the y coordinate of both buttons
     * @param length   the distance from the previous button to the next button, plus one
     * @param previous the previous page button
     * @param next     the next page button
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder setPagingButtons(int x, int y, int length, @NotNull GuiItem previous, @NotNull GuiItem next) {
        PagingButtons buttons = new PagingButtons(length, Pane.Priority.HIGH, pages, plugin);
        buttons.setBackwardButton(previous);
        buttons.setForwardButton(next);

        gui.addPane(Slot.fromXY(x, y), buttons);
        return this;
    }

    /**
     * Cancels every click and drag in the gui, so players cannot take or place items. Item actions still run.
     *
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder cancelClicks() {
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        gui.setOnGlobalDrag(event -> event.setCancelled(true));
        return this;
    }

    /**
     * Changes the number of rows of the gui.
     *
     * @param rows the rows, from 1 to 6
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder setRows(int rows) {
        gui.setRows(rows);
        pages.setHeight(rows);
        return this;
    }

    /**
     * Displays the page and updates the gui for its viewers.
     *
     * @param page the page index
     * @return this builder
     * @throws ArrayIndexOutOfBoundsException if the page does not exist
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder setPage(int page) {
        pages.setPage(page);
        gui.update();
        return this;
    }

    /**
     * Displays the next page, if there is one.
     *
     * @return {@code true} if the page changed
     * @since 0.1.0
     */
    public boolean nextPage() {
        if (pages.getPage() + 1 >= pages.getPages())
            return false;

        setPage(pages.getPage() + 1);
        return true;
    }

    /**
     * Displays the previous page, if there is one.
     *
     * @return {@code true} if the page changed
     * @since 0.1.0
     */
    public boolean previousPage() {
        if (pages.getPage() <= 0)
            return false;

        setPage(pages.getPage() - 1);
        return true;
    }

    /**
     * Shows the gui to the human entity.
     *
     * @param humanEntity the viewer
     * @since 0.1.0
     */
    public void show(@NotNull HumanEntity humanEntity) {
        gui.show(humanEntity);
    }

    /**
     * Starts a builder for a chest gui.
     *
     * @param rows   the rows, from 1 to 6
     * @param title  the title
     * @param plugin the plugin that owns the gui
     * @return a new builder
     * @throws me.croabeast.inventory.exception.UnsupportedVersionException if this server version is not supported
     * @since 0.1.0
     */
    @NotNull
    public static ChestBuilder of(int rows, @NotNull TextHolder title, @NotNull Plugin plugin) {
        return new ChestBuilder(rows, title, plugin);
    }

    /**
     * Starts a builder for a chest gui.
     *
     * @param rows   the rows, from 1 to 6
     * @param title  the title, already colored
     * @param plugin the plugin that owns the gui
     * @return a new builder
     * @throws me.croabeast.inventory.exception.UnsupportedVersionException if this server version is not supported
     * @since 0.1.0
     */
    @NotNull
    public static ChestBuilder of(int rows, @NotNull String title, @NotNull Plugin plugin) {
        return of(rows, StringHolder.of(title), plugin);
    }

    /**
     * Starts a builder for a chest gui owned by the plugin that loaded this class.
     *
     * @param rows  the rows, from 1 to 6
     * @param title the title
     * @return a new builder
     * @throws me.croabeast.inventory.exception.UnsupportedVersionException if this server version is not supported
     * @since 0.1.0
     */
    @NotNull
    public static ChestBuilder of(int rows, @NotNull TextHolder title) {
        return of(rows, title, JavaPlugin.getProvidingPlugin(ChestBuilder.class));
    }

    /**
     * Starts a builder for a chest gui owned by the plugin that loaded this class.
     *
     * @param rows  the rows, from 1 to 6
     * @param title the title, already colored
     * @return a new builder
     * @throws me.croabeast.inventory.exception.UnsupportedVersionException if this server version is not supported
     * @since 0.1.0
     */
    @NotNull
    public static ChestBuilder of(int rows, @NotNull String title) {
        return of(rows, StringHolder.of(title));
    }
}
