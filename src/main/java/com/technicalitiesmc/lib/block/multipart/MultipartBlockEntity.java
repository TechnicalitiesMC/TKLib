package com.technicalitiesmc.lib.block.multipart;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class MultipartBlockEntity extends BlockEntity {

    public MultipartBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public BlockState getState(BlockSlot slot) {
        return Blocks.AIR.defaultBlockState();
    }

    public BlockEntity getEntity(BlockSlot slot) {
        return null;
    }

    public boolean testIntersection(AABB bounds, BlockState self) {
        return false;
    }

}
