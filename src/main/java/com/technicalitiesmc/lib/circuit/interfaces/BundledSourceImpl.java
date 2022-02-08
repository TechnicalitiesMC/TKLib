package com.technicalitiesmc.lib.circuit.interfaces;

import net.minecraft.world.item.DyeColor;

import java.util.Arrays;

final class BundledSourceImpl implements BundledSource {

    private static final DyeColor[] COLORS = DyeColor.values();
    private static final int[] NO_SIGNAL = new int[COLORS.length];
    private static final int[] FULL_SIGNAL = new int[COLORS.length];
    static {
        Arrays.fill(FULL_SIGNAL, 255);
    }

    static final BundledSourceImpl OFF = new BundledSourceImpl(NO_SIGNAL, NO_SIGNAL);
    static final BundledSourceImpl FULL_WEAK = new BundledSourceImpl(NO_SIGNAL, FULL_SIGNAL);
    static final BundledSourceImpl FULL_STRONG = new BundledSourceImpl(FULL_SIGNAL, FULL_SIGNAL);

    private final int[] strong, weak;

    BundledSourceImpl(int[] strong, int[] weak) {
        this.strong = strong;
        this.weak = weak;
    }

    @Override
    public int[] getStrongOutput() {
        return strong;
    }

    @Override
    public int[] getWeakOutput() {
        return weak;
    }

}
