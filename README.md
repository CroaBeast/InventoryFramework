# InventoryFramework

> **Hard fork of [stefvanschie/IF](https://github.com/stefvanschie/IF).** This project started from IF 0.12.2 and is
> developed independently: it uses its own coordinates and packages, and it is not API-compatible with the original.
> All credit for the original framework goes to Stef van Schie and the IF contributors.

*This framework works for Minecraft versions 1.14-1.16, 1.17.1, 1.18.2, 1.19.4, 1.20-1.21, and 26.1-26.3*

An inventory framework for managing GUIs

This framework is based on a pane principle. This means that the GUI is divided into different types of panes which all behave differently. A GUI consists of multiple panes which can interact with each other.

Next to those panes, GUIs can also be created from XML files by simple loading them in. This allows for easy GUI creation with little code.

## What changed from IF
The fork is being restructured and improved, so the API can still change until 1.0.0. So far:

- **Build:** Gradle (Kotlin DSL) instead of Maven. Every NMS module compiles against the Spigot server installed by
  BuildTools; 1.17 - 1.21.x are compiled against Mojang names and remapped to Spigot names on build.
- **Coordinates and packages:** published as `me.croabeast.inventory:framework`, with all classes under
  `me.croabeast.inventory`. Versioning restarted at 0.1.0.
- **Older versions are back:** 1.14.x, 1.15.x, 1.16.1, 1.16.2 - 1.16.3 and 1.16.4 are supported again. 1.14 and 1.15
  were ported to the current NMS layer, and the loom GUI now works on every supported version.
- **Simpler NMS layer:** the per-block abstract classes were merged into a single `CustomInventory` interface (plus
  `AnvilInventory` and `MerchantInventory`, which keep their extra state), and `VersionMatcher` resolves every
  inventory through `newInventory(VersionMatcher.Type, Version)`.
- **Less boilerplate:** accessors and plain constructors use Lombok, alongside the JetBrains nullability annotations.
- **Module layout:** `core` (the framework itself), `adventure` (text holders) and `nms` (one module per Minecraft
  version). `InventoryView`, a class before 1.21 and an interface since, is handled in `core` through method handles
  instead of two separately compiled modules.
- **Builders:** `me.croabeast.inventory.builder` has fluent builders for items (`ItemBuilder`), paginated chests
  (`ChestBuilder`), toggle buttons (`ToggleBuilder`), anvil text prompts (`AnvilInputBuilder`) and confirmation
  dialogs (`ConfirmBuilder`).

## Releases
Every version is published as a [GitHub release](https://github.com/CroaBeast/InventoryFramework/releases) with the
shaded jar attached. A release covers every commit from its version bump onwards, and it is rebuilt with the new
commits until the next version is bumped.

## Gradle dependency
InventoryFramework is built with Gradle (Kotlin DSL). Install it into your local Maven repository with `./gradlew publishToMavenLocal`
(see [Building from source](#building-from-source)), then add it to your `build.gradle.kts`:
```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("me.croabeast.inventory:framework:0.1.0")
}
```
In order to include the project in your own project, you will need to use the `shadowJar` plugin and relocate the
framework to your own namespace, with [YOUR PACKAGE] being the top-level package of your project:
```kotlin
plugins {
    id("com.gradleup.shadow") version "9.6.1"
}

tasks.shadowJar {
    relocate("me.croabeast.inventory", "[YOUR PACKAGE].inventory")
}
```

If your plugin is a Paper plugin targeting a Minecraft version before 26.1, you must set the `paperweight-mappings-namespace` in `META-INF/MANIFEST.MF` to `spigot`:
```kotlin
tasks.shadowJar {
    relocate("me.croabeast.inventory", "[YOUR PACKAGE].inventory")

    manifest {
        attributes("paperweight-mappings-namespace" to "spigot")
    }
}
```

## Maven dependency
```XML
<dependency>
    <groupId>me.croabeast.inventory</groupId>
    <artifactId>framework</artifactId>
    <version>0.1.0</version>
</dependency>
```
Shade it with the `maven-shade-plugin` and relocate `me.croabeast.inventory` to `[YOUR PACKAGE].inventory`.

## Dependency via plugin.yml
InventoryFramework does **not** support declaring the dependency via the libraries section in the plugin.yml. Please make use of a build tool as described above to use it as a dependency.

## Building from source
This project relies on NMS, for which the dependencies are not available online. Every NMS module compiles against a
Spigot server installed into your local Maven repository by [BuildTools](https://www.spigotmc.org/wiki/buildtools/).
The Spigot version of each module is listed in `nmsVersions` in `build.gradle.kts`.

Run these in an empty folder. Each Minecraft version needs a Java version BuildTools accepts for it.
```
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar -O BuildTools.jar

# Java 11
java -jar BuildTools.jar --rev 1.14.4
java -jar BuildTools.jar --rev 1.15.2
java -jar BuildTools.jar --rev 1.16.1
java -jar BuildTools.jar --rev 1.16.3
java -jar BuildTools.jar --rev 1.16.5

# --rev 1.20, 1.20.5 and 1.21 resolve to 1.20.1, 1.20.6 and 1.21.1; build numbers pin the original releases
# Java 17
java -jar BuildTools.jar --rev 1.17.1 --remapped
java -jar BuildTools.jar --rev 1.18.2 --remapped
java -jar BuildTools.jar --rev 1.19.4 --remapped
java -jar BuildTools.jar --rev 3798 --remapped   # 1.20
java -jar BuildTools.jar --rev 1.20.1 --remapped
java -jar BuildTools.jar --rev 1.20.2 --remapped
java -jar BuildTools.jar --rev 1.20.4 --remapped

# Java 21
java -jar BuildTools.jar --rev 4132 --remapped   # 1.20.5
java -jar BuildTools.jar --rev 1.20.6 --remapped
java -jar BuildTools.jar --rev 4289 --remapped   # 1.21
java -jar BuildTools.jar --rev 1.21.1 --remapped
java -jar BuildTools.jar --rev 1.21.3 --remapped
java -jar BuildTools.jar --rev 1.21.4 --remapped
java -jar BuildTools.jar --rev 1.21.5 --remapped
java -jar BuildTools.jar --rev 1.21.8 --remapped
java -jar BuildTools.jar --rev 1.21.10 --remapped
java -jar BuildTools.jar --rev 1.21.11 --remapped

# Java 25
java -jar BuildTools.jar --rev 26.1.2
```

Your environment is now set up correctly. To create a build, run the following inside the root folder of the project.
```
./gradlew build
```
Your build is now available as `core/build/libs/InventoryFramework-0.1.0.jar`.

## Adventure support

InventoryFramework supports [Adventure](https://github.com/KyoriPowered/adventure), but does not shade it in itself.
The use of Adventure `Component`s instead of legacy `String`s is completely optional.
If you do not wish to use Adventure you can safely ignore all `TextHolder` related methods.

### What is Adventure?

Adventure is a library that adds proper modern text support to Minecraft.
Modern text is represented using bungee-chat and `BaseComponent` instances in Spigot.
Adventure is an alternative to bungee-chat and offers more features.

### Using Adventure on 1.16.5+ Paper

You don't need to import/shade anything for Adventure support in this case!

*Note: Paper only supports Adventure on build 473 and above. If you aren't running months old builds, then you are fine.*

### Using Adventure on Spigot and older Paper

On Spigot Adventure isn't included in the server, therefore you have to shade and relocate it yourself.
The following dependencies need to be imported and shaded:
- adventure-api
- adventure-platform-bukkit

Please consult the [Adventure documentation](https://docs.adventure.kyori.net/) for more information.

### How to use Adventure `Component`s

Example of migration from legacy `String` to Adventure `Component`:
 - legacy: `namedGui.setTitle("My Title!");`
 - Adventure: `namedGui.setTitle(ComponentHolder.of(Component.text("My Title!")));`
 - Plain text as a component: `namedGui.setTitle(ComponentHolder.fromString("My Title!"));`. The string is used as it
   is, without parsing color codes or MiniMessage tags.

We apologize for the boilerplate (the `ComponentHolder.of(...)` call), but that was the only way to not make InventoryFramework hard-depend on Adventure.

Full Adventure support is only achieved when your server natively supports Adventure (it is running Paper) and your plugin depends on Paper (instead of Spigot).
In other words, you won't benefit from Adventure as much if you use Spigot instead of Paper.
This is because when Adventure is relocated we have to convert everything back to legacy `String`s before passing them to the Bukkit API.

---

NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.
