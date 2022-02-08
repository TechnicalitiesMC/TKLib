package com.technicalitiesmc.lib.circuit.interfaces;

public interface BundledSource {

    static BundledSource of(int[] strong, int[] weak) {
        return new BundledSourceImpl(strong, weak);
    }

    static BundledSource off() {
        return BundledSourceImpl.OFF;
    }

    static BundledSource full(boolean strong) {
        return strong ? fullStrong() : fullWeak();
    }

    static BundledSource fullWeak() {
        return BundledSourceImpl.FULL_WEAK;
    }

    static BundledSource fullStrong() {
        return BundledSourceImpl.FULL_STRONG;
    }

    int[] getStrongOutput();

    int[] getWeakOutput();

}
