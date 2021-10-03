package com.technicalitiesmc.lib.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class SimpleItemHolder implements ItemHolder, INBTSerializable<CompoundTag> {

    private final NonNullList<ItemStack> items;
    @Nullable
    private final Runnable updateCallback;

    public SimpleItemHolder(int size, Runnable updateCallback) {
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.updateCallback = updateCallback;
    }

    public SimpleItemHolder(int size) {
        this(size, null);
    }

    @Override
    public int getSize() {
        return items.size();
    }


    @Override
    public ItemStack get(int slot) {
        return items.get(slot);
    }

    @Override
    public void set(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (updateCallback != null)
            updateCallback.run();
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
    public ItemHolder slice(int fromIncluding, int toExcluding) {
        return new Slice(fromIncluding, toExcluding - fromIncluding);
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        var slotList = new ListTag();
        for (var slot : items) {
            slotList.add(slot.serializeNBT());
        }
        tag.put("slots", slotList);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        var slotList = tag.getList("slots", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.of(slotList.getCompound(i)));
        }
    }

    private class Slice implements ItemHolder {

        private final int from, size;

        public Slice(int from, int size) {
            this.from = from;
            this.size = size;
        }

        @Override
        public int getSize() {
            return size;
        }

        @Override
        public ItemStack get(int slot) {
            return SimpleItemHolder.this.get(slot + from);
        }

        @Override
        public void set(int slot, ItemStack stack) {
            SimpleItemHolder.this.set(slot + from, stack);
        }

        @Override
        public void clear() {
            for (int i = 0; i < SimpleItemHolder.this.items.size(); i++) {
                SimpleItemHolder.this.items.set(i + from, ItemStack.EMPTY);
            }
            if (SimpleItemHolder.this.updateCallback != null)
                SimpleItemHolder.this.updateCallback.run();
        }

        @Override
        public ItemHolder slice(int fromIncluding, int toExcluding) {
            return new Slice(from + fromIncluding, toExcluding - fromIncluding);
        }

    }

}