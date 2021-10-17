package com.technicalitiesmc.lib.block.multipart;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public interface Multipart {

    @Nullable
    BlockSlot getSlot(BlockState state);

    default VoxelShape getIntersectionShape(BlockState state, Level level, BlockPos pos, @Nullable BlockState other) {
        return state.getCollisionShape(level, pos);
    }

    default IntersectionTestResult testIntersection(BlockState state, Level level, BlockPos pos, BlockState other) {
        return IntersectionTestResult.PASS;
    }

    static boolean testIntersectionBetween(Level level, BlockPos pos, BlockState first, BlockState second) {
        if (first.getBlock() instanceof Multipart multipart1 && second.getBlock() instanceof Multipart multipart2) {
            var result1 = multipart1.testIntersection(first, level, pos, second);
            if (result1 == IntersectionTestResult.INTERSECTION) {
                return true;
            }
            var result2 = multipart2.testIntersection(second, level, pos, first);
            if (result2 == IntersectionTestResult.INTERSECTION) {
                return true;
            }
            if (result1 == IntersectionTestResult.NO_INTERSECTION || result2 == IntersectionTestResult.NO_INTERSECTION) {
                return false;
            }
            var shape1 = multipart1.getIntersectionShape(first, level, pos, second).toAabbs();
            var shape2 = multipart2.getIntersectionShape(second, level, pos, first).toAabbs();
            for (AABB aabb1 : shape1) {
                for (AABB aabb2 : shape2) {
                    if (aabb1.intersects(aabb2)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    static boolean testIntersectionAgainst(Level level, BlockPos pos, AABB bounds, @Nullable BlockState self) {
        var state = level.getBlockState(pos);
        if (state == self) {
            return false;
        }
        if (state.getBlock() instanceof Multipart multipart) {
            var shape = multipart.getIntersectionShape(state, level, pos, self).toAabbs();
            for (AABB aabb : shape) {
                if (aabb.intersects(bounds)) {
                    return true;
                }
            }
            return false;
        } else if (state.getBlock() instanceof MultipartBlock) {
            if (level.getBlockEntity(pos) instanceof MultipartBlockEntity entity) {
                return entity.testIntersection(bounds, self);
            }
        }
        return true;
    }

    static BlockState getBlockState(BlockGetter level, BlockPos pos, @Nullable BlockSlot slot) {
        var state = level.getBlockState(pos);
        if (slot == null) {
            return state;
        }
        var block = state.getBlock();
        if (block instanceof Multipart multipart) {
            return multipart.getSlot(state) == slot ? state : Blocks.AIR.defaultBlockState();
        }
        if (block instanceof MultipartBlock) {
            return ((MultipartBlockEntity) level.getBlockEntity(pos)).getState(slot);
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Nullable
    static BlockEntity getBlockEntity(BlockGetter level, BlockPos pos, @Nullable BlockSlot slot) {
        var entity = level.getBlockEntity(pos);
        if (entity == null || slot == null) {
            return entity;
        }
        if (entity instanceof MultipartBlockEntity mbe) {
            return mbe.getEntity(slot);
        }
        return null;
    }

    @Nullable
    static BlockEntity getBlockEntity(BlockGetter level, BlockPos pos, BlockState state) {
        var entity = level.getBlockEntity(pos);
        if (entity == null || entity.getBlockState() == state) {
            return entity;
        }
        if (state.getBlock() instanceof Multipart multipart && entity instanceof MultipartBlockEntity mbe) {
            return mbe.getEntity(multipart.getSlot(state));
        }
        return null;
    }

}
