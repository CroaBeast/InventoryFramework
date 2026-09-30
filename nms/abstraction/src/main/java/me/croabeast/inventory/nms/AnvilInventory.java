package me.croabeast.inventory.nms;

import me.croabeast.inventory.nms.util.ObservableValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * An anvil inventory
 *
 * @since 0.1.0
 */
public abstract class AnvilInventory implements CustomInventory {

    /**
     * The name input text.
     */
    @NotNull
    protected final ObservableValue<@NotNull String> observableText = new ObservableValue<>("");

    /**
     * The enchantment cost displayed
     */
    protected short cost;

    /**
     * Sets the enchantment level cost for this anvil gui. Taking the item from the result slot will not actually remove
     * these levels. Having a cost specified does not impede a player's ability to take the item in the result item,
     * even if the player does not have the specified amount of levels. The cost must be a non-negative number.
     *
     * @param cost the cost
     * @since 0.1.0
     * @throws IllegalArgumentException when the cost is less than zero
     */
    public void setCost(short cost) {
        if (cost < 0)
            throw new IllegalArgumentException("Cost must be non-negative");

        this.cost = cost;
    }

    /**
     * Gets the text shown in the rename slot of the anvil
     *
     * @return the rename text
     * @since 0.1.0
     */
    @NotNull
    public String getRenameText() {
        String text = observableText.get();

        if (text == null)
            throw new IllegalStateException("Rename text is null");

        return text;
    }

    /**
     * Subscribes to changes of the name input.
     *
     * @param onNameInputChanged the consumer to call when the name input changes
     * @since 0.1.0
     */
    public void subscribeToNameInputChanges(@NotNull Consumer<? super String> onNameInputChanged) {
        this.observableText.subscribe(onNameInputChanged);
    }
}
