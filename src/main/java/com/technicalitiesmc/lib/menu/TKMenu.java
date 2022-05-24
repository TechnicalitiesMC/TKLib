package com.technicalitiesmc.lib.menu;

import com.technicalitiesmc.lib.container.item.ItemContainer;
import com.technicalitiesmc.lib.menu.slot.TKGhostSlot;
import com.technicalitiesmc.lib.menu.slot.TKSlot;
import com.technicalitiesmc.lib.util.value.Reference;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class TKMenu extends AbstractContainerMenu {

    private final List<Region> regions = new ArrayList<>();
    private final Int2ObjectMap<Region> slotRegions = new Int2ObjectOpenHashMap<>();
    private final List<MenuComponent> components = new ArrayList<>();
    private final Tracker dataTracker = new Tracker();

    protected final Inventory playerInv;
    private final ResourceLocation texture;
    private final int width, height;

    @Deprecated(forRemoval = true)
    protected TKMenu(RegistryObject<? extends MenuType<?>> type, int id, Inventory playerInv) {
        this(type, id, playerInv, null, 176, 166);
    }

    protected TKMenu(
            RegistryObject<? extends MenuType<?>> type, int id, Inventory playerInv,
            ResourceLocation texture, int width, int height
    ) {
        super(type.get(), id);
        this.playerInv = playerInv;
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    protected final Region createRegion() {
        var region = new Region();
        regions.add(region);
        return region;
    }

    @Contract(value = "_ -> param1", pure = true)
    protected final <T extends MenuComponent> T add(T component) {
        component.id = components.size();
        components.add(component);
        component.subscribe(dataTracker);
        return component;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        var region = slotRegions.get(slot);
        var s = slots.get(slot);
        if (region == null || !s.hasItem() || !s.mayPickup(player)) {
            return ItemStack.EMPTY;
        }
        var stack = s.getItem().copy();

        for (var handle : region.shiftTargets) {
            for (var target : handle.getSlots()) {
                if (moveItemStackTo(stack, target.index, target.index + 1, false) && stack.isEmpty()) {
                    s.set(stack);
                    return ItemStack.EMPTY;
                }
            }
        }
        s.set(stack);

        return ItemStack.EMPTY;
    }

    public void onMessage(int component, byte[] data) {
        var c = components.get(component);
        var buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
        c.onEvent(buf);
        buf.release();
    }

    public List<MenuComponent> components() {
        return components;
    }

    public final class Region implements RegionHandle {

        private final List<Slot> slots = new ArrayList<>();
        private final List<RegionHandle> shiftTargets = new ArrayList<>();

        private Region() {
        }

        public Slot addSlot(int x, int y, Container container, int slot) {
            return addSlots(x, y, 1, 1, container, slot).iterator().next();
        }

        public Iterable<Slot> addSlots(int x, int y, int rows, int columns, Container container, int start) {
            return addSlots(x, y, rows, columns, (x1, y1, id) -> new Slot(container, id + start, x1, y1));
        }

        public TKSlot addSlot(int x, int y, ItemContainer inventory, int slot) {
            return addSlots(x, y, 1, 1, inventory, slot).iterator().next();
        }

        public Iterable<TKSlot> addSlots(int x, int y, int rows, int columns, ItemContainer inventory, int start) {
            return addSlots(x, y, rows, columns, (x1, y1, id) -> new TKSlot(x1, y1, inventory, id + start));
        }

        public TKGhostSlot addGhostSlot(int x, int y, ItemContainer inventory, int slot, int limit) {
            return addGhostSlots(x, y, 1, 1, inventory, slot, limit).iterator().next();
        }

        public Iterable<TKGhostSlot> addGhostSlots(int x, int y, int rows, int columns, ItemContainer inventory, int start, int limit) {
            return addSlots(x, y, rows, columns, (x1, y1, id) -> new TKGhostSlot(x1, y1, inventory, id + start, limit));
        }

        public <T extends Slot> Iterable<T> addSlots(int x, int y, int rows, int columns, SlotFactory<T> slotFactory) {
            var firstIndex = slots.size();
            for (int j = 0; j < rows; j++) {
                for (int i = 0; i < columns; i++) {
                    addSlot(slotFactory.createSlot(x + i * 18, y + j * 18, i + (j * columns)));
                }
            }
            return (Iterable<T>) slots.subList(firstIndex, slots.size());
        }

        public <T extends Slot> T addSlot(T slot) {
            slots.add(slot);
            slotRegions.put(TKMenu.this.slots.size(), this);
            TKMenu.this.addSlot(slot);
            return slot;
        }

        public void addPlayerSlots(Inventory playerInv) {
            addPlayerSlots(playerInv, false);
        }

        public void addPlayerSlots(Inventory playerInv, boolean lockSelected) {
            addPlayerSlots((width - 160) / 2, height - 82, playerInv, lockSelected);
        }

        public void addPlayerSlots(int x, int y, Inventory playerInv) {
            addPlayerSlots(x, y, playerInv, false);
        }

        public void addPlayerSlots(int x, int y, Inventory playerInv, boolean lockSelected) {
            if (lockSelected) {
                addSlots(x, y, 3, 9, playerInv, 9);
                addSlots(x, y + 58, 1, 9, (x1, y1, id) -> new Slot(playerInv, id, x1, y1) {
                    @Override
                    public boolean mayPickup(Player player) {
                        return getSlotIndex() != ((Inventory) container).selected;
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return getSlotIndex() != ((Inventory) container).selected;
                    }
                });
            } else {
                addSlots(x, y, 3, 9, playerInv, 9);
                addSlots(x, y + 58, 1, 9, playerInv, 0);
            }
        }

        public void onChanged(Consumer<TKSlot> callback) {
            for (Slot slot : slots) {
                if (slot instanceof TKSlot s) {
                    s.onChanged(callback);
                }
            }
        }

        public void addShiftTargets(RegionHandle... regions) {
            shiftTargets.addAll(Arrays.asList(regions));
        }

        @Override
        public List<Slot> getSlots() {
            return slots;
        }

        public Reversed reversed() {
            return new Reversed();
        }

        public final class Reversed implements RegionHandle {

            private Reversed() {
            }

            @Override
            public List<Slot> getSlots() {
                var list = new ArrayList<>(Region.this.slots);
                Collections.reverse(list);
                return list;
            }

        }

    }

    public sealed interface RegionHandle {

        List<Slot> getSlots();

    }

    @FunctionalInterface
    public interface SlotFactory<T extends Slot> {

        T createSlot(int x, int y, int index);

    }

    private class Tracker implements MenuComponent.DataTracker {

        @Override
        public void trackInts(int[] array) {
            addDataSlots(new ContainerData() {
                @Override
                public int get(int i) {
                    return array[i];
                }

                @Override
                public void set(int i, int value) {
                    array[i] = value;
                }

                @Override
                public int getCount() {
                    return array.length;
                }
            });
        }

        @Override
        public void trackBoolean(Reference<Boolean> reference) {
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return reference.get() ? 1: 0;
                }

                @Override
                public void set(int value) {
                    reference.set(value != 0);
                }
            });
        }

        @Override
        public void trackInt(Reference<Integer> reference) {
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return reference.get();
                }

                @Override
                public void set(int value) {
                    reference.set(value);
                }
            });
        }

        @Override
        public void trackEnum(Reference<? extends Enum<?>> reference) {
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return reference.get().ordinal();
                }

                @Override
                public void set(int value) {
                    var type = reference.get().getClass();
                    ((Reference) reference).set(type.getEnumConstants()[value]);
                }
            });
        }

    }

}
