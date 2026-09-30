package me.croabeast.inventory.builder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.croabeast.inventory.adventure.StringHolder;
import me.croabeast.inventory.adventure.TextHolder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A three row confirmation dialog: confirm on the left, cancel on the right and an optional item in the middle.
 * <p>
 * The callbacks run inside the click event. Bukkit does not allow closing or opening inventories there directly, so
 * schedule that for the next tick.
 * </p>
 * <pre><code>
 * ConfirmBuilder.of("Delete home?")
 *     .setInfoItem(homeIcon)
 *     .setOnConfirm(player -&gt; deleteHome(player))
 *     .setOnCancel(player -&gt; scheduleClose(player))
 *     .show(player);
 * </code></pre>
 *
 * @since 0.1.0
 */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfirmBuilder {

    /**
     * The title of the dialog.
     */
    @NotNull
    private final TextHolder title;

    /**
     * The plugin that owns the gui.
     */
    @NotNull
    private final Plugin plugin;

    /**
     * The confirm item, or {@code null} for a green "Confirm" block.
     */
    @Nullable
    private ItemStack confirmItem;

    /**
     * The cancel item, or {@code null} for a red "Cancel" block.
     */
    @Nullable
    private ItemStack cancelItem;

    /**
     * The item shown between both buttons, or {@code null} for none.
     */
    @Nullable
    private ItemStack infoItem;

    /**
     * Called with the viewer when the confirm item is clicked.
     */
    @Nullable
    private Consumer<? super HumanEntity> onConfirm;

    /**
     * Called with the viewer when the cancel item is clicked.
     */
    @Nullable
    private Consumer<? super HumanEntity> onCancel;

    /**
     * Builds the dialog. The returned builder can still be changed, for example to fill the empty slots.
     *
     * @return the chest builder of the dialog
     * @throws me.croabeast.inventory.exception.UnsupportedVersionException if this server version is not supported
     * @since 0.1.0
     */
    @NotNull
    public ChestBuilder build() {
        ItemBuilder confirm = confirmItem != null ?
                ItemBuilder.of(confirmItem) :
                ItemBuilder.of(Material.LIME_CONCRETE).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm");
        ItemBuilder cancel = cancelItem != null ?
                ItemBuilder.of(cancelItem) :
                ItemBuilder.of(Material.RED_CONCRETE).setName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel");

        ChestBuilder chest = ChestBuilder.of(3, title, plugin).cancelClicks()
                .addItem(0, 2, 1, confirm.setAction(event -> {
                    if (onConfirm != null)
                        onConfirm.accept(event.getWhoClicked());
                }).build(plugin))
                .addItem(0, 6, 1, cancel.setAction(event -> {
                    if (onCancel != null)
                        onCancel.accept(event.getWhoClicked());
                }).build(plugin));

        if (infoItem != null)
            chest.addItem(0, 4, 1, ItemBuilder.of(infoItem).build(plugin));

        return chest;
    }

    /**
     * Builds the dialog and shows it to the human entity.
     *
     * @param humanEntity the viewer
     * @since 0.1.0
     */
    public void show(@NotNull HumanEntity humanEntity) {
        build().show(humanEntity);
    }

    /**
     * Starts a confirmation dialog.
     *
     * @param title  the title of the dialog
     * @param plugin the plugin that owns the gui
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static ConfirmBuilder of(@NotNull TextHolder title, @NotNull Plugin plugin) {
        return new ConfirmBuilder(title, plugin);
    }

    /**
     * Starts a confirmation dialog.
     *
     * @param title  the title of the dialog, already colored
     * @param plugin the plugin that owns the gui
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static ConfirmBuilder of(@NotNull String title, @NotNull Plugin plugin) {
        return of(StringHolder.of(title), plugin);
    }

    /**
     * Starts a confirmation dialog owned by the plugin that loaded this class.
     *
     * @param title the title of the dialog, already colored
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static ConfirmBuilder of(@NotNull String title) {
        return of(title, JavaPlugin.getProvidingPlugin(ConfirmBuilder.class));
    }
}
