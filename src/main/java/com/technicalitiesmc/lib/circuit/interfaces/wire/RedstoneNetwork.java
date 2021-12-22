package com.technicalitiesmc.lib.circuit.interfaces.wire;

public interface RedstoneNetwork {

    static void build(RedstoneWire wire) {
        // TODO: remove
    }

    void propagate();

    void invalidate();

}
