package com.technicalitiesmc.lib.block;

import com.technicalitiesmc.lib.math.IndexedBlockHitResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public interface CustomBlockHighlight {

    @Nullable
    default VoxelShape getCustomHighlightShape(Level level, BlockHitResult target, Player player) {
        return getShapeFromHit(target);
    }

    @Nullable
    static VoxelShape getShapeFromHit(BlockHitResult target) {
        VoxelShape shape = null;
        while (target instanceof IndexedBlockHitResult hit) {
            target = hit.getParent();
            shape = hit.getShape();
        }
        return shape;
    }

}
