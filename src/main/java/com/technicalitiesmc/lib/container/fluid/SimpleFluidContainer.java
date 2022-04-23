package com.technicalitiesmc.lib.container.fluid;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class SimpleFluidContainer implements FluidContainer {

    @Override
    public int size() {
        return 0;
    }

    @NotNull
    @Override
    public FluidStack get(int tank) {
        return null;
    }

    @Override
    public void set(int tank, @NotNull FluidStack stack) {

    }

    @Override
    public boolean isValid(int tank, @NotNull FluidStack stack) {
        return false;
    }

    @Override
    public int getCapacity(int tank) {
        return 0;
    }

}
