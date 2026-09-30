package me.croabeast.inventory.pane.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.croabeast.inventory.pane.Pane;
import org.jetbrains.annotations.NotNull;

/**
 * An object for representing a pane with its slot.
 *
 * @since 0.1.0
 */
@Getter
@AllArgsConstructor
public class PositionedPane {

    /**
     * The slot of the pane.
     */
    @NotNull
    private final Slot slot;

    /**
     * The pane.
     */
    @NotNull
    private final Pane pane;

}
