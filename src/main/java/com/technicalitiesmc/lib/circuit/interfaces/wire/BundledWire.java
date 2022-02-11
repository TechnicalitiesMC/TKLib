package com.technicalitiesmc.lib.circuit.interfaces.wire;

import net.minecraft.world.item.DyeColor;

import javax.annotation.Nullable;

public interface BundledWire {

    @Nullable
    RedstoneConductor getConductor(DyeColor color);

}
