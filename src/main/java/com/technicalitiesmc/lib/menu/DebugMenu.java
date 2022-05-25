package com.technicalitiesmc.lib.menu;

import com.technicalitiesmc.lib.init.TKLibMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class DebugMenu extends TKMenu {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");

    public DebugMenu(int id, Inventory playerInv, IItemHandler inventory) {
        super(TKLibMenus.DEBUG, id, playerInv, BACKGROUND, 176, 222);

        var region = createRegion();
        var slots = inventory.getSlots();

        var sizeSlot = addDataSlot(DataSlot.standalone());
        sizeSlot.set(slots);

        for (int i = 0; i < slots; i++) {
            int x = i % 9, y = i / 9;
            region.addSlot(new SlotItemHandler(inventory, i, 8 + x * 18, 18 + y * 18) {

                @Override
                public boolean mayPickup(Player playerIn) {
                    return false;
                }

                @Override
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    return false;
                }

                @Override
                public boolean isActive() {
                    return getSlotIndex() < sizeSlot.get();
                }
            });
        }
    }

    public DebugMenu(int id, Inventory playerInv) {
        this(id, playerInv, new ItemStackHandler(9 * 6));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

}
