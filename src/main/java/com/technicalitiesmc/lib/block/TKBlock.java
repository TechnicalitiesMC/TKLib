package com.technicalitiesmc.lib.block;

import com.technicalitiesmc.lib.util.TickingSide;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraftforge.fmllegacy.RegistryObject;

import javax.annotation.Nullable;

public class TKBlock extends Block {

    public TKBlock(Properties properties) {
        super(properties);
    }

    public static class WithEntity<TEntity extends TKBlockEntity> extends TKBlock implements EntityBlock {

        private final RegistryObject<BlockEntityType<TEntity>> entityType;
        private final BlockEntityTicker<TEntity> serverTicker, clientTicker;

        public WithEntity(Properties properties, RegistryObject<BlockEntityType<TEntity>> entityType, TickingSide tickingSide) {
            super(properties);
            this.entityType = entityType;
            this.serverTicker = tickingSide.isServer() ? this::tickServer : null;
            this.clientTicker = tickingSide.isClient() ? this::tickClient : null;
        }

        protected void tickServer(Level level, BlockPos pos, BlockState state, TEntity entity) {
        }

        protected void tickClient(Level level, BlockPos pos, BlockState state, TEntity entity) {
        }

        @Nullable
        @Override
        public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return entityType.get().create(pos, state);
        }

        @Nullable
        @Override
        public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
            return (BlockEntityTicker<T>) (level.isClientSide() ? clientTicker : serverTicker);
        }

        @Nullable
        @Override
        public <T1 extends BlockEntity> GameEventListener getListener(Level level, T1 entity) {
            return EntityBlock.super.getListener(level, entity);
        }

    }

}
