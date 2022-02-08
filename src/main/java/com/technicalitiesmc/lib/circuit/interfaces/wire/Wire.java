package com.technicalitiesmc.lib.circuit.interfaces.wire;

import com.technicalitiesmc.lib.math.VecDirection;

public interface Wire {

    void setState(VecDirection side, WireConnectionState state);

}
