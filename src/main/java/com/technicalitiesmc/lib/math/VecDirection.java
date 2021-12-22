package com.technicalitiesmc.lib.math;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public enum VecDirection {
    NEG_X(Direction.Axis.X, Direction.AxisDirection.NEGATIVE, 2),
    POS_X(Direction.Axis.X, Direction.AxisDirection.POSITIVE, 0),
    NEG_Y(Direction.Axis.Y, Direction.AxisDirection.NEGATIVE, -1),
    POS_Y(Direction.Axis.Y, Direction.AxisDirection.POSITIVE, -1),
    NEG_Z(Direction.Axis.Z, Direction.AxisDirection.NEGATIVE, 3),
    POS_Z(Direction.Axis.Z, Direction.AxisDirection.POSITIVE, 1);

    public static final VecDirection[] VALUES = values();

    private final Direction.Axis axis;
    private final Direction.AxisDirection axisDirection;
    private final int horizontalIndex;
    private final Vec3i offset;

    VecDirection(Direction.Axis axis, Direction.AxisDirection axisDirection, int horizontalIndex) {
        this.axis = axis;
        this.axisDirection = axisDirection;
        this.horizontalIndex = horizontalIndex;
        this.offset = Direction.fromAxisAndDirection(axis, axisDirection).getNormal();
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

}
