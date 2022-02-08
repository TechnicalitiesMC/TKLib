package com.technicalitiesmc.lib.circuit.interfaces.wire;

import net.minecraft.util.StringRepresentable;

public enum WireConnectionState {
    DISCONNECTED(0, Visual.DISCONNECTED),
    FORCE_DISCONNECTED(1, Visual.DISCONNECTED),
    WIRE(2, Visual.ANODE),
    INPUT(4, Visual.CATHODE) {
        @Override
        public WireConnectionState getOpposite() {
            return OUTPUT;
        }
    },
    OUTPUT(5, Visual.ANODE) {
        @Override
        public WireConnectionState getOpposite() {
            return INPUT;
        }
    };

    private static final WireConnectionState[] VALUES = values();
    private static final WireConnectionState[] SERIAL_VALUES = { DISCONNECTED, FORCE_DISCONNECTED, WIRE, WIRE, INPUT, OUTPUT };

    private final int id;
    private final Visual visualState;

    WireConnectionState(int id, Visual visualState) {
        this.id = id;
        this.visualState = visualState;
    }

    public Visual getVisualState() {
        return visualState;
    }

    public int serialize() {
        return id;
    }

    public static WireConnectionState deserialize(int value) {
        return SERIAL_VALUES[value];
    }

    public WireConnectionState getOpposite() {
        return this;
    }

    public boolean isDisconnected() {
        return this == DISCONNECTED || this == FORCE_DISCONNECTED;
    }

    public enum Visual implements StringRepresentable {
        DISCONNECTED("disconnected"),
        ANODE("anode"),
        CATHODE("cathode");

        private final String name;

        Visual(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
