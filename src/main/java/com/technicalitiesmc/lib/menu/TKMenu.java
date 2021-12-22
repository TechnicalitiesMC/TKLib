package com.technicalitiesmc.lib.menu;

import com.technicalitiesmc.lib.inventory.ItemHolder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class TKMenu extends AbstractContainerMenu {

    private final List<Region> regions = new ArrayList<>();

    protected final Inventory playerInv;

    protected TKMenu(RegistryObject<? extends MenuType<?>> type, int id, Inventory playerInv) {
        super(type.get(), id);
        this.playerInv = playerInv;
    }

    protected final Region createRegion() {
        var region = new Region();
        regions.add(region);
        return region;
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

        public TKSlot addSlot(int x, int y, ItemHolder inventory, int slot) {
            return addSlots(x, y, 1, 1, inventory, slot).iterator().next();
        }

        public Iterable<TKSlot> addSlots(int x, int y, int rows, int columns, ItemHolder inventory, int start) {
            return addSlots(x, y, rows, columns, (x1, y1, id) -> new TKSlot(x1, y1, inventory, id + start));
        }

        public TKGhostSlot addGhostSlot(int x, int y, ItemHolder inventory, int slot, int limit) {
            return addGhostSlots(x, y, 1, 1, inventory, slot, limit).iterator().next();
        }

        public Iterable<TKGhostSlot> addGhostSlots(int x, int y, int rows, int columns, ItemHolder inventory, int start, int limit) {
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
            TKMenu.this.addSlot(slot);
            return slot;
        }

        public void addPlayerSlots(int x, int y, Inventory playerInv) {
            addSlots(x, y, 3, 9, playerInv, 9);
            addSlots(x, y + 58, 1, 9, playerInv, 0);
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

        public Reversed reversed() {
            return new Reversed();
        }

        public class Reversed implements RegionHandle {

            private Reversed() {
            }

            private Region getRegion() {
                return Region.this;
            }

        }

    }

    public interface RegionHandle {

    }

    @FunctionalInterface
    public interface SlotFactory<T extends Slot> {

        T createSlot(int x, int y, int index);

    }

}
