package com.technicalitiesmc.lib.circuit.interfaces;

final class RedstoneSourceImpl implements RedstoneSource {

    static final RedstoneSourceImpl OFF = new RedstoneSourceImpl(0, 0);
    static final RedstoneSourceImpl FULL_WEAK = new RedstoneSourceImpl(0, 255);
    static final RedstoneSourceImpl FULL_STRONG = new RedstoneSourceImpl(255, 255);

    private final int strong, weak;

    RedstoneSourceImpl(int strong, int weak) {
        this.strong = strong;
        this.weak = weak;
    }

    @Override
    public int getStrongOutput() {
        return strong;
    }

    @Override
    public int getWeakOutput() {
        return weak;
    }

}
