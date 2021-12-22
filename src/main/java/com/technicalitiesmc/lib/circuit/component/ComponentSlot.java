package com.technicalitiesmc.lib.circuit.component;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public enum ComponentSlot {
    DEFAULT,
    OVERLAY,
    SUPPORT;

    public static final ComponentSlot[] VALUES = values();

    static {
        for (ComponentSlot layer : VALUES) {
            layer.above = VALUES[(layer.ordinal() + 1) % VALUES.length];
            layer.below = VALUES[(layer.ordinal() + VALUES.length - 1) % VALUES.length];
        }
    }

    private static final Vec3i DOWN = new Vec3i(0, -1, 0);
    private static final Vec3i UP = new Vec3i(0, 1, 0);

    private ComponentSlot above, below;

    public ComponentSlot next(Direction.AxisDirection direction) {
        return direction == Direction.AxisDirection.POSITIVE ? above : below;
    }

    public boolean isEdge(Direction.AxisDirection direction) {
        return this == (direction == Direction.AxisDirection.POSITIVE ? SUPPORT : DEFAULT);
    }

    public Vec3i getOffsetTowards(Direction.AxisDirection direction) {
        return direction == Direction.AxisDirection.POSITIVE ?
                (this == SUPPORT ? UP : Vec3i.ZERO) :
                (this == DEFAULT ? DOWN : Vec3i.ZERO);
    }

}
