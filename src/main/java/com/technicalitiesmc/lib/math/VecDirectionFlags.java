package com.technicalitiesmc.lib.math;

import com.technicalitiesmc.lib.util.AbstractFlags8;

public class VecDirectionFlags extends AbstractFlags8<VecDirection, VecDirectionFlags> {

    private static final VecDirectionFlags NONE = new VecDirectionFlags((byte) 0b000000);
    private static final VecDirectionFlags ALL = new VecDirectionFlags((byte) 0b111111);
    private static final VecDirectionFlags HORIZONTALS = new VecDirectionFlags((byte) 0b110011);
    private static final VecDirectionFlags VERTICALS = new VecDirectionFlags((byte) 0b001100);

    public static VecDirectionFlags none() {
        return NONE;
    }

    public static VecDirectionFlags all() {
        return ALL;
    }

    public static VecDirectionFlags horizontals() {
        return HORIZONTALS;
    }

    public static VecDirectionFlags verticals() {
        return VERTICALS;
    }

    public static VecDirectionFlags of(VecDirection... directions) {
        return new VecDirectionFlags(makeMask(directions));
    }

    public static VecDirectionFlags deserialize(byte value) {
        return new VecDirectionFlags(value);
    }

    private VecDirectionFlags(byte value) {
        super(value);
    }

    @Override
    protected Class<VecDirection> getType() {
        return VecDirection.class;
    }

    @Override
    protected VecDirectionFlags create(byte value) {
        return new VecDirectionFlags(value);
    }

}
