package com.technicalitiesmc.lib.container;

import com.technicalitiesmc.lib.container.fluid.FluidContainer;
import com.technicalitiesmc.lib.container.item.ItemContainer;
import net.minecraft.world.Container;
import net.minecraftforge.fluids.FluidStack;

public interface ExtendedContainer extends Container {

    static ExtendedContainer of(ItemContainer itemContainer, FluidContainer fluidContainer) {
        return new SimpleExtendedContainer(itemContainer, fluidContainer);
    }

    int getTanks();

    boolean areTanksEmpty();

    FluidStack getFluid(int tank);

    FluidStack removeFluid(int tank, int amount);

    void setFluid(int tank, FluidStack stack);

    int getTankCapacity(int tank);

    boolean canPlaceFluid(int tank, FluidStack stack);

}
