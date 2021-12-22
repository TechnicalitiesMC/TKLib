package com.technicalitiesmc.lib.circuit.interfaces.wire;

import net.minecraft.world.item.DyeColor;

import javax.annotation.Nullable;

public interface RedstoneWire {

    @Nullable
    DyeColor getColor();

    void clearNetwork();

    void setNetwork(RedstoneNetwork network);

    void visit(Visitor visitor);

    int getInput();

    void updateAndNotify(int newPower);

    interface Visitor {

        void accept(RedstoneWire wire);

    }

}
