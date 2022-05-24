package com.technicalitiesmc.lib.inventory;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.List;

public class DroppedItemHandler implements IItemHandler {

    private final List<ItemEntity> entities;

    public DroppedItemHandler(Level level, AABB area) {
        this.entities = level.getEntitiesOfClass(ItemEntity.class, area);
    }

    @Override
    public int getSlots() {
        return entities.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        var entity = entities.get(slot);
        return entity.isAlive() ? entity.getItem() : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        var entity = entities.get(slot);
        if (!entity.isAlive()) {
            return ItemStack.EMPTY;
        }
        var stack = entity.getItem();
        var originalSize = stack.getCount();
        var split = stack.split(amount);
        if (simulate) {
            stack.setCount(originalSize);
        } else if (stack.isEmpty()) {
            entity.remove(Entity.RemovalReason.KILLED);
        }
        return split;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }

}
