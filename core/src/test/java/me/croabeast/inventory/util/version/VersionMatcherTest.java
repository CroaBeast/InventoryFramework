package me.croabeast.inventory.util.version;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VersionMatcherTest {

    @Test
    void testEveryVersionHasItsImplementations() {
        for (Version version : Version.values()) {
            for (VersionMatcher.Type type : VersionMatcher.Type.values()) {
                if (type == VersionMatcher.Type.SMITHING_TABLE && !version.existsModernSmithingTable())
                    continue;

                if (type == VersionMatcher.Type.LEGACY_SMITHING_TABLE && !version.existsLegacySmithingTable())
                    continue;

                assertNotNull(VersionMatcher.findImplementation(type, version), type + " on " + version);
            }
        }
    }
}
