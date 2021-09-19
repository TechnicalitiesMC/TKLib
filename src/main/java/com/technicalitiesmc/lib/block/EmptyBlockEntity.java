package com.technicalitiesmc.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public final class EmptyBlockEntity extends TKBlockEntity {

    public EmptyBlockEntity(BlockEntityType<EmptyBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

}
