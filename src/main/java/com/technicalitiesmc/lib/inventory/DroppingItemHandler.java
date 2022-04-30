package com.technicalitiesmc.lib.inventory;

import com.technicalitiesmc.lib.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class DroppingItemHandler implements IItemHandler {

    private final Level level;
    private final BlockPos pos;
    private final Direction side;
    private final ItemEntity[] entities;

    public DroppingItemHandler(Level level, BlockPos pos, Direction side, int size) {
        this.level = level;
        this.pos = pos;
        this.side = side;
        this.entities = new ItemEntity[size];
    }

    @Override
    public int getSlots() {
        return entities.length;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (entities[slot] != null) {
            return stack;
        }
        if (!simulate) {
            entities[slot] = Utils.dropItemOutwardsAtSide(level, pos, side, stack);
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

}
