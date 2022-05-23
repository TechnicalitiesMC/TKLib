package com.technicalitiesmc.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TKBlock extends Block implements BlockComponent.Context {

    private static final ThreadLocal<Property<?>[]> STATE_PROPERTIES = new ThreadLocal<>();
    private static Properties cacheStateProperties(Properties properties, Property<?>[] stateProperties) {
        STATE_PROPERTIES.set(stateProperties);
        return properties;
    }

    private final List<BlockComponent> components = new ArrayList<>();

    public TKBlock(Properties properties, Property<?>... stateProperties) {
        super(cacheStateProperties(properties, stateProperties));
    }

    // Component management and initialization

    final <T extends BlockComponent> T doAddComponent(BlockComponent.Constructor<T> constructor) {
        var component = constructor.create(this);
        components.add(component);
        return component;
    }

    protected final <T extends BlockComponent.WithoutData> T addComponent(BlockComponent.Constructor<T> constructor) {
        return doAddComponent(constructor);
    }

    protected final Iterable<BlockComponent> getComponents() {
        return components;
    }

    // Helpers

    @Override
    public final TKBlock getBlock() {
        return this;
    }

    @Nullable
    public Object getInterface(Class<?> itf) {
        return getInterfaceFromComponents(itf);
    }

    @Nullable
    protected final Object getInterfaceFromComponents(Class<?> itf) {
        for (var component : components) {
            var impl = component.getInterface(itf);
            if (impl != null) {
                return impl;
            }
        }
        return null;
    }

    public final Component getDefaultContainerName() {
        var name = getRegistryName();
        return new TranslatableComponent("container." + name.getNamespace() + "." + name.getPath());
    }

    protected final InteractionResult openMenu(Level level, Player player, MenuConstructor constructor) {
        return openMenu(level, player, constructor, getDefaultContainerName());
    }

    protected final InteractionResult openMenu(Level level, Player player, MenuConstructor constructor, Component title) {
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider(constructor, title));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    protected final <T extends Comparable<T>> void setDefault(Property<T> property, T value) {
        registerDefaultState(defaultBlockState().setValue(property, value));
    }

    // Implementation

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE_PROPERTIES.get());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var state = super.getStateForPlacement(context);
        for (var component : getComponents()) {
            state = component.getStateForPlacement(context, state);
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction side, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        state = super.updateShape(state, side, neighborState, level, pos, neighborPos);
        for (var component : getComponents()) {
            state = component.updateShape(state, side, neighborState, level, pos, neighborPos);
        }
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, neighborPos, moving);
        for (var component : getComponents()) {
            component.neighborChanged(state, level, pos, block, neighborPos, moving);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        for (var component : getComponents()) {
            var result = component.use(state, level, pos, player, hand, hit);
            if (result != InteractionResult.PASS) {
                return result;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState prevState, boolean moving) {
        for (var component : getComponents()) {
            component.onPlace(state, level, pos, prevState, moving);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack item) {
        for (var component : getComponents()) {
            component.setPlacedBy(level, pos, state, entity, item);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        for (var component : getComponents()) {
            component.onRemove(state, level, pos, newState, moving);
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        var signal = 0;
        for (var component : getComponents()) {
            signal = Math.max(signal, component.getAnalogOutputSignal(state, level, pos));
        }
        return signal;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        for (var component : getComponents()) {
            var shape = component.getShape(state, level, pos, context);
            if (shape != null) {
                return shape;
            }
        }
        return super.getShape(state, level, pos, context);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        for (var component : getComponents()) {
            state = component.rotate(state, rotation);
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
        for (var component : getComponents()) {
            state = component.rotate(state, level, pos, rotation);
        }
        return state;
    }

    public static class WithEntity extends TKBlock implements EntityBlock {

        final RegistryObject<BlockEntityType<TKBlockEntity>> entityType;
        final Map<String, BlockComponent.WithData<?>> components = new HashMap<>();

        public WithEntity(Properties properties, RegistryObject<BlockEntityType<TKBlockEntity>> entityType, Property<?>... stateProperties) {
            super(properties, stateProperties);
            this.entityType = entityType;
        }

        protected final <T extends BlockComponent.WithData<?>> T addComponent(String name, BlockComponent.Constructor<T> constructor) {
            var component = doAddComponent(constructor);
            components.put(name, component);
            return component;
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return entityType.get().create(pos, state);
        }

    }

    // Static helpers

    @Nullable
    public static <T> T getInterface(Block block, Class<T> itf) {
        if (block instanceof TKBlock tkb) {
            return (T) tkb.getInterface(itf);
        }
        if (itf.isAssignableFrom(block.getClass())) {
            return (T) block;
        }
        return null;
    }

}
