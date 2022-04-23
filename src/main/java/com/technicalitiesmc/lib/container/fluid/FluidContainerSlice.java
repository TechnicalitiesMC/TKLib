package com.technicalitiesmc.lib.container.fluid;

import net.minecraftforge.fluids.FluidStack;

import java.util.Objects;

public class FluidContainerSlice implements FluidContainer {

    private final FluidContainer parent;
    private final int from, size;

    public FluidContainerSlice(FluidContainer parent, int from, int size) {
        this.parent = parent;
        this.from = from;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public FluidStack get(int tank) {
        Objects.checkIndex(tank, size);
        return parent.get(from + tank);
    }

    @Override
    public void set(int tank, FluidStack stack) {
        Objects.checkIndex(tank, size);
        parent.set(from + tank, stack);
    }

    @Override
    public boolean isValid(int tank, FluidStack stack) {
        Objects.checkIndex(tank, size);
        return parent.isValid(from + tank, stack);
    }

    @Override
    public int getCapacity(int tank) {
        Objects.checkIndex(tank, size);
        return parent.getCapacity(from + tank);
    }

}
