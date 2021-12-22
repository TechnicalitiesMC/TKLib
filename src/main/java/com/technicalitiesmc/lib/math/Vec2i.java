package com.technicalitiesmc.lib.math;

public record Vec2i(int x, int y) {

    public static final Vec2i ZERO = new Vec2i(0, 0);
    public static final Vec2i NEG_X = new Vec2i(-1, 0);
    public static final Vec2i POS_X = new Vec2i(1, 0);
    public static final Vec2i NEG_Y = new Vec2i(0, -1);
    public static final Vec2i POS_Y = new Vec2i(0, 1);
    public static final Vec2i NEG_X_NEG_Y = new Vec2i(-1, -1);
    public static final Vec2i NEG_X_POS_Y = new Vec2i(-1, 1);
    public static final Vec2i POS_X_NEG_Y = new Vec2i(1, -1);
    public static final Vec2i POS_X_POS_Y = new Vec2i(1, 1);

    public Vec2i(int[] values) {
        this(values[0], values[1]);
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public Vec2i offset(int x, int y) {
        return new Vec2i(x() + x, y() + y);
    }

    public Vec2i offset(Vec2i amt) {
        return new Vec2i(x() + amt.x(), y() + amt.y());
    }

    public Vec2i negate() {
        return new Vec2i(-x(), -y());
    }

    public int[] toArray() {
        return new int[] { x(), y() };
    }

}
