package com.technicalitiesmc.lib.menu.slot;

import com.technicalitiesmc.lib.container.item.ItemContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TKGhostSlot extends TKSlot {

    private final int limit;

    public TKGhostSlot(int x, int y, ItemContainer inventory, int slot, int limit) {
        super(x, y, inventory, slot);
        this.limit = limit;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
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
