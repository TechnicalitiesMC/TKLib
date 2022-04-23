package com.technicalitiesmc.lib.container.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A basic serializable {@link ItemContainer} implementation.
 */
public class SimpleItemContainer implements ItemContainer.Serializable {

    private final NonNullList<ItemStack> items;
    @Nullable
    private final Runnable updateCallback;

    public SimpleItemContainer(int size, Runnable updateCallback) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.updateCallback = updateCallback;
    }

    public SimpleItemContainer(int size) {
        this(size, null);
    }

    public SimpleItemContainer(NonNullList<ItemStack> stacks) {
        this(stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            items.set(i, stacks.get(i).copy());
        }
    }

    @Override
    public int size() {
        return items.size();
    }


    @Override
    public ItemStack get(int slot) {
        Objects.checkIndex(slot, size());
        return items.get(slot);
    }

    @Override
    public void set(int slot, ItemStack stack) {
        Objects.checkIndex(slot, size());
        items.set(slot, stack);
        if (updateCallback != null)
            updateCallback.run();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        Objects.checkIndex(slot, size());
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        if (updateCallback != null)
            updateCallback.run();
    }

    @Override
    public ItemContainer slice(int from, int to) {
        Objects.checkFromToIndex(from, to, size());
        return new Slice(from, to - from);
    }

    @Override
    public CompoundTag save() {
        var tag = new CompoundTag();
        var slotList = new ListTag();
        for (var slot : items) {
            slotList.add(slot.serializeNBT());
        }
        tag.put("slots", slotList);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        var slotList = tag.getList("slots", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.of(slotList.getCompound(i)));
        }
    }

    public static SimpleItemContainer from(NonNullList<ItemStack> items) {
        var inventory = new SimpleItemContainer(items.size());
        for (int i = 0; i < items.size(); i++) {
            inventory.set(i, items.get(i).copy());
        }
        return inventory;
    }

    private class Slice implements ItemContainer {

        private final int from, size;

        public Slice(int from, int size) {
            this.from = from;
            this.size = size;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public ItemStack get(int slot) {
            Objects.checkIndex(slot, size);
            return SimpleItemContainer.this.get(slot + from);
        }

        @Override
        public void set(int slot, ItemStack stack) {
            Objects.checkIndex(slot, size);
            SimpleItemContainer.this.set(slot + from, stack);
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            Objects.checkIndex(slot, size);
            return SimpleItemContainer.this.isValid(slot, stack);
        }

        @Override
        public void clear() {
            for (int i = 0; i < size; i++) {
                SimpleItemContainer.this.items.set(i + from, ItemStack.EMPTY);
            }
            if (SimpleItemContainer.this.updateCallback != null)
                SimpleItemContainer.this.updateCallback.run();
        }

        @Override
        public ItemContainer slice(int from, int to) {
            Objects.checkFromToIndex(from, to, size);
            return new Slice(this.from + from, to - from);
        }

    }

}