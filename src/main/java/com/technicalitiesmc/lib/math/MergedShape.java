package com.technicalitiesmc.lib.math;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MergedShape extends CustomShape {

    public static MergedShape of(VoxelShape... shapes) {
        return of(ImmutableList.copyOf(shapes));
    }

    public static MergedShape of(List<VoxelShape> shapes) {
        return of(ImmutableList.copyOf(shapes));
    }

    public static MergedShape of(ImmutableList<VoxelShape> shapes) {
        var merged = shapes.stream()
                .filter(Objects::nonNull)
                .map(MergedShape::unwrap)
                .reduce((a, b) -> Shapes.join(a, b, BooleanOp.OR))
                .map(VoxelShape::optimize)
                .orElse(Shapes.empty());
        return new MergedShape(shapes, merged);
    }

    private static VoxelShape unwrap(VoxelShape shape) {
        return shape instanceof CustomShape ? ((CustomShape) shape).parent : shape;
    }

    private final ImmutableList<VoxelShape> shapes;

    private MergedShape(ImmutableList<VoxelShape> shapes, VoxelShape merged) {
        super(merged);
        this.shapes = shapes;
    }

    public ImmutableList<VoxelShape> getShapes() {
        return shapes;
    }

    public Optional<IndexedShape> find(int index) {
        return shapes.stream()
                .filter(s -> s instanceof IndexedShape is && is.getIndex() == index)
                .map(s -> (IndexedShape) s)
                .findFirst();
    }

    @Nullable
    @Override
    public BlockHitResult clip(Vec3 start, Vec3 end, BlockPos pos) {
        BlockHitResult closest = null;
        double closestDist = Double.POSITIVE_INFINITY;
        for (var shape : shapes) {
            var hit = shape.clip(start, end, pos);
            double dist;
            if (hit == null || hit.getType() == HitResult.Type.MISS || (dist = hit.getLocation().distanceToSqr(start)) > closestDist) {
                continue;
            }
            closest = new IndexedBlockHitResult(hit, shape, shape instanceof IndexedShape is ? is.getIndex() : -1);
            closestDist = dist;
        }
        return closest;
    }

}
