package com.technicalitiesmc.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface ScrollingHandler {

    boolean scroll(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, double scrollDelta);

}
