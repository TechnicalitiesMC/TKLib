package com.technicalitiesmc.lib.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class NbtHelper {

    public static <T> ListTag write(NonNullList<T> list, BiFunction<T, CompoundTag, CompoundTag> serializer) {
        return write(list, e -> serializer.apply(e, new CompoundTag()));
    }

    public static <T, N extends Tag> ListTag write(NonNullList<T> list, Function<T, N> serializer) {
        var tag = new ListTag();
        for (T element : list) {
            tag.addTag(tag.size(), serializer.apply(element));
        }
        return tag;
    }

    public static <T, N extends Tag> NonNullList<T> read(ListTag tag, Function<N, T> deserializer) {
        var list = NonNullList.<T>createWithCapacity(tag.size());
        for (Tag element : tag) {
            list.add(deserializer.apply((N) element));
        }
        return list;
    }

    public static <T> Optional<T> maybe(CompoundTag tag, BiFunction<CompoundTag, String, T> function, String name) {
        return Optional.ofNullable(tag.contains(name) ? function.apply(tag, name) : null);
    }

    public static void putEnum(CompoundTag tag, String name, Enum<?> value) {
        tag.putByte(name, (byte) value.ordinal());
    }

    public static <E extends Enum<E>> E getEnum(CompoundTag tag, String name, Class<E> type) {
        return type.getEnumConstants()[tag.getByte(name)];
    }

    public static void maybePutEnum(CompoundTag tag, String name, @Nullable Enum<?> value) {
        if (value != null) {
            tag.putByte(name, (byte) value.ordinal());
        }
    }

    public static <E extends Enum<E>> Optional<E> maybeGetEnum(CompoundTag tag, String name, Class<E> type) {
        return Optional.ofNullable(tag.contains(name) ? getEnum(tag, name, type) : null);
    }

}
