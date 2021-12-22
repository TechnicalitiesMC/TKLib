package com.technicalitiesmc.lib.util.value;

public final class Value<T> implements Reference<T> {

    private T value;

    public Value(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

}
