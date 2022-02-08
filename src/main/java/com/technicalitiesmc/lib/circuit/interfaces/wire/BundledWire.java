package com.technicalitiesmc.lib.circuit.interfaces.wire;

import net.minecraft.world.item.DyeColor;

public interface BundledWire extends Wire {

    RedstoneConductor getConductor(DyeColor color);

}
