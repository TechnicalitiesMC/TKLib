package com.technicalitiesmc.lib.container.fluid.adapter;

import com.technicalitiesmc.lib.container.fluid.FluidContainer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidContainerToFluidHandlerAdapter implements IFluidHandler {

    private final FluidContainer inventory;

    public FluidContainerToFluidHandlerAdapter(FluidContainer inventory) {
        this.inventory = inventory;
    }

    @Override
    public int getTanks() {
        return inventory.size();
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return inventory.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return 0; // TODO
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true; // TODO
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return null;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return null;
    }

}
