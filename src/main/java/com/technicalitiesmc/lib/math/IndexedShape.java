package com.technicalitiesmc.lib.math;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class IndexedShape extends CustomShape {

    public static IndexedShape box(int index, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new IndexedShape(index, Block.box(minX, minY, minZ, maxX, maxY, maxZ));
    }

    public static IndexedShape create(int index, AABB box) {
        return new IndexedShape(index, Shapes.create(box));
    }

    private final int index;

    public IndexedShape(int index, VoxelShape parent) {
        super(parent);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Nullable
    @Override
    public BlockHitResult clip(Vec3 start, Vec3 end, BlockPos pos) {
        var hit = super.clip(start, end, pos);
        return hit != null ? new IndexedBlockHitResult(hit, this, index) : null;
    }

}
