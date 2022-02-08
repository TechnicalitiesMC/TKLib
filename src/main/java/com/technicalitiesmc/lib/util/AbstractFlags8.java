package com.technicalitiesmc.lib.util;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class AbstractFlags8<T extends Enum<T>, F extends AbstractFlags8<T, F>> {

    public static byte makeMask(Enum<?> value) {
        return (byte) (1 << value.ordinal());
    }

    public static byte makeMask(Enum<?>... values) {
        var value = (byte) 0;
        for (var val : values) {
            value |= makeMask(val);
        }
        return value;
    }

    private final byte value;

    protected AbstractFlags8(byte value) {
        this.value = value;
    }

    protected final byte getValue() {
        return value;
    }

    protected abstract F create(byte value);

    public boolean isEmpty() {
        return value == 0;
    }

    public boolean has(T val) {
        return (value & makeMask(val)) != 0;
    }

    public boolean hasAny(T... values) {
        return (value & makeMask(values)) != 0;
    }

    public boolean hasAny(F values) {
        return (value & values.getValue()) != 0;
    }

    public boolean hasAll(T... values) {
        var mask = makeMask(values);
        return (value & mask) == mask;
    }

    public boolean hasAll(F values) {
        var mask = values.getValue();
        return (value & mask) == mask;
    }

    public F and(T val) {
        return create((byte) (value | makeMask(val)));
    }

    public F and(T... values) {
        return create((byte) (value | makeMask(values)));
    }

    public F and(F values) {
        return create((byte) (value | values.getValue()));
    }

    public F except(T val) {
        return create((byte) (value & ~makeMask(val)));
    }

    public F except(T... values) {
        return create((byte) (value & ~makeMask(values)));
    }

    public F except(F values) {
        return create((byte) (value & ~values.getValue()));
    }

    public F onlyIn(T val) {
        return create((byte) (value & makeMask(val)));
    }

    public final F onlyIn(T... values) {
        return create((byte) (value & makeMask(values)));
    }

    public F onlyIn(F values) {
        return create((byte) (value & values.getValue()));
    }

    public Stream<T> stream(Class<T> clazz) {
        return Arrays.stream(clazz.getEnumConstants())
                .filter(this::has);
    }

    public byte serialize() {
        return value;
    }

}
