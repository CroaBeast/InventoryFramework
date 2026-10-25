package me.croabeast.inventory.gui.type.util;

import lombok.Getter;
import me.croabeast.inventory.adventure.StringHolder;
import me.croabeast.inventory.adventure.TextHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class NamedGui extends Gui {

    /**
     * The title of this gui
     */
    @NotNull
    private TextHolder titleHolder;

    /**
     * Constructs a new gui with a title
     *
     * @param title the title/name of this gui
     * @since 0.1.0
     */
    public NamedGui(@NotNull String title) {
        this(StringHolder.of(title));
    }

    /**
     * Constructs a new gui with a title
     *
     * @param title the title/name of this gui
     * @since 0.1.0
     */
    public NamedGui(@NotNull TextHolder title) {
        this(title, JavaPlugin.getProvidingPlugin(NamedGui.class));
    }

    /**
     * Constructs a new gui with a title for the given {@code plugin}.
     *
     * @param title the title/name of this gui
     * @param plugin the owning plugin of this gui
     * @see #NamedGui(String)
     * @since 0.1.0
     */
    public NamedGui(@NotNull String title, @NotNull Plugin plugin) {
        this(StringHolder.of(title), plugin);
    }

    /**
     * Constructs a new gui with a title for the given {@code plugin}.
     *
     * @param title the title/name of this gui
     * @param plugin the owning plugin of this gui
     * @see #NamedGui(TextHolder)
     * @since 0.1.0
     */
    public NamedGui(@NotNull TextHolder title, @NotNull Plugin plugin) {
        super(plugin);
        this.titleHolder = title;
    }

    /**
     * Sets the title for this inventory.
     *
     * @param title the title
     */
    public void setTitle(@NotNull String title) {
        setTitle(StringHolder.of(title));
    }

    /**
     * Sets the title for this inventory.
     *
     * @param title the title
     * @since 0.1.0
     */
    public void setTitle(@NotNull TextHolder title) {
        this.titleHolder = title;
        this.dirty = true;
    }

    /**
     * Returns the title of this gui as a legacy string.
     *
     * @return the title
     * @since 0.1.0
     */
    @NotNull
    public String getTitle() {
        return titleHolder.asLegacyString();
    }
}
