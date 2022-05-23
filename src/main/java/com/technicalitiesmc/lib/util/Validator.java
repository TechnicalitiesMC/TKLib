package com.technicalitiesmc.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface Validator {

    static Validator always() {
        return () -> true;
    }

    static Validator ensureBlock(BlockGetter level, BlockPos pos, Block block) {
        return () -> level.getBlockState(pos).is(block);
    }

    static Validator timeout(Level level, long timeout) {
        var targetTime = level.getGameTime() + timeout;
        return () -> level.getGameTime() < targetTime;
    }

    boolean isValid();

    default Validator and(Validator other) {
        return () -> isValid() && other.isValid();
    }

    default Validator andTimeout(Level level, long timeout) {
        return and(timeout(level, timeout));
    }

}
