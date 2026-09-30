package me.croabeast.inventory.nms.v1_19_4.util;

import lombok.experimental.UtilityClass;
import me.croabeast.inventory.adventure.ComponentHolder;
import me.croabeast.inventory.adventure.StringHolder;
import me.croabeast.inventory.adventure.TextHolder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A utility class for adding {@link TextHolder} support.
 *
 * @since 0.1.0
 */
@UtilityClass
public class TextHolderUtil {
    
    
    /**
     * Converts the specified value to a vanilla component.
     *
     * @param holder the value to convert
     * @return the value as a vanilla component
     * @since 0.1.0
     */
    @NotNull
    public Component toComponent(@NotNull TextHolder holder) {
        if (holder instanceof StringHolder) {
            return toComponent((StringHolder) holder);
        } else {
            return toComponent((ComponentHolder) holder);
        }
    }
    
    /**
     * Converts the specified legacy string holder to a vanilla component.
     *
     * @param holder the value to convert
     * @return the value as a vanilla component
     * @since 0.1.0
     */
    @NotNull
    private Component toComponent(@NotNull StringHolder holder) {
        return Component.literal(holder.asLegacyString());
    }
    
    /**
     * Converts the specified Adventure component holder to a vanilla component.
     *
     * @param holder the value to convert
     * @return the value as a vanilla component
     * @since 0.1.0
     */
    @NotNull
    private Component toComponent(@NotNull ComponentHolder holder) {
        return Objects.requireNonNull(Component.Serializer.fromJson(holder.asJson()));
    }
}
