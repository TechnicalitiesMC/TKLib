package com.technicalitiesmc.lib.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PropertyMap {

    private final Map<Property, Comparable> properties;

    private PropertyMap(Map<Property, Comparable> properties) {
        this.properties = properties;
    }

    public <T extends Comparable<T>> PropertyMap setProperty(Property<T> property, T value) {
        if (!properties.containsKey(property)) {
            throw new IllegalArgumentException("Cannot set property that is not already in the map: " + property.getName());
        }
        var props = new HashMap<>(properties);
        props.put(property, value);
        return new PropertyMap(props);
    }

    public <T extends Comparable<T>> T getProperty(Property<T> property) {
        return (T) properties.get(property);
    }

    public Set<Property<?>> getProperties() {
        return (Set) properties.keySet();
    }

    public Map<Property, Comparable> getValues() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyMap that = (PropertyMap) o;
        return properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    public void serialize(FriendlyByteBuf buf) {
        buf.writeInt(properties.size());
        properties.forEach((p, v) -> {
            buf.writeUtf(p.getName());
            buf.writeUtf(p.getName(v));
        });
    }

    public PropertyMap deserialize(FriendlyByteBuf buf) {
        return deserialize(PartialDeserialization.deserialize(buf));
    }

    public PropertyMap deserialize(PartialDeserialization data) {
        var map = this;
        var props = properties.keySet().stream()
                .collect(Collectors.toMap(Property::getName, Function.identity()));
        for (var entry : data.properties.entrySet()) {
            var prop = props.get(entry.getKey());
            if (prop == null) {
                throw new IllegalStateException("Cannot deserialize property map.");
            }
            var val = prop.getValue(entry.getValue());
            if (val.isEmpty()) {
                throw new IllegalStateException("Cannot deserialize property map.");
            }
            map = map.setProperty(prop, (Comparable) val.get());
        }
        return map;
    }

    public static class Builder {

        private final Map<Property, Comparable> properties = new HashMap<>();

        public Builder add(Property... props) {
            for (var prop : props) {
                properties.put(prop, (Comparable) prop.getPossibleValues().iterator().next());
            }
            return this;
        }

        public <T extends Comparable<T>> Builder add(Property<T> property, T value) {
            properties.put(property, value);
            return this;
        }

        public boolean isEmpty() {
            return properties.isEmpty();
        }

        public PropertyMap build() {
            return new PropertyMap(ImmutableMap.copyOf(properties));
        }

    }

    public static class PartialDeserialization {

        private final Map<String, String> properties;

        private PartialDeserialization(Map<String, String> properties) {
            this.properties = properties;
        }

        public static PartialDeserialization deserialize(FriendlyByteBuf buf) {
            var map = new HashMap<String, String>();
            var amt = buf.readInt();
            for (int i = 0; i < amt; i++) {
                var prop = buf.readUtf();
                var val = buf.readUtf();
                map.put(prop, val);
            }
            return new PartialDeserialization(map);
        }

    }

}
