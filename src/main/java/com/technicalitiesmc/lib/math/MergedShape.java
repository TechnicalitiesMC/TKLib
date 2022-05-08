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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class MergedShape extends CustomShape {

    private static VoxelShape merge(Stream<VoxelShape> shapes) {
        return shapes
                .filter(Objects::nonNull)
                .map(MergedShape::unwrap)
                .reduce((a, b) -> Shapes.joinUnoptimized(a, b, BooleanOp.OR))
                .map(VoxelShape::optimize)
                .orElse(Shapes.empty());
    }

    public static MergedShape ofMerged(VoxelShape... shapes) {
        return of(merge(Arrays.stream(shapes)), shapes);
    }

    public static MergedShape of(VoxelShape baseShape, VoxelShape... shapes) {
        return of(baseShape, ImmutableList.copyOf(shapes));
    }

    public static MergedShape ofMerged(List<VoxelShape> shapes) {
        return of(merge(shapes.stream()), ImmutableList.copyOf(shapes));
    }

    public static MergedShape of(VoxelShape baseShape, List<VoxelShape> shapes) {
        return of(baseShape, ImmutableList.copyOf(shapes));
    }

    public static MergedShape ofMerged(ImmutableList<VoxelShape> shapes) {
        return of(merge(shapes.stream()), shapes);
    }

    public static MergedShape of(VoxelShape baseShape, ImmutableList<VoxelShape> shapes) {
        return new MergedShape(shapes, baseShape);
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
            closest = hit;
            closestDist = dist;
        }
        return closest;
    }

}
