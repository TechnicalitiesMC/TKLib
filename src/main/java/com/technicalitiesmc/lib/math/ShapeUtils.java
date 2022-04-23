package com.technicalitiesmc.lib.math;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapeUtils {

    public static VoxelShape rotate(VoxelShape down, Direction direction) {
        if (direction == Direction.DOWN){
            return down;
        }
        return down.toAabbs().parallelStream()
            .map(b -> Shapes.create(rotate(b, direction)))
            .reduce((a, b) -> Shapes.join(a, b, BooleanOp.OR))
            .map(VoxelShape::optimize).orElse(Shapes.empty());
    }

    public static AABB rotate(AABB down, Direction direction) {
        return switch (direction) {
            case DOWN -> down;
            case UP -> down.move(0, 1 - down.maxY, 0);
            case NORTH -> new AABB(down.minX, down.minZ, down.minY, down.maxX, down.maxZ, down.maxY);
            case SOUTH -> new AABB(down.minX, down.minZ, 1 - down.maxY, down.maxX, down.maxZ, 1 - down.minY);
            case WEST -> new AABB(down.minY, down.minX, down.minZ, down.maxY, down.maxX, down.maxZ);
            case EAST -> new AABB(1 - down.maxY, down.minX, down.minZ, 1 - down.minY, down.maxX, down.maxZ);
        };
    }

}
