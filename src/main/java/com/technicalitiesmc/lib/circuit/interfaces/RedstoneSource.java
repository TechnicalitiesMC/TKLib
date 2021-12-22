package com.technicalitiesmc.lib.circuit.interfaces;

public interface RedstoneSource {

    static RedstoneSource of(int strong, int weak) {
        return new RedstoneSourceImpl(strong, weak);
    }

    static RedstoneSource off() {
        return RedstoneSourceImpl.OFF;
    }

    static RedstoneSource full(boolean strong) {
        return strong ? fullStrong() : fullWeak();
    }

    static RedstoneSource fullWeak() {
        return RedstoneSourceImpl.FULL_WEAK;
    }

    static RedstoneSource fullStrong() {
        return RedstoneSourceImpl.FULL_STRONG;
    }

    int getStrongOutput();

    int getWeakOutput();

}
