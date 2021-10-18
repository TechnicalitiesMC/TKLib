package com.technicalitiesmc.lib.math;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IndexedShape extends CustomShape {

    public static IndexedShape box(int index, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new IndexedShape(Block.box(minX, minY, minZ, maxX, maxY, maxZ), index);
    }

    private final int index;

    public IndexedShape(VoxelShape parent, int index) {
        super(parent);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
