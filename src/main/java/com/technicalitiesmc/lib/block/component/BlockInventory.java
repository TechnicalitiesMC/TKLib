package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.block.BlockComponentData;
import com.technicalitiesmc.lib.container.item.ItemContainer;
import com.technicalitiesmc.lib.container.item.SimpleItemContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;

public class BlockInventory extends BlockComponent.WithData<BlockInventory.Data> {

    private static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static final UpdateCallback EMPTY_CALLBACK = ($, $$, $$$, $$$$) -> {};

    private final InventoryFactory inventoryFactory;
    private final boolean shouldDropItemsOnBreak, shouldOutputToComparators, shouldExposeCaps, shouldSendToClients;
    private final UpdateCallback updateCallback;

    private BlockInventory(Context context, InventoryFactory inventoryFactory, EnumSet<Flag> flags, @Nullable UpdateCallback updateCallback) {
        super(context, Data::new);
        this.inventoryFactory = inventoryFactory;
        this.shouldDropItemsOnBreak = flags.contains(Flag.DROP_ON_BREAK);
        this.shouldOutputToComparators = flags.contains(Flag.COMPARATOR_OUTPUT);
        this.shouldExposeCaps = flags.contains(Flag.EXPOSE_ITEM_HANDLER);
        this.shouldSendToClients = flags.contains(Flag.SEND_TO_CLIENTS);
        this.updateCallback = updateCallback != null ? updateCallback : EMPTY_CALLBACK;
    }

    // Static construction

    public static BlockComponent.Constructor<BlockInventory> of(int slots, @Nullable UpdateCallback updateCallback, Flag... flags) {
        return of(callback -> new SimpleItemContainer(slots, callback), updateCallback, flags);
    }

    public static BlockComponent.Constructor<BlockInventory> of(InventoryFactory inventoryFactory, @Nullable UpdateCallback updateCallback, Flag... flags) {
        return context -> new BlockInventory(
                context,
                inventoryFactory,
                flags.length != 0 ? EnumSet.copyOf(Arrays.asList(flags)) : EnumSet.noneOf(Flag.class),
                updateCallback
        );
    }

    // API

    @Nullable
    public ItemContainer at(BlockGetter level, BlockPos pos, BlockState state) {
        var data = getData(level, pos, state);
        return data == null ? null : data.inventory;
    }

    // Impl

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!moving && shouldDropItemsOnBreak && !state.is(newState.getBlock())) {
            var data = getData(level, pos, state);
            if (data != null) {
                Containers.dropContents(level, pos, data.inventory.asVanillaContainer());
            }
            level.updateNeighbourForOutputSignal(pos, getBlock());
        }
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (shouldOutputToComparators) {
            var data = getData(level, pos, state);
            return data == null ? 0 : AbstractContainerMenu.getRedstoneSignalFromContainer(data.inventory.asVanillaContainer());
        }
        return 0;
    }

    public static class Data extends BlockComponentData<BlockInventory> {

        private final ItemContainer.Serializable inventory;
        private final LazyOptional<IItemHandler> itemHandler;

        private Data(Context context) {
            super(context);
            var component = getComponent();
            this.inventory = component.inventoryFactory.createInventory(this::onInventoryUpdate);
            this.itemHandler = component.shouldExposeCaps ? LazyOptional.of(inventory::asItemHandler) : LazyOptional.empty();
        }

        private void onInventoryUpdate() {
            var component = getComponent();
            component.updateCallback.onUpdated(getLevel(), getBlockPos(), getBlockState(), inventory);
            markUnsaved();
            if (component.shouldOutputToComparators) {
                updateComparators();
            }
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ITEM_HANDLER_CAPABILITY) {
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
            tag.put("inventory", inventory.save());
            return tag;
        }

        @Override
        public void load(CompoundTag tag) {
            inventory.load(tag.getCompound("inventory"));
        }

        @Override
        public CompoundTag saveDescription(CompoundTag tag) {
            if (getComponent().shouldSendToClients) {
                tag.put("inventory", inventory.save());
            }
            return tag;
        }

        @Override
        public void loadDescription(CompoundTag tag) {
            if (getComponent().shouldSendToClients) {
                inventory.load(tag.getCompound("inventory"));
            }
        }

    }

    public enum Flag {
        COMPARATOR_OUTPUT,
        DROP_ON_BREAK,
        EXPOSE_ITEM_HANDLER,
        SEND_TO_CLIENTS
    }

    @FunctionalInterface
    public interface InventoryFactory {

        ItemContainer.Serializable createInventory(Runnable updateCallback);

    }

    @FunctionalInterface
    public interface UpdateCallback {

        void onUpdated(Level level, BlockPos pos, BlockState state, ItemContainer inventory);

    }

}
