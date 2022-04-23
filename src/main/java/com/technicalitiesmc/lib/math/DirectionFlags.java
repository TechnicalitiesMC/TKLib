package com.technicalitiesmc.lib.math;

import com.technicalitiesmc.lib.util.AbstractFlags8;
import net.minecraft.core.Direction;

public class DirectionFlags extends AbstractFlags8<Direction, DirectionFlags> {

    private static final DirectionFlags NONE = new DirectionFlags((byte) 0b000000);
    private static final DirectionFlags ALL = new DirectionFlags((byte) 0b111111);
    private static final DirectionFlags HORIZONTALS = new DirectionFlags((byte) 0b111100);
    private static final DirectionFlags VERTICALS = new DirectionFlags((byte) 0b000011);

    public static DirectionFlags none() {
        return NONE;
    }

    public static DirectionFlags all() {
        return ALL;
    }

    public static DirectionFlags horizontals() {
        return HORIZONTALS;
    }

    public static DirectionFlags verticals() {
        return VERTICALS;
    }

    public static DirectionFlags of(Direction... directions) {
        return new DirectionFlags(makeMask(directions));
    }

    public static DirectionFlags deserialize(byte value) {
        return new DirectionFlags(value);
    }

    private DirectionFlags(byte value) {
        super(value);
    }

    @Override
    protected Class<Direction> getType() {
        return Direction.class;
    }

    @Override
    protected DirectionFlags create(byte value) {
        return new DirectionFlags(value);
    }

}
