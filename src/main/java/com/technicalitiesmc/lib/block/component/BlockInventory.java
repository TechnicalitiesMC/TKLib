package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.block.BlockComponentData;
import com.technicalitiesmc.lib.inventory.ItemHolder;
import com.technicalitiesmc.lib.inventory.SerializableItemHolder;
import com.technicalitiesmc.lib.inventory.SimpleItemHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;

public class BlockInventory extends BlockComponent.WithData<BlockInventory.Data> {

    @CapabilityInject(IItemHandler.class)
    private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY;

    private final boolean shouldDropItemsOnBreak;

    public BlockInventory(Context context, int slots, Flag... flags) {
        this(context, slots, flags.length != 0 ? EnumSet.copyOf(Arrays.asList(flags)) : EnumSet.noneOf(Flag.class));
    }

    public BlockInventory(Context context, int slots, EnumSet<Flag> flags) {
        this(context, callback -> new SimpleItemHolder(slots, callback), flags);
    }

    public BlockInventory(Context context, InventoryFactory inventoryFactory, Flag... flags) {
        this(context, inventoryFactory, EnumSet.copyOf(Arrays.asList(flags)));
    }

    public BlockInventory(Context context, InventoryFactory inventoryFactory, EnumSet<Flag> flags) {
        super(context, ctx -> new Data(ctx, inventoryFactory, flags));
        this.shouldDropItemsOnBreak = flags.contains(Flag.DROP_ON_BREAK);
    }

    // API

    public ItemHolder at(BlockGetter level, BlockPos pos, BlockState state) {
        return getData(level, pos, state).inventory;
    }

    // Impl

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (shouldDropItemsOnBreak && !state.is(newState.getBlock())) {
            var data = getData(level, pos, state);
            Containers.dropContents(level, pos, data.inventory.asVanillaContainer());
            level.updateNeighbourForOutputSignal(pos, getBlock());
        }
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        var data = getData(level, pos, state);
        return AbstractContainerMenu.getRedstoneSignalFromContainer(data.inventory.asVanillaContainer());
    }

    public static class Data extends BlockComponentData {

        private final boolean shouldUpdateComparators, shouldExposeCaps;

        private final SerializableItemHolder inventory;
        private final LazyOptional<IItemHandler> itemHandler;

        private Data(Context context, InventoryFactory inventoryFactory, EnumSet<Flag> flags) {
            super(context);
            this.shouldUpdateComparators = flags.contains(Flag.COMPARATOR_OUTPUT);
            this.shouldExposeCaps = flags.contains(Flag.EXPOSE_ITEM_HANDLER);

            this.inventory = inventoryFactory.createInventory(this::onInventoryUpdate);
            this.itemHandler = shouldExposeCaps ? LazyOptional.of(inventory::asItemHandler) : LazyOptional.empty();
        }

        private void onInventoryUpdate() {
            markUnsaved();
            if (shouldUpdateComparators) {
                updateComparators();
            }
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (shouldExposeCaps && cap == ITEM_HANDLER_CAPABILITY) {
                return itemHandler.cast();
            }
            return super.getCapability(cap, side);
        }

        @Override
        public void invalidateCaps() {
            itemHandler.invalidate();
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.put("inventory", inventory.serializeNBT());
            return tag;
        }

        @Override
        public void load(CompoundTag tag) {
            inventory.deserializeNBT(tag.getCompound("inventory"));
        }

    }

    public enum Flag {
        COMPARATOR_OUTPUT,
        DROP_ON_BREAK,
        EXPOSE_ITEM_HANDLER
    }

    @FunctionalInterface
    public interface InventoryFactory {

        SerializableItemHolder createInventory(Runnable updateCallback);

    }

}
