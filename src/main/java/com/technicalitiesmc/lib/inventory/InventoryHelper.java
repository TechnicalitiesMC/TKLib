package com.technicalitiesmc.lib.inventory;

import com.technicalitiesmc.lib.menu.EmptyMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class InventoryHelper {

    public static Iterable<Integer> find(IItemHandler inventory, Predicate<ItemStack> filter) {
        return () -> IntStream.range(0, inventory.getSlots())
                .filter(s -> filter.test(inventory.getStackInSlot(s)))
                .iterator();
    }

    public static CraftingContainer createCraftingContainer(ItemHolder inventory, int offset) {
        var craftingContainer = new CraftingContainer(new EmptyMenu(), 3, 3);
        for (int i = 0; i < 9; i++) {
            craftingContainer.setItem(i, inventory.get(i + offset));
        }
        return craftingContainer;
    }

}
