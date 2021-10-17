package com.technicalitiesmc.lib.block.multipart;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public interface Multipart {

    @Nullable
    BlockSlot getSlot(BlockState state);

    default VoxelShape getIntersectionShape(BlockState state, Level level, BlockPos pos) {
        return state.getCollisionShape(level, pos);
    }

    default IntersectionTestResult testIntersection(BlockState state, Level level, BlockPos pos, BlockState other) {
        return IntersectionTestResult.PASS;
    }

    static BlockState getBlockState(BlockGetter level, BlockPos pos, @Nullable BlockSlot slot) {
        return level.getBlockState(pos);
    }

    @Nullable
    static BlockEntity getBlockEntity(BlockGetter level, BlockPos pos, @Nullable BlockSlot slot) {
        return level.getBlockEntity(pos);
    }

    @Nullable
    static BlockEntity getBlockEntity(BlockGetter level, BlockPos pos, BlockState state) {
        return level.getBlockEntity(pos);
    }

}
