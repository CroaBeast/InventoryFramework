package me.croabeast.inventory.util.version;

import lombok.experimental.UtilityClass;
import me.croabeast.inventory.exception.UnsupportedVersionException;
import me.croabeast.inventory.nms.AnvilInventory;
import me.croabeast.inventory.nms.CustomInventory;
import me.croabeast.inventory.nms.MerchantInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Utility class containing versioning related methods.
 *
 * @since 0.1.0
 */
@UtilityClass
public class VersionMatcher {

    /**
     * The inventories that need a version-specific implementation. Every NMS module implements them as
     * {@code me.croabeast.inventory.nms.<version>.<name>InventoryImpl}, where {@code <version>} is the lowercase name of
     * the {@link Version} constant.
     *
     * @since 0.1.0
     */
    public enum Type {
        ANVIL("Anvil"),
        BEACON("Beacon"),
        CARTOGRAPHY_TABLE("CartographyTable"),
        ENCHANTING_TABLE("EnchantingTable"),
        GRINDSTONE("Grindstone"),
        LOOM("Loom"),
        MERCHANT("Merchant"),
        SMITHING_TABLE("SmithingTable"),
        // 1.19.4 has both smithing tables; before it the only one is the legacy table
        LEGACY_SMITHING_TABLE("LegacySmithingTable", "SmithingTable"),
        STONECUTTER("Stonecutter");

        /**
         * The class names to try, without the {@code InventoryImpl} suffix, in order
         */
        @NotNull
        private final String @NotNull [] names;

        Type(@NotNull String @NotNull ... names) {
            this.names = names;
        }
    }

    /**
     * Gets a new inventory of the specified type for the specified version.
     *
     * @param type the type of inventory
     * @param version the version to get the inventory of
     * @return the inventory
     * @throws UnsupportedVersionException when the inventory does not exist in the specified version
     * @since 0.1.0
     */
    @NotNull
    public CustomInventory newInventory(@NotNull Type type, @NotNull Version version) {
        boolean smithing = type == Type.SMITHING_TABLE || type == Type.LEGACY_SMITHING_TABLE;

        if (smithing && !version.existsModernSmithingTable() && !version.existsLegacySmithingTable())
            throw new UnsupportedVersionException("Smithing tables didn't exist in version " + version);

        if (type == Type.SMITHING_TABLE && !version.existsModernSmithingTable())
            throw new UnsupportedVersionException("Modern smithing tables didn't exist in version " + version);

        if (type == Type.LEGACY_SMITHING_TABLE && !version.existsLegacySmithingTable())
            throw new UnsupportedVersionException("Legacy smithing tables don't exist in version " + version);

        Class<? extends CustomInventory> implementation = findImplementation(type, version);

        if (implementation == null)
            throw new UnsupportedVersionException(type + " inventories don't exist in version " + version);

        try {
            return implementation.getConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Gets a new anvil inventory for the specified version.
     *
     * @param version the version to get the inventory of
     * @return the anvil inventory
     * @since 0.1.0
     */
    @NotNull
    public AnvilInventory newAnvilInventory(@NotNull Version version) {
        return (AnvilInventory) newInventory(Type.ANVIL, version);
    }

    /**
     * Gets a new merchant inventory for the specified version.
     *
     * @param version the version to get the inventory of
     * @return the merchant inventory
     * @since 0.1.0
     */
    @NotNull
    public MerchantInventory newMerchantInventory(@NotNull Version version) {
        return (MerchantInventory) newInventory(Type.MERCHANT, version);
    }

    /**
     * Finds the implementation of an inventory type for a version, without initializing it.
     *
     * @param type the type of inventory
     * @param version the version
     * @return the implementation, or null if the version has none
     * @since 0.1.0
     */
    @Nullable
    Class<? extends CustomInventory> findImplementation(@NotNull Type type, @NotNull Version version) {
        // Taken from a class instead of a literal, so the name follows the package when IF is relocated
        String base = CustomInventory.class.getName();
        String prefix = base.substring(0, base.lastIndexOf('.') + 1) + version.name().toLowerCase(Locale.ROOT) + '.';

        for (String name : type.names) {
            try {
                return Class.forName(prefix + name + "InventoryImpl", false, VersionMatcher.class.getClassLoader())
                        .asSubclass(CustomInventory.class);
            } catch (ClassNotFoundException ignored) {}
        }

        return null;
    }
}
