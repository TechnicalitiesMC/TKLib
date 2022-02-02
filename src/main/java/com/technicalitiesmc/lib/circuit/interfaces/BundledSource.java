package com.technicalitiesmc.lib.circuit.interfaces;

import net.minecraft.world.item.DyeColor;

public interface BundledSource {

    int getStrongOutput(DyeColor color);

    int getWeakOutput(DyeColor color);

}
