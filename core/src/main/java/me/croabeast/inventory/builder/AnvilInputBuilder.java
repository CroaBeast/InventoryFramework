package me.croabeast.inventory.builder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.croabeast.inventory.adventure.StringHolder;
import me.croabeast.inventory.adventure.TextHolder;
import me.croabeast.inventory.exception.UnsupportedVersionException;
import me.croabeast.inventory.gui.type.AnvilGui;
import me.croabeast.inventory.pane.StaticPane;
import me.croabeast.inventory.pane.util.Slot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A single line text prompt backed by an {@link AnvilGui}.
 * <p>
 * The input slot shows the current value, and clicking the result slot passes the typed text to
 * {@link #setOnConfirm(BiConsumer)}. If an anvil cannot be opened on this server version,
 * {@link #setOnUnsupported(Consumer)} runs instead, so callers can fall back to another input method such as chat.
 * </p>
 * <pre><code>
 * AnvilInputBuilder.of("Permission")
 *     .setInput(current)
 *     .setOnConfirm((player, text) -&gt; save(text))
 *     .setOnUnsupported(player -&gt; startChatEditor(player))
 *     .show(player);
 * </code></pre>
 *
 * @since 0.1.0
 */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnvilInputBuilder {

    /**
     * Shown instead of a blank value, since an item cannot have an empty name.
     */
    private static final String EMPTY_INPUT = "<empty>";

    /**
     * The title of the anvil.
     */
    @NotNull
    private final TextHolder title;

    /**
     * The plugin that owns the gui.
     */
    @NotNull
    private final Plugin plugin;

    /**
     * The value the input starts with.
     */
    @NotNull
    private String input = "";

    /**
     * The item in the input slot. Its name is replaced by the input.
     */
    @NotNull
    private ItemStack inputItem = new ItemStack(Material.PAPER);

    /**
     * The item in the result slot, or {@code null} for a green "Save" pane.
     */
    @Nullable
    private ItemStack resultItem;

    /**
     * Called with the viewer and the typed text when the result slot is clicked.
     */
    @Nullable
    private BiConsumer<? super HumanEntity, ? super String> onConfirm;

    /**
     * Called instead of opening the anvil when this server version is not supported.
     */
    @Nullable
    private Consumer<? super HumanEntity> onUnsupported;

    /**
     * Opens the anvil for the human entity, or runs the unsupported callback if it cannot be opened.
     *
     * @param humanEntity the viewer
     * @since 0.1.0
     */
    public void show(@NotNull HumanEntity humanEntity) {
        try {
            AnvilGui gui = new AnvilGui(title, plugin);
            gui.setCost((short) 0);
            gui.setOnGlobalClick(event -> event.setCancelled(true));
            gui.setOnGlobalDrag(event -> event.setCancelled(true));

            StaticPane inputPane = new StaticPane(1, 1);
            inputPane.addItem(ItemBuilder.of(inputItem)
                    .setName(input.trim().isEmpty() ? EMPTY_INPUT : input)
                    .build(plugin), 0, 0);
            gui.getFirstItemComponent().addPane(Slot.fromXY(0, 0), inputPane);

            ItemBuilder result = resultItem != null ?
                    ItemBuilder.of(resultItem) :
                    ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Save");

            StaticPane resultPane = new StaticPane(1, 1);
            resultPane.addItem(result.setAction(event -> {
                if (onConfirm == null)
                    return;

                // The rename field starts with the input item's name, so an untouched blank value comes back as the
                // placeholder.
                String text = gui.getRenameText();
                onConfirm.accept(event.getWhoClicked(), EMPTY_INPUT.equals(ChatColor.stripColor(text)) ? "" : text);
            }).build(plugin), 0, 0);
            gui.getResultComponent().addPane(Slot.fromXY(0, 0), resultPane);

            gui.show(humanEntity);
        } catch (UnsupportedVersionException | LinkageError e) {
            if (onUnsupported != null)
                onUnsupported.accept(humanEntity);
        }
    }

    /**
     * Starts a text prompt.
     *
     * @param title  the title of the anvil
     * @param plugin the plugin that owns the gui
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static AnvilInputBuilder of(@NotNull TextHolder title, @NotNull Plugin plugin) {
        return new AnvilInputBuilder(title, plugin);
    }

    /**
     * Starts a text prompt.
     *
     * @param title  the title of the anvil, already colored
     * @param plugin the plugin that owns the gui
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static AnvilInputBuilder of(@NotNull String title, @NotNull Plugin plugin) {
        return of(StringHolder.of(title), plugin);
    }

    /**
     * Starts a text prompt owned by the plugin that loaded this class.
     *
     * @param title the title of the anvil, already colored
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static AnvilInputBuilder of(@NotNull String title) {
        return of(title, JavaPlugin.getProvidingPlugin(AnvilInputBuilder.class));
    }
}
