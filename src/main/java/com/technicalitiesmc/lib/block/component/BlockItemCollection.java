package com.technicalitiesmc.lib.block.component;

import com.google.common.base.Suppliers;
import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.block.BlockComponentData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
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
import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Supplier;

public class BlockItemCollection<T extends Collection<ItemStack>> extends BlockComponent.WithData<BlockItemCollection.Data<T>> {

    private static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private static final Runnable EMPTY_RUNNABLE = () -> {};

    private final CollectionFactory<T> collectionFactory;
    private final int capacity;
    private final ItemHandlerMapper<T> exposedItemHandlerMapper;
    private final boolean shouldDropItemsOnBreak, shouldOutputToComparators, shouldExposeCaps;

    private BlockItemCollection(Context context, CollectionFactory<T> collectionFactory, int capacity,
                                @Nullable ItemHandlerMapper<T> exposedItemHandlerMapper, EnumSet<Flag> flags) {
        super(context, Data::new);
        this.collectionFactory = collectionFactory;
        this.capacity = capacity;
        this.exposedItemHandlerMapper = exposedItemHandlerMapper;
        this.shouldDropItemsOnBreak = flags.contains(Flag.DROP_ON_BREAK);
        this.shouldOutputToComparators = flags.contains(Flag.COMPARATOR_OUTPUT);
        this.shouldExposeCaps = flags.contains(Flag.EXPOSE_ITEM_HANDLER) && exposedItemHandlerMapper != null;
    }

    // Static construction

    public static <T extends Collection<ItemStack>> Constructor<BlockItemCollection<T>> of(
            CollectionFactory<T> collectionFactory,
            int capacity,
            Flag... flags
    ) {
        return of(collectionFactory, capacity, null, flags);
    }

    public static <T extends Collection<ItemStack>> Constructor<BlockItemCollection<T>> of(
            CollectionFactory<T> collectionFactory,
            int capacity,
            @Nullable ItemHandlerMapper<T> exposedItemHandlerMapper,
            Flag... flags
    ) {
        return context -> new BlockItemCollection<>(
                context,
                collectionFactory,
                capacity,
                exposedItemHandlerMapper,
                flags.length != 0 ? EnumSet.copyOf(Arrays.asList(flags)) : EnumSet.noneOf(Flag.class)
        );
    }

    // API

    public int capacity() {
        return capacity;
    }

    @Nullable
    public T at(BlockGetter level, BlockPos pos, BlockState state) {
        var data = getData(level, pos, state);
        return data == null ? null : data.collection;
    }

    @Nullable
    public IItemHandler itemHandlerAt(BlockGetter level, BlockPos pos, BlockState state) {
        var data = getData(level, pos, state);
        return data == null ? null : data.itemHandler.get();
    }

    // Impl

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!moving && shouldDropItemsOnBreak && !state.is(newState.getBlock())) {
            var data = getData(level, pos, state);
            if (data != null) {
                var list = NonNullList.<ItemStack>create();
                list.addAll(data.collection);
                Containers.dropContents(level, pos, list);
            }
            level.updateNeighbourForOutputSignal(pos, getBlock());
        }
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (shouldOutputToComparators) {
            var data = getData(level, pos, state);
            if (data != null) {
                return data.collection.size() * 15 / capacity;
            }
        }
        return 0;
    }

    public static class Data<T extends Collection<ItemStack>> extends BlockComponentData<BlockItemCollection<T>> {

        private final T collection;
        private final Supplier<IItemHandler> itemHandler;
        private final LazyOptional<IItemHandler> lazyItemHandler;

        private Data(Context context) {
            super(context);
            var component = getComponent();
            this.collection = component.collectionFactory.createCollection();
            this.itemHandler = Suppliers.memoize(() -> component.exposedItemHandlerMapper.asItemHandler(
                    collection, component.capacity, this::onCollectionChanged
            ));
            this.lazyItemHandler = component.shouldExposeCaps ? LazyOptional.of(itemHandler::get) : LazyOptional.empty();
        }

        private void onCollectionChanged() {
            markUnsaved();
            if (getComponent().shouldOutputToComparators) {
                updateComparators();
            }
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ITEM_HANDLER_CAPABILITY) {
                return lazyItemHandler.cast();
            }
            return super.getCapability(cap, side);
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            var tagList = new ListTag();
            for (var stack : collection) {
                tagList.add(stack.save(new CompoundTag()));
            }
            tag.put("inventory", tagList);
            return tag;
        }

        @Override
        public void load(CompoundTag tag) {
            var tagList = tag.getList("inventory", Tag.TAG_COMPOUND);
            collection.clear();
            for (var itemTag : tagList) {
                collection.add(ItemStack.of((CompoundTag) itemTag));
            }
        }

    }

    public enum Flag {
        COMPARATOR_OUTPUT,
        DROP_ON_BREAK,
        EXPOSE_ITEM_HANDLER
    }

    @FunctionalInterface
    public interface CollectionFactory<T extends Collection<ItemStack>> {

        T createCollection();

    }
    
    @FunctionalInterface
    public interface ItemHandlerMapper<T extends Collection<ItemStack>> {
        
        IItemHandler asItemHandler(T collection, int capacity, Runnable saveCallback);
        
    }

}
