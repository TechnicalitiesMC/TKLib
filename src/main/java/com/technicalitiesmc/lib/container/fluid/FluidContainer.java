package com.technicalitiesmc.lib.container.fluid;

import com.technicalitiesmc.lib.container.fluid.adapter.FluidContainerToFluidHandlerAdapter;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.Objects;

public interface FluidContainer {

    @Contract(pure = true)
    int size();

    @Contract(pure = true)
    @Nonnull FluidStack get(int tank);

    void set(int tank, @Nonnull FluidStack stack);

    @Contract(pure = true)
    boolean isValid(int tank, @Nonnull FluidStack stack);

    @Contract(pure = true)
    int getCapacity(int tank);

    default void clear() {
        for (int i = 0; i < size(); i++) {
            set(i, FluidStack.EMPTY);
        }
    }

    @Contract(pure = true)
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            if (!get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Contract(value = "_, _ -> new", pure = true)
    default @Nonnull FluidContainer slice(int from, int to) {
        Objects.checkFromToIndex(from, to, size());
        return new FluidContainerSlice(this, from, to - from);
    }

    @Contract(value = "-> new", pure = true)
    default @Nonnull IFluidHandler asFluidHandler() {
        return new FluidContainerToFluidHandlerAdapter(this);
    }

}
