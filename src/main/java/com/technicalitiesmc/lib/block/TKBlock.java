package com.technicalitiesmc.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TKBlock extends Block implements BlockComponent.Context {

    private final List<BlockComponent> components = new ArrayList<>();

    public TKBlock(Properties properties) {
        super(properties);
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

    // Implementation

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        for (BlockComponent component : getComponents()) {
            var result = component.use(state, level, pos, player, hand, hit);
            if (result != InteractionResult.PASS)
                return result;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        for (BlockComponent component : getComponents()) {
            component.onRemove(state, level, pos, newState, moving);
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        var signal = 0;
        for (BlockComponent component : getComponents()) {
            signal = Math.max(signal, component.getAnalogOutputSignal(state, level, pos));
        }
        return signal;
    }

    public static class WithEntity extends TKBlock implements EntityBlock {

        final RegistryObject<BlockEntityType<TKBlockEntity>> entityType;
        final Map<String, BlockComponent.WithData> components = new HashMap<>();

        public WithEntity(Properties properties, RegistryObject<BlockEntityType<TKBlockEntity>> entityType) {
            super(properties);
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

}
