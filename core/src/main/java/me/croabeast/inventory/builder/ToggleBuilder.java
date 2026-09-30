package me.croabeast.inventory.builder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.croabeast.inventory.gui.GuiItem;
import me.croabeast.inventory.pane.Pane;
import me.croabeast.inventory.pane.component.ToggleButton;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * A fluent builder for single slot {@link ToggleButton}s.
 * <pre><code>
 * ToggleButton button = ToggleBuilder.of(enabled)
 *     .setItems(onItem, offItem)
 *     .setOnToggle((event, state) -&gt; save(state))
 *     .getButton();
 * menu.addPane(0, Slot.fromXY(4, 2), button);
 * </code></pre>
 *
 * @since 0.1.0
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToggleBuilder {

    /**
     * The button being built.
     */
    @NotNull
    @Getter
    private final ToggleButton button;

    /**
     * Sets the items shown while the button is enabled and disabled.
     *
     * @param enabled  the item shown while enabled
     * @param disabled the item shown while disabled
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ToggleBuilder setItems(@NotNull GuiItem enabled, @NotNull GuiItem disabled) {
        button.setEnabledItem(enabled);
        button.setDisabledItem(disabled);
        return this;
    }

    /**
     * Sets the action run after every click, with the state the button is in after the click.
     *
     * @param action the click event and the new state
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ToggleBuilder setOnToggle(@NotNull BiConsumer<? super InventoryClickEvent, Boolean> action) {
        button.setOnClick(event -> action.accept(event, button.isEnabled()));
        return this;
    }

    /**
     * Sets whether players can toggle the button by clicking it.
     *
     * @param allowToggle whether clicks toggle the button
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ToggleBuilder setAllowToggle(boolean allowToggle) {
        button.allowToggle(allowToggle);
        return this;
    }

    /**
     * Sets the priority of the button over other panes on the same slot.
     *
     * @param priority the priority
     * @return this builder
     * @since 0.1.0
     */
    @NotNull
    public ToggleBuilder setPriority(@NotNull Pane.Priority priority) {
        button.setPriority(priority);
        return this;
    }

    /**
     * Starts a builder for a single slot toggle button.
     *
     * @param enabled whether the button starts enabled
     * @param plugin  the plugin that owns the button
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static ToggleBuilder of(boolean enabled, @NotNull Plugin plugin) {
        return new ToggleBuilder(new ToggleButton(1, 1, enabled, plugin));
    }

    /**
     * Starts a builder for a single slot toggle button owned by the plugin that loaded InventoryFramework.
     *
     * @param enabled whether the button starts enabled
     * @return a new builder
     * @since 0.1.0
     */
    @NotNull
    public static ToggleBuilder of(boolean enabled) {
        return new ToggleBuilder(new ToggleButton(1, 1, enabled));
    }
}
