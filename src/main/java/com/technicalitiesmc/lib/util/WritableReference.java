package com.technicalitiesmc.lib.util;

public interface WritableReference<T> {

    static <T> WritableReference<T> empty() {
        return val -> {};
    }

    void set(T value);

}
