package com.technicalitiesmc.lib.math;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;

public enum VecDirection implements StringRepresentable {
    NEG_X("neg_x", Direction.Axis.X, Direction.AxisDirection.NEGATIVE, 2),
    POS_X("pos_x", Direction.Axis.X, Direction.AxisDirection.POSITIVE, 0),
    NEG_Y("neg_y", Direction.Axis.Y, Direction.AxisDirection.NEGATIVE, -1),
    POS_Y("pos_y", Direction.Axis.Y, Direction.AxisDirection.POSITIVE, -1),
    NEG_Z("neg_z", Direction.Axis.Z, Direction.AxisDirection.NEGATIVE, 3),
    POS_Z("pos_z", Direction.Axis.Z, Direction.AxisDirection.POSITIVE, 1);

    public static final VecDirection[] VALUES = values();
    private static final VecDirection[] HORIZONTALS = { POS_X, POS_Z, NEG_X, NEG_Z };

    private final String name;
    private final Direction.Axis axis;
    private final Direction.AxisDirection axisDirection;
    private final int horizontalIndex;
    private final Vec3i offset;

    VecDirection(String name, Direction.Axis axis, Direction.AxisDirection axisDirection, int horizontalIndex) {
        this.name = name;
        this.axis = axis;
        this.axisDirection = axisDirection;
        this.horizontalIndex = horizontalIndex;
        this.offset = Direction.fromAxisAndDirection(axis, axisDirection).getNormal();
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static VecDirection fromDirection(Direction direction) {
        return switch (direction) {
            case DOWN -> NEG_Y;
            case UP -> POS_Y;
            case NORTH -> NEG_Z;
            case SOUTH -> POS_Z;
            case WEST -> NEG_X;
            case EAST -> POS_X;
        };
    }

    public static VecDirection fromAxisAndDirection(Direction.Axis axis, Direction.AxisDirection axisDirection) {
        return switch (axis) {
            case X -> axisDirection == Direction.AxisDirection.POSITIVE ? POS_X : NEG_X;
            case Y -> axisDirection == Direction.AxisDirection.POSITIVE ? POS_Y : NEG_Y;
            case Z -> axisDirection == Direction.AxisDirection.POSITIVE ? POS_Z : NEG_Z;
        };
    }

    public static VecDirection getNearest(Vec3i vec) {
        var direction = Direction.getNearest(vec.getX(), vec.getY(), vec.getZ());
        return fromAxisAndDirection(direction.getAxis(), direction.getAxisDirection());
    }

    public VecDirection getOpposite() {
        return VALUES[ordinal() ^ 1];
    }

    public Direction.Axis getAxis() {
        return axis;
    }

    public Direction.AxisDirection getAxisDirection() {
        return axisDirection;
    }

    public int getHorizontalIndex() {
        return horizontalIndex;
    }

    public Vec3i getOffset() {
        return offset;
    }

    public boolean isPositive() {
        return axisDirection == Direction.AxisDirection.POSITIVE;
    }

    public VecDirection applyY(Rotation rotation) {
        return HORIZONTALS[(getHorizontalIndex() + rotation.ordinal()) % HORIZONTALS.length];
    }
}
