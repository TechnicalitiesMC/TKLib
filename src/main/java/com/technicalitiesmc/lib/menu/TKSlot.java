package com.technicalitiesmc.lib.menu;

import com.technicalitiesmc.lib.container.item.ItemContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class TKSlot extends Slot implements LockableSlot, ColoredSlot {

    private static final Container EMPTY_CONTAINER = new SimpleContainer(0);

    private final ItemContainer inventory;
    private final int slot;
    private final Set<Consumer<TKSlot>> updateCallbacks = new HashSet<>();

    private boolean locked = false;
    private int color = 0;

    public TKSlot(int x, int y, ItemContainer inventory, int slot) {
        super(EMPTY_CONTAINER, 0, x, y);
        this.inventory = inventory;
        this.slot = slot;
    }

    public TKSlot lock() {
        locked = true;
        return this;
    }

    public TKSlot withColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public int getColor() {
        return color;
    }

    public void onChanged(Consumer<TKSlot> callback) {
        updateCallbacks.add(callback);
    }

    public int getIndex() {
        return slot;
    }

    @Override
    public ItemStack getItem() {
        return inventory.get(slot);
    }

    @Override
    public void set(ItemStack stack) {
        inventory.set(slot, stack);
        for (var consumer : updateCallbacks) {
            consumer.accept(this);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        for (var consumer : updateCallbacks) {
            consumer.accept(this);
        }
    }

    @Override
    public ItemStack remove(int amount) {
        var item = getItem();
        var split = item.split(amount);
        set(item);
        return split;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return other instanceof TKSlot s && s.inventory == inventory;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !isLocked() && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player player) {
        return !isLocked() && super.mayPickup(player);
    }

}
