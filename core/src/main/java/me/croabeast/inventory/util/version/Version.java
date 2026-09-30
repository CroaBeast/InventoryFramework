package me.croabeast.inventory.util.version;

import me.croabeast.inventory.exception.UnsupportedVersionException;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;

/**
 * The different supported NMS versions
 *
 * @since 0.1.0
 */
public enum Version {

    /**
     * Version 1.14.x
     *
     * @since 0.1.0
     */
    V1_14,

    /**
     * Version 1.15.x
     *
     * @since 0.1.0
     */
    V1_15,

    /**
     * Version 1.16.1
     *
     * @since 0.1.0
     */
    V1_16_1,

    /**
     * Version 1.16.2 - 1.16.3
     *
     * @since 0.1.0
     */
    V1_16_2_3,

    /**
     * Version 1.16.4 - 1.16.5
     *
     * @since 0.1.0
     */
    V1_16_4_5,

    /**
     * Version 1.17.1
     *
     * @since 0.1.0
     */
    V1_17_1,

    /**
     * Version 1.18.2
     *
     * @since 0.1.0
     */
    V1_18_2,

    /**
     * Version 1.19.4
     *
     * @since 0.1.0
     */
    V1_19_4,

    /**
     * Version 1.20.0
     *
     * @since 0.1.0
     */
    V1_20_0,

    /**
     * Version 1.20.1
     *
     * @since 0.1.0
     */
    V1_20_1,

    /**
     * Version 1.20.2
     *
     * @since 0.1.0
     */
    V1_20_2,

    /**
     * Version 1.20.3 - 1.20.4
     *
     * @since 0.1.0
     */
    V1_20_3_4,

    /**
     * Version 1.20.5
     *
     * @since 0.1.0
     */
    V1_20_5,

    /**
     * Version 1.20.6
     *
     * @since 0.1.0
     */
    V1_20_6,

    /**
     * Version 1.21.0
     *
     * @since 0.1.0
     */
    V1_21_0,

    /**
     * Version 1.21.1
     *
     * @since 0.1.0
     */
    V1_21_1,

    /**
     * Version 1.21.2 - 1.21.3
     *
     * @since 0.1.0
     */
    V1_21_2_3,

    /**
     * Version 1.21.4
     *
     * @since 0.1.0
     */
    V1_21_4,

    /**
     * Version 1.21.5
     *
     * @since 0.1.0
     */
    V1_21_5,

    /**
     * Version 1.21.6 - 1.21.8
     *
     * @since 0.1.0
     */
    V1_21_6_8,

    /**
     * Version 1.21.9 - 1.21.10
     *
     * @since 0.1.0
     */
    V1_21_9_10,

    /**
     * Version 1.21.11
     *
     * @since 0.1.0
     */
    V1_21_11,

    /**
     * Version 26.1 or higher. Checked against 26.1.2, 26.2 and 26.3, which share the same NMS for these menus.
     *
     * @since 0.1.0
     */
    V26_1;

    /**
     * A collection of versions on which modern smithing tables are available.
     */
    private static final Collection<Version> MODERN_SMITHING_TABLE_VERSIONS = EnumSet.of(
            V1_19_4,
            V1_20_0, V1_20_1, V1_20_2, V1_20_3_4, V1_20_5, V1_20_6,
            V1_21_0, V1_21_1, V1_21_2_3, V1_21_4, V1_21_5, V1_21_6_8, V1_21_9_10, V1_21_11,
            V26_1
    );

    /**
     * A collection of versions on which legacy smithing tables ae available.
     */
    @NotNull
    private static final Collection<@NotNull Version> LEGACY_SMITHING_TABLE_VERSIONS = EnumSet.of(
            V1_16_1, V1_16_2_3, V1_16_4_5, V1_17_1, V1_18_2, V1_19_4
    );

    /**
     * Checks if this version is older than the provided version.
     *
     * @param version the version to check if it is newer
     * @return true if this version is older, false otherwise
     * @since 0.1.0
     */
    public boolean isOlderThan(@NotNull Version version) {
        return ordinal() < version.ordinal();
    }

    /**
     * Checks whether modern smithing tables exist on this version. Returns true if they do, otherwise false.
     *
     * @return true if modern smithing tables are available
     * @since 0.1.0
     */
    boolean existsModernSmithingTable() {
        return MODERN_SMITHING_TABLE_VERSIONS.contains(this);
    }

    /**
     * Checks whether legacy smithing tables exist on this version. Returns true if they do, otherwise false.
     *
     * @return true if legacy smithing tables are available
     * @since 0.1.0
     */
    boolean existsLegacySmithingTable() {
        return LEGACY_SMITHING_TABLE_VERSIONS.contains(this);
    }

    /**
     * Gets the version currently being used. If the used version is not supported, an
     * {@link UnsupportedVersionException} will be thrown.
     *
     * @return the version of the current instance
     * @since 0.1.0
     */
    @NotNull
    public static Version getVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0];

        if (version.indexOf('.') == 2)
            return V26_1; //this is a 26.1+ release, so it's V26.1

        switch (version) {
            case "1.14":
            case "1.14.1":
            case "1.14.2":
            case "1.14.3":
            case "1.14.4":
                return V1_14;
            case "1.15":
            case "1.15.1":
            case "1.15.2":
                return V1_15;
            case "1.16.1":
                return V1_16_1;
            case "1.16.2":
            case "1.16.3":
                return V1_16_2_3;
            case "1.16.4":
            case "1.16.5":
                return V1_16_4_5;
            case "1.17.1":
                return V1_17_1;
            case "1.18.2":
                return V1_18_2;
            case "1.19.4":
                return V1_19_4;
            case "1.20":
                return V1_20_0;
            case "1.20.1":
                return V1_20_1;
            case "1.20.2":
                return V1_20_2;
            case "1.20.3":
            case "1.20.4":
                return V1_20_3_4;
            case "1.20.5":
                return V1_20_5;
            case "1.20.6":
                return V1_20_6;
            case "1.21":
                return V1_21_0;
            case "1.21.1":
                return V1_21_1;
            case "1.21.2":
            case "1.21.3":
                return V1_21_2_3;
            case "1.21.4":
                return V1_21_4;
            case "1.21.5":
                return V1_21_5;
            case "1.21.6":
            case "1.21.7":
            case "1.21.8":
                return V1_21_6_8;
            case "1.21.9":
            case "1.21.10":
                return V1_21_9_10;
            case "1.21.11":
                return V1_21_11;
            default:
                throw new UnsupportedVersionException("The server version provided is not supported");
        }
    }
}
