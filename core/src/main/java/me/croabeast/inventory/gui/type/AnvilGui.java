package me.croabeast.inventory.gui.type;

import lombok.Getter;
import lombok.Setter;
import me.croabeast.inventory.HumanEntityCache;
import me.croabeast.inventory.nms.AnvilInventory;
import me.croabeast.inventory.adventure.TextHolder;
import me.croabeast.inventory.exception.XMLLoadException;
import me.croabeast.inventory.gui.GuiComponent;
import me.croabeast.inventory.gui.GuiItem;
import me.croabeast.inventory.gui.type.util.InventoryBased;
import me.croabeast.inventory.gui.type.util.NamedGui;
import me.croabeast.inventory.pane.Pane;
import me.croabeast.inventory.util.version.Version;
import me.croabeast.inventory.util.version.VersionMatcher;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Represents a gui in the form of an anvil
 *
 * @since 0.1.0
 */
public class AnvilGui extends NamedGui implements InventoryBased {

    /**
     * Called whenever the name input is changed.
     */
    @NotNull
    @Setter
    private Consumer<? super String> onNameInputChanged = (name) -> {};

    /**
     * Represents the gui component for the first item
     */
    @NotNull
    @Getter
    private GuiComponent firstItemComponent = new GuiComponent(1, 1);

    /**
     * Represents the gui component for the second item
     */
    @NotNull
    @Getter
    private GuiComponent secondItemComponent = new GuiComponent(1, 1);

    /**
     * Represents the gui component for the result
     */
    @NotNull
    @Getter
    private GuiComponent resultComponent = new GuiComponent(1, 1);

    /**
     * Represents the gui component for the player inventory
     */
    @NotNull
    @Getter
    private GuiComponent playerGuiComponent = new GuiComponent(9, 4);

    /**
     * An internal anvil inventory
     */
    @NotNull
    private final AnvilInventory anvilInventory = VersionMatcher.newAnvilInventory(Version.getVersion());

    /**
     * Constructs a new anvil gui
     *
     * @param title the title/name of this gui.
     * @since 0.1.0
     */
    public AnvilGui(@NotNull String title) {
        super(title);

        this.anvilInventory.subscribeToNameInputChanges(this::callOnRename);
    }

    /**
     * Constructs a new anvil gui
     *
     * @param title the title/name of this gui.
     * @since 0.1.0
     */
    public AnvilGui(@NotNull TextHolder title) {
        super(title);

        this.anvilInventory.subscribeToNameInputChanges(this::callOnRename);
    }

    /**
     * Constructs a new anvil gui for the given {@code plugin}.
     *
     * @param title the title/name of this gui.
     * @param plugin the owning plugin of this gui
     * @see #AnvilGui(String)
     * @since 0.1.0
     */
    public AnvilGui(@NotNull String title, @NotNull Plugin plugin) {
        super(title, plugin);

        this.anvilInventory.subscribeToNameInputChanges(this::callOnRename);
    }

    /**
     * Constructs a new anvil gui for the given {@code plugin}.
     *
     * @param title the title/name of this gui.
     * @param plugin the owning plugin of this gui
     * @see #AnvilGui(TextHolder)
     * @since 0.1.0
     */
    public AnvilGui(@NotNull TextHolder title, @NotNull Plugin plugin) {
        super(title, plugin);

        this.anvilInventory.subscribeToNameInputChanges(this::callOnRename);
    }

    @Override
    public void update() {
        super.updating = true;

        if (isDirty()) {
            Inventory oldInventory = this.inventory;
            this.inventory = createInventory();

            if (oldInventory != null) {
                for (HumanEntity viewer : new ArrayList<>(oldInventory.getViewers())) {
                    viewer.openInventory(this.inventory);
                }
            }

            markChanges();
        }

        getInventory().clear();

        getFirstItemComponent().display(getInventory(), 0);
        getSecondItemComponent().display(getInventory(), 1);
        getResultComponent().display(getInventory(), 2);

        getPlayerGuiComponent().display();

        HumanEntityCache humanEntityCache = getHumanEntityCache();

        for (HumanEntity viewer : getViewers()) {
            ItemStack cursor = viewer.getItemOnCursor();
            viewer.setItemOnCursor(new ItemStack(Material.AIR));

            populateBottomInventory(viewer);

            viewer.setItemOnCursor(cursor);
        }

        if (!super.updating)
            throw new AssertionError("Gui#isUpdating became false before Gui#update finished");

        super.updating = false;
    }

