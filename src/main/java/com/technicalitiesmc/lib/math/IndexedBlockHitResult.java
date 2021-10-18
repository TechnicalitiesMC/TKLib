package com.technicalitiesmc.lib.math;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IndexedBlockHitResult extends BlockHitResult {

    private final BlockHitResult parent;
    private final VoxelShape shape;
    private final int index;

    public IndexedBlockHitResult(BlockHitResult parent, VoxelShape shape, int index) {
        super(parent.getLocation(), parent.getDirection(), parent.getBlockPos(), parent.isInside());
        this.parent = parent;
        this.shape = shape;
        this.index = index;
    }

    public BlockHitResult getParent() {
        return parent;
    }

    public VoxelShape getShape() {
        return shape;
    }

    public int getIndex() {
        return index;
    }

}
