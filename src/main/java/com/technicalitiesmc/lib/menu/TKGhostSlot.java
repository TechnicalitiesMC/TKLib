package com.technicalitiesmc.lib.menu;

import com.technicalitiesmc.lib.container.item.ItemContainer;
import net.minecraft.world.entity.player.Player;

public class TKGhostSlot extends TKSlot {

    private final int limit;

    public TKGhostSlot(int x, int y, ItemContainer inventory, int slot, int limit) {
        super(x, y, inventory, slot);
        this.limit = limit;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return limit;
    }

}