    @NotNull
    public Iterable<? extends GuiItem> getItems() {
        Collection<GuiItem> items = new HashSet<>();

        for (Pane pane : getFirstItemComponent().getPanes()) {
            items.addAll(pane.getItems());
        }

        for (Pane pane : getSecondItemComponent().getPanes()) {
            items.addAll(pane.getItems());
        }

        for (Pane pane : getResultComponent().getPanes()) {
            items.addAll(pane.getItems());
        }

        for (Pane pane : getPlayerGuiComponent().getPanes()) {
            items.addAll(pane.getItems());
        }

        return items;
    }

    @Override
    public void show(@NotNull HumanEntity humanEntity) {
        if (isDirty())
            update();

        populateBottomInventory(humanEntity);

        humanEntity.openInventory(getInventory());
    }

    /**
     * Populates the inventory of the {@link HumanEntity} if needed.
     *
     * @param humanEntity the human entity
     * @since 0.1.0
     */
    private void populateBottomInventory(@NotNull HumanEntity humanEntity) {
        if (getPlayerGuiComponent().hasItem()) {
            HumanEntityCache humanEntityCache = getHumanEntityCache();

            if (!humanEntityCache.contains(humanEntity))
                humanEntityCache.storeAndClear(humanEntity);

            getPlayerGuiComponent().placeItems(humanEntity.getInventory(), 0);
        }
    }

    @NotNull
    public AnvilGui copy() {
        AnvilGui gui = new AnvilGui(getTitleHolder(), super.plugin);

        gui.firstItemComponent = firstItemComponent.copy();
        gui.secondItemComponent = secondItemComponent.copy();
        gui.resultComponent = resultComponent.copy();
        gui.playerGuiComponent = playerGuiComponent.copy();

        gui.setOnTopClick(this.onTopClick);
        gui.setOnBottomClick(this.onBottomClick);
        gui.setOnGlobalClick(this.onGlobalClick);
        gui.setOnOutsideClick(this.onOutsideClick);
        gui.setOnClose(this.onClose);

        return gui;
    }

    @Override
    public void click(@NotNull InventoryClickEvent event) {
        int rawSlot = event.getRawSlot();

        if (rawSlot == 0) {
            getFirstItemComponent().click(this, event, 0);
        } else if (rawSlot == 1) {
            getSecondItemComponent().click(this, event, 0);
        } else if (rawSlot == 2) {
            getResultComponent().click(this, event, 0);
        } else {
            getPlayerGuiComponent().click(this, event, rawSlot - 3);
        }
    }

    @NotNull
    public Inventory getInventory() {
        if (this.inventory == null)
            this.inventory = createInventory();

        return inventory;
    }

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
        if (cost < 0){
            throw new IllegalArgumentException("Cost must be non-negative");
        }

