package com.technicalitiesmc.lib.container;

import com.technicalitiesmc.lib.container.fluid.FluidContainer;
import com.technicalitiesmc.lib.container.item.ItemContainer;
import com.technicalitiesmc.lib.container.item.adapter.ItemContainerToContainerAdapter;
import net.minecraftforge.fluids.FluidStack;

public class SimpleExtendedContainer extends ItemContainerToContainerAdapter implements ExtendedContainer {

    protected final FluidContainer fluidContainer;

    public SimpleExtendedContainer(ItemContainer itemContainer, FluidContainer fluidContainer) {
        super(itemContainer);
        this.fluidContainer = fluidContainer;
    }

    public ItemContainer getItemContainer() {
        return itemContainer;
    }

    public FluidContainer getFluidContainer() {
        return fluidContainer;
    }

    @Override
    public int getTanks() {
        return fluidContainer.size();
    }

    @Override
    public boolean areTanksEmpty() {
        return fluidContainer.isEmpty();
    }

    @Override
    public FluidStack getFluid(int tank) {
        return fluidContainer.get(tank);
    }

    @Override
    public FluidStack removeFluid(int tank, int amount) {
        var currentStack = fluidContainer.get(tank);

        var extracted = currentStack.copy();
        extracted.setAmount(Math.min(extracted.getAmount(), amount));

        var leftover = currentStack.copy();
        leftover.shrink(extracted.getAmount());
        fluidContainer.set(tank, leftover);

        return extracted;
    }

    @Override
    public void setFluid(int tank, FluidStack stack) {
        fluidContainer.set(tank, stack);
    }

    @Override
    public int getTankCapacity(int tank) {
        return fluidContainer.getCapacity(tank);
    }

    @Override
    public boolean canPlaceFluid(int tank, FluidStack stack) {
        return fluidContainer.isValid(tank, stack);
    }

}
