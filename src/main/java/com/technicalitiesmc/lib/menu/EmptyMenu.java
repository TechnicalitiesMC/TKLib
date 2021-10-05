package com.technicalitiesmc.lib.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class EmptyMenu extends AbstractContainerMenu {

    public EmptyMenu() {
        super(null, -1);
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }

    @Override
    public void slotsChanged(Container p_38868_) {
    }

    @Override
    public void broadcastChanges() {
    }

    @Override
    public void broadcastFullState() {
    }

}
