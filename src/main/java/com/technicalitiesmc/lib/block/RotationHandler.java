package com.technicalitiesmc.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public interface RotationHandler {

    boolean rotate(BlockState state, Level level, BlockPos pos, Direction.Axis axis, Rotation rotation);

}
