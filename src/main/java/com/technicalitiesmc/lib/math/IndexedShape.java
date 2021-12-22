package com.technicalitiesmc.lib.math;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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

}
