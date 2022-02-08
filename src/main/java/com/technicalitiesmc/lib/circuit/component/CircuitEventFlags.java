package com.technicalitiesmc.lib.circuit.component;

import com.technicalitiesmc.lib.util.AbstractFlags8;

import java.util.Iterator;

public class CircuitEventFlags extends AbstractFlags8<CircuitEvent, CircuitEventFlags> implements Iterable<CircuitEvent> {

    private static final CircuitEventFlags NONE = new CircuitEventFlags((byte) 0b000000);

    public static CircuitEventFlags none() {
        return NONE;
    }

    public static CircuitEventFlags of(CircuitEvent... values) {
        return new CircuitEventFlags(makeMask(values));
    }

    public static CircuitEventFlags deserialize(byte value) {
        return new CircuitEventFlags(value);
    }

    private CircuitEventFlags(byte value) {
        super(value);
    }

    @Override
    protected CircuitEventFlags create(byte value) {
        return new CircuitEventFlags(value);
    }

    @Override
    public Iterator<CircuitEvent> iterator() {
        return stream(CircuitEvent.class).iterator();
    }

}
