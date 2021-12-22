package com.technicalitiesmc.lib.util;

public class Flags8<T extends Enum<T>> extends AbstractFlags8<T, Flags8<T>> {

    private static final Flags8<?> NONE = new Flags8<>((byte) 0b000000);
    private static final Flags8<?> ALL = new Flags8<>((byte) 0b111111);

    public static <T extends Enum<T>> Flags8<T> none() {
        return (Flags8<T>) NONE;
    }

    public static <T extends Enum<T>> Flags8<T> all() {
        return (Flags8<T>) ALL;
    }

    public static <T extends Enum<T>> Flags8<T> of(T... values) {
        return new Flags8<>(makeMask(values));
    }

    protected Flags8(byte value) {
        super(value);
    }

    @Override
    protected Flags8<T> create(byte value) {
        return new Flags8<>(value);
    }

}
