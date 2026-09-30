package me.croabeast.inventory.adventure;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Wrapper of an Adventure {@link Component}.
 *
 * @since 0.1.0
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class ComponentHolder extends TextHolder {
    
    /**
     * Whether the server platform natively supports Adventure.
     * A null value indicates that we don't yet know this: it hasn't been determined yet.
     * This field should not be used directly, use {@link #isNativeAdventureSupport()} instead.
     */
    @Nullable
    private static Boolean nativeAdventureSupport;
    
    /**
     * The serializer to use when converting wrapped values to legacy strings.
     * A null value indicates that we haven't created the serializer yet.
     * This field should not be used directly, use {@link #getLegacySerializer()} instead.
     */
    @Nullable
    private static LegacyComponentSerializer legacySerializer;
    
    /**
     * Wraps the specified Adventure component.
     *
     * @param value the value to wrap
     * @return an instance that wraps the specified value
     * @since 0.1.0
     */
    @NotNull
    public static ComponentHolder of(@NotNull Component value) {
        Validate.notNull(value, "value mustn't be null");
        return isNativeAdventureSupport()
                ? new NativeComponentHolder(value)
                : new ForeignComponentHolder(value);
    }

    /**
     * Wraps the specified string as a plain text component. The string is not parsed, so legacy color codes and
     * MiniMessage tags are shown as they are written.
     *
     * @param string the text to wrap
     * @return an instance that wraps a text component with the specified string
     * @since 0.1.0
     */
    @NotNull
    public static ComponentHolder fromString(@NotNull String string) {
        return of(Component.text(string));
    }
    
    /**
     * Gets whether the server platform natively supports Adventure.
     * Native Adventure support means that eg. {@link ItemMeta#displayName(Component)}
     * is a valid method.
     *
     * @return whether the server platform natively supports Adventure
     * @since 0.1.0
     */
    private static boolean isNativeAdventureSupport() {
        if (nativeAdventureSupport == null)
            try {
                NativeComponentHolder holder = getComponentHolder();

                ItemMeta meta = new ItemStack(Material.STONE).getItemMeta();
                holder.asItemDisplayName(meta);
                holder.asItemLoreAtEnd(meta);
                
                nativeAdventureSupport = true;
            } catch (Throwable t) {
                nativeAdventureSupport = false;
            }

        return nativeAdventureSupport;
    }

    private static NativeComponentHolder getComponentHolder() {
        Component component = Component.text("test");
        NativeComponentHolder holder = new NativeComponentHolder(component);

        //If NoSuchMethodError or something is thrown we can assume that
        //Adventure components are not natively supported by the server platform

        //noinspection unused
        Object ignored1 = holder.asInventoryTitle(null, 9);
        //noinspection unused
        Object ignored2 = holder.asInventoryTitle(null, InventoryType.HOPPER);
        return holder;
    }

    /**
     * Gets the serializer to use when converting wrapped values to legacy strings.
     * Main use case being the implementation of {@link #asLegacyString()}.
     *
     * @return a serializer for converting wrapped values to legacy strings
     * @since 0.1.0
     */
    private static LegacyComponentSerializer getLegacySerializer() {
        if (legacySerializer == null) {
            LegacyComponentSerializer.Builder builder = LegacyComponentSerializer.builder()
                    .character(LegacyComponentSerializer.SECTION_CHAR);
            if (!net.md_5.bungee.api.ChatColor.class.isEnum()) {
                //1.16+ Spigot (or Paper), hex colors are supported, no need to down sample them
                builder.hexColors().useUnusualXRepeatedCharacterHexFormat();
            }
            legacySerializer = builder.build();
        }
        return legacySerializer;
    }
    
    /**
     * The Adventure component this instance wraps.
     */
    @NotNull
    protected final Component value;
        
    /**
     * Gets the Adventure component this instance wraps.
     *
     * @return the contained Adventure component
     * @since 0.1.0
     */
    @NotNull
    public Component getComponent() {
        return value;
    }
    
    /**
     * Gets the wrapped Adventure component in a JSON representation.
     *
     * @return the contained Adventure component as JSON
     * @since 0.1.0
     */
    @NotNull
    public JsonElement asJson() {
        return GsonComponentSerializer.gson().serializeToTree(value);
    }
    
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
                && Objects.equals(value, ((ComponentHolder) other).value);
    }
    
    @NotNull
    public String asLegacyString() {
        return getLegacySerializer().serialize(value);
    }
}