        this.anvilInventory.setCost(cost);
    }

    @NotNull
    public Inventory createInventory() {
        Inventory inventory = this.anvilInventory.createInventory(getTitleHolder());

        addInventory(inventory, this);

        return inventory;
    }

    /**
     * Gets the rename text currently specified in the anvil.
     *
     * @return the rename text
     * @since 0.1.0
     * @see org.bukkit.inventory.AnvilInventory#getRenameText()
     */
    @NotNull
    public String getRenameText() {
        return anvilInventory.getRenameText();
    }

    @Override
    public boolean isPlayerInventoryUsed() {
        return getPlayerGuiComponent().hasItem();
    }

    @Override
    public int getViewerCount() {
        return getInventory().getViewers().size();
    }

    @NotNull
    public List<HumanEntity> getViewers() {
        return new ArrayList<>(getInventory().getViewers());
    }

    /**
     * Calls the consumer that was specified using {@link #setOnNameInputChanged(Consumer)}, so the consumer that should
     * be called whenever the rename input is changed. Catches and logs all exceptions the consumer might throw.
     *
     * @param newInput the new rename input
     * @since 0.1.0
     */
    private void callOnRename(@NotNull String newInput) {
        try {
            this.onNameInputChanged.accept(newInput);
        } catch (Throwable throwable) {
            String message = "Exception while handling onRename, newInput='" + newInput + "'";

            this.plugin.getLogger().log(Level.SEVERE, message, throwable);
        }
    }

    /**
     * Loads an anvil gui from an XML file.
     *
     * @param instance the instance on which to reference fields and methods
     * @param inputStream the input stream containing the XML data
     * @param plugin the plugin that will be the owner of the created gui
     * @return the loaded anvil gui
     * @see #load(Object, InputStream)
     * @since 0.1.0
     */
    @Nullable
    public static AnvilGui load(@NotNull Object instance, @NotNull InputStream inputStream, @NotNull Plugin plugin) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
            Element documentElement = document.getDocumentElement();

            documentElement.normalize();

            return load(instance, documentElement, plugin);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads an anvil gui from the specified element, applying code references to the provided instance.
     *
     * @param instance the instance on which to reference fields and methods
     * @param element the element to load the gui from
     * @param plugin the plugin that will own the created gui
     * @return the loaded anvil gui
     * @see #load(Object, Element)
     * @since 0.1.0
     */
    @NotNull
    public static AnvilGui load(@NotNull Object instance, @NotNull Element element, @NotNull Plugin plugin) {
        if (!element.hasAttribute("title"))
            throw new XMLLoadException("Provided XML element's gui tag doesn't have the mandatory title attribute set");

        AnvilGui anvilGui = new AnvilGui(element.getAttribute("title"), plugin);
        anvilGui.initializeOrThrow(instance, element);

        if (element.hasAttribute("populate"))
            return anvilGui;

        NodeList childNodes = element.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node item = childNodes.item(index);

            if (item.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element componentElement = (Element) item;

            if (!componentElement.getTagName().equalsIgnoreCase("component"))
                throw new XMLLoadException("Gui element contains non-component tags");

            if (!componentElement.hasAttribute("name"))
                throw new XMLLoadException("Component tag does not have a name specified");

            GuiComponent component;

            switch (componentElement.getAttribute("name")) {
                case "first-item":
                    component = anvilGui.getFirstItemComponent();
                    break;
                case "second-item":
                    component = anvilGui.getSecondItemComponent();
                    break;
                case "result":
                    component = anvilGui.getResultComponent();
                    break;
                case "player-inventory":
                    component = anvilGui.getPlayerGuiComponent();
                    break;
                default:
                    throw new XMLLoadException("Unknown component name");
            }

            component.load(instance, componentElement, plugin);
        }

        return anvilGui;
    }

    /**
     * Loads an anvil gui from an XML file.
     *
     * @param instance the instance on which to reference fields and methods
     * @param inputStream the input stream containing the XML data
     * @return the loaded anvil gui
     * @since 0.1.0
     */
    @Nullable
    public static AnvilGui load(@NotNull Object instance, @NotNull InputStream inputStream) {
        return load(instance, inputStream, JavaPlugin.getProvidingPlugin(AnvilGui.class));
    }

    /**
     * Loads an anvil gui from the specified element, applying code references to the provided instance.
     *
     * @param instance the instance on which to reference fields and methods
     * @param element the element to load the gui from
     * @return the loaded anvil gui
     * @since 0.1.0
     */
    @NotNull
    public static AnvilGui load(@NotNull Object instance, @NotNull Element element) {
        return load(instance, element, JavaPlugin.getProvidingPlugin(AnvilGui.class));
    }
}
