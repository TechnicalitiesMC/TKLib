package com.technicalitiesmc.lib.util.value;

public sealed class Value<T> implements Reference<T> {

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

    public static final class Notifying<T> extends Value<T> {

        private final Runnable callback;

        public Notifying(T value, Runnable callback) {
            super(value);
            this.callback = callback;
        }

        @Override
        public void set(T value) {
            super.set(value);
            callback.run();
        }

    }

}
