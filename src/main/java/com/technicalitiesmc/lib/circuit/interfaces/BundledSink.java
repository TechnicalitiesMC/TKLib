package com.technicalitiesmc.lib.circuit.interfaces;

public interface BundledSink {

    static BundledSink instance() {
        return BundledSinkImpl.INSTANCE;
    }

}
