package com.technicalitiesmc.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockComponentDataContext {

    Level getLevel();

    BlockPos getBlockPos();

    BlockState getBlockState();

    void markUnsaved();

    void updateComparators();

}
