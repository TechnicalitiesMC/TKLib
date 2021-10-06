package com.technicalitiesmc.lib.menu;

import com.technicalitiesmc.lib.init.TKLibMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LockedChestMenu extends ChestMenu {


    public static LockedChestMenu threeRows(int id, Inventory playerInv) {
        return threeRows(id, playerInv, new SimpleContainer(9 * 3));
    }

    public static LockedChestMenu sixRows(int id, Inventory playerInv) {
        return sixRows(id, playerInv, new SimpleContainer(9 * 6));
    }

    public static LockedChestMenu threeRows(int id, Inventory playerInv, Container container) {
        return new LockedChestMenu(TKLibMenus.LOCKED_GENERIC_9x3.get(), 3, id, playerInv, container);
    }

    public static LockedChestMenu sixRows(int id, Inventory playerInv, Container container) {
        return new LockedChestMenu(TKLibMenus.LOCKED_GENERIC_9x6.get(), 6, id, playerInv, container);
    }

    private Slot lockedSlot;

    public LockedChestMenu(MenuType<?> type, int rows, int id, Inventory playerInv, Container container) {
        super(type, id, playerInv, container, rows);

        findSlot(playerInv, playerInv.selected).ifPresent(i -> {
            var currentSlot = slots.get(i);
            var newSlot = new Slot(currentSlot.container, currentSlot.getContainerSlot(), currentSlot.x, currentSlot.y) {
                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }

                @Override
                public boolean mayPlace(ItemStack player) {
                    return false;
                }
            };
            newSlot.index = currentSlot.index;
            slots.set(i, newSlot);
            lockedSlot = newSlot;
        });
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot != lockedSlot && super.canTakeItemForPickAll(stack, slot);
    }

}
