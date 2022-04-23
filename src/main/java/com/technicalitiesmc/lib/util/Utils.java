package com.technicalitiesmc.lib.util;

import com.technicalitiesmc.lib.math.VecDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class Utils {

    private static final Capability<DyeHolder> DYE_HOLDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    /**
     * Sets the bit at the specified position to the given state.
     */
    public static byte set(byte value, int position, boolean state) {
        if (state) {
            return (byte) (value | (1 << position));
        } else {
            return (byte) (value & ~(1 << position));
        }
    }

    /**
     * Calculates which side the specified point is closest to.
     */
    public static VecDirection calculatePlanarDirection(float x, float z) {
        if (Math.abs(x) > Math.abs(z)) {
            return x > 0 ? VecDirection.POS_X : VecDirection.NEG_X;
        } else {
            return z > 0 ? VecDirection.POS_Z : VecDirection.NEG_Z;
        }
    }

    /**
     * Returns the next element in the array, looping back at the end, and returning the first if not found.
     */
    public static <T> T cycle(T[] arr, T current) {
        var currentIdx = ArrayUtils.indexOf(arr, current);
        var idx = (currentIdx + 1) % arr.length;
        return arr[idx];
    }

    /**
     * Returns the next element in the array that passes the predicate, looping back at the end and returning the first
     * valid entry if not found. If no valid entry is found, the original value is returned.
     */
    public static <T> T cycleConditionally(T[] arr, T current, Predicate<T> predicate) {
        var currentIdx = ArrayUtils.indexOf(arr, current);
        for (var i = 0; i < arr.length - 1; i++) { // Skip the last element, aka the input
            var idx = (currentIdx + i + 1) % (arr.length);
            var candidate = arr[idx];
            if (predicate.test(candidate)) {
                return candidate;
            }
        }
        return current;
    }

    /**
     * Applies a rotation around the Y axis to an array.
     */
    public static <T> T[] rotateArray(T[] arr, Rotation rotation) {
        var newArr = Arrays.copyOf(arr, arr.length);
        for (var direction : Direction.Plane.HORIZONTAL) {
            newArr[rotation.rotate(direction).ordinal()] = arr[direction.ordinal()];
        }
        return newArr;
    }

    /**
     * Applies a rotation around the Y axis to an array.
     */
    public static int[] rotateArray(int[] arr, Rotation rotation) {
        var newArr = Arrays.copyOf(arr, arr.length);
        for (var direction : Direction.Plane.HORIZONTAL) {
            newArr[rotation.rotate(direction).ordinal()] = arr[direction.ordinal()];
        }
        return newArr;
    }

    /**
     * Resolves the block hit to a state. If this is a multipart hit, the returned state is the state of the part.
     */
    public static BlockState resolveHit(BlockGetter level, BlockHitResult hit) {
        return level.getBlockState(hit.getBlockPos());
    }

    /**
     * Creates a new identity-comparing hash set.
     */
    public static <T> Set<T> newIdentityHashSet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }

    /**
     * Creates and fills an enum map with values.
     */
    public static <K extends Enum<K>, V> EnumMap<K, V> newFilledEnumMap(Class<K> keyType, Function<K, V> valueMapper) {
        var map = new EnumMap<K, V>(keyType);
        for (var key : keyType.getEnumConstants()) {
            map.put(key, valueMapper.apply(key));
        }
        return map;
    }

    @Nullable
    public static DyeColor getDyeColor(ItemStack stack) {
        var cap = stack.getCapability(DYE_HOLDER_CAPABILITY);
        if (cap.isPresent()) {
            return cap.orElse(null).getColor();
        }
        return DyeColor.getColor(stack);
    }

}
