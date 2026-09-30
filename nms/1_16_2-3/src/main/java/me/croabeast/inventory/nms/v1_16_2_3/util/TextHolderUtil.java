package me.croabeast.inventory.nms.v1_16_2_3.util;

import lombok.experimental.UtilityClass;
import me.croabeast.inventory.adventure.ComponentHolder;
import me.croabeast.inventory.adventure.StringHolder;
import me.croabeast.inventory.adventure.TextHolder;
import net.minecraft.server.v1_16_R2.ChatComponentText;
import net.minecraft.server.v1_16_R2.IChatBaseComponent;
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
    public IChatBaseComponent toComponent(@NotNull TextHolder holder) {
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
    private IChatBaseComponent toComponent(@NotNull StringHolder holder) {
        return new ChatComponentText(holder.asLegacyString());
    }
    
    /**
     * Converts the specified Adventure component holder to a vanilla component.
     *
     * @param holder the value to convert
     * @return the value as a vanilla component
     * @since 0.1.0
     */
    @NotNull
    private IChatBaseComponent toComponent(@NotNull ComponentHolder holder) {
        return Objects.requireNonNull(IChatBaseComponent.ChatSerializer.a(holder.asJson()));
    }
}
