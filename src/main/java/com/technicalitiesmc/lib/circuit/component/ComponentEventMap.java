package com.technicalitiesmc.lib.circuit.component;

import com.technicalitiesmc.lib.math.VecDirection;
import com.technicalitiesmc.lib.math.VecDirectionFlags;

import java.util.EnumMap;
import java.util.Map;

public class ComponentEventMap {

    private static final ComponentEventMap EMPTY = new ComponentEventMap(new EnumMap<>(VecDirection.class));

    public static ComponentEventMap empty() {
        return EMPTY;
    }

    private final EnumMap<VecDirection, CircuitEventFlags> flags;

    private ComponentEventMap(EnumMap<VecDirection, CircuitEventFlags> flags) {
        this.flags = flags;
    }

    public CircuitEventFlags get(VecDirection side) {
        return flags.getOrDefault(side, CircuitEventFlags.none());
    }

    public boolean hasAny(VecDirection side, CircuitEvent... events) {
        return get(side).hasAny(events);
    }

    public VecDirectionFlags findAny(CircuitEvent... events) {
        return findAny(VecDirectionFlags.all(), events);
    }

    public VecDirectionFlags findAny(VecDirectionFlags mask, CircuitEvent... events) {
        return VecDirectionFlags.of(
                flags.entrySet().stream()
                        .filter(e -> mask.has(e.getKey()) && e.getValue().hasAny(events))
                        .map(Map.Entry::getKey)
                        .toArray(VecDirection[]::new)
        );
    }

    public byte[] serialize() {
        var bytes = new byte[VecDirection.VALUES.length];
        for (var side : VecDirection.VALUES) {
            bytes[side.ordinal()] = get(side).serialize();
        }
        return bytes;
    }

    public static class Builder {

        private final EnumMap<VecDirection, CircuitEventFlags> flags = new EnumMap<>(VecDirection.class);

        public void add(VecDirection side, CircuitEventFlags events) {
            flags.compute(side, ($, current) -> {
                return current != null ? current.and(events) : events;
            });
        }

        public void add(VecDirection side, CircuitEvent... events) {
            add(side, CircuitEventFlags.of(events));
        }

        public void add(VecDirectionFlags sides, CircuitEvent... events) {
            sides.forEach(side -> add(side, events));
        }

        public boolean isEmpty() {
            return flags.isEmpty();
        }

        public ComponentEventMap build() {
            return new ComponentEventMap(flags);
        }

        public static ComponentEventMap.Builder deserialize(byte[] bytes) {
            var builder = new ComponentEventMap.Builder();
            for (var side : VecDirection.VALUES) {
                builder.add(side, CircuitEventFlags.deserialize(bytes[side.ordinal()]));
            }
            return builder;
        }

    }

}
