package com.technicalitiesmc.lib.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public abstract class CustomShape extends ArrayVoxelShape {

    private static final DiscreteVoxelShape DISCRETE_SHAPE = new BitSetDiscreteVoxelShape(1, 1, 1);
    private static final double[] EMPTY = new double[2];

    static {
        DISCRETE_SHAPE.fill(0, 0, 0);
    }

    final VoxelShape parent;

    protected CustomShape(VoxelShape parent) {
        super(DISCRETE_SHAPE, EMPTY, EMPTY, EMPTY);
        this.parent = parent;
    }

    @Override
    @Nullable
    public BlockHitResult clip(Vec3 p_83221_, Vec3 p_83222_, BlockPos p_83223_) {
        return parent.clip(p_83221_, p_83222_, p_83223_);
    }

    @Override
    public double min(Direction.Axis p_83289_) {
        return parent.min(p_83289_);
    }

    @Override
    public double max(Direction.Axis p_83298_) {
        return parent.max(p_83298_);
    }

    @Override
    public AABB bounds() {
        return parent.bounds();
    }

    @Override
    public boolean isEmpty() {
        return parent.isEmpty();
    }

    @Override
    public VoxelShape move(double p_83217_, double p_83218_, double p_83219_) {
        return parent.move(p_83217_, p_83218_, p_83219_);
    }

    @Override
    public VoxelShape optimize() {
        return parent.optimize();
    }

    @Override
    public void forAllEdges(Shapes.DoubleLineConsumer p_83225_) {
        parent.forAllEdges(p_83225_);
    }

    @Override
    public void forAllBoxes(Shapes.DoubleLineConsumer p_83287_) {
        parent.forAllBoxes(p_83287_);
    }

    @Override
    public List<AABB> toAabbs() {
        return parent.toAabbs();
    }

    @Override
    public double min(Direction.Axis p_166079_, double p_166080_, double p_166081_) {
        return parent.min(p_166079_, p_166080_, p_166081_);
    }

    @Override
    public double max(Direction.Axis p_83291_, double p_83292_, double p_83293_) {
        return parent.max(p_83291_, p_83292_, p_83293_);
    }

    @Override
    public Optional<Vec3> closestPointTo(Vec3 p_166068_) {
        return parent.closestPointTo(p_166068_);
    }

    @Override
    public VoxelShape getFaceShape(Direction p_83264_) {
        return parent.getFaceShape(p_83264_);
    }

    @Override
    public double collide(Direction.Axis p_83260_, AABB p_83261_, double p_83262_) {
        return parent.collide(p_83260_, p_83261_, p_83262_);
    }

    @Override
    public String toString() {
        return parent.toString();
    }

}
