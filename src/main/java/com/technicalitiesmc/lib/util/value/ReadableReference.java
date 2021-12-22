package com.technicalitiesmc.lib.util.value;

public interface ReadableReference<T> {

    static <T> ReadableReference<T> of(T value) {
        return () -> value;
    }

    T get();

}
