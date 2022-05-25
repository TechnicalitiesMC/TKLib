package com.technicalitiesmc.lib.compat.jei;

import com.technicalitiesmc.lib.client.screen.TKMenuScreen;
import com.technicalitiesmc.lib.menu.slot.TKGhostSlot;
import com.technicalitiesmc.lib.network.TKLibNetworkHandler;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class GhostSlotHandler<S extends TKMenuScreen<?>> implements IGhostIngredientHandler<S> {

    @Override
    public <I> List<Target<I>> getTargets(S gui, I ingredient, boolean doStart) {
        if (!(ingredient instanceof ItemStack)) {
            return List.of();
        }
        int top = gui.getGuiTop(), left = gui.getGuiLeft();
        return gui.getMenu().slots.stream()
                .filter(Slot::isActive)
                .filter(TKGhostSlot.class::isInstance)
                .map(slot -> (Target<I>) new SimpleTarget<I>(
                        slot,
                        new Rect2i(left + slot.x, top + slot.y, 16, 16)
                ))
                .toList();
    }

    @Override
    public void onComplete() {
    }

    record SimpleTarget<I>(Slot slot, Rect2i area) implements IGhostIngredientHandler.Target<I> {

        @Override
        public Rect2i getArea() {
            return area;
        }

        @Override
        public void accept(I ingredient) {
            var stack = (ItemStack) ingredient;
            slot.set(stack);
            TKLibNetworkHandler.sendServerboundGhostSlotSet(slot.index, stack);
        }

    }

}
