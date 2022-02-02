package com.technicalitiesmc.lib.circuit.interfaces;

public interface RedstoneSink {

    static RedstoneSink instance() {
        return RedstoneSinkImpl.INSTANCE;
    }

}
