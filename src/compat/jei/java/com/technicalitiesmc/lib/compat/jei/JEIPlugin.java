package com.technicalitiesmc.lib.compat.jei;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.client.screen.GenericMenuScreen;
import com.technicalitiesmc.lib.client.screen.TKMenuScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID = new ResourceLocation(TKLib.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(TKMenuScreen.class, new GhostSlotHandler<>());
        registration.addGhostIngredientHandler(GenericMenuScreen.class, new GhostSlotHandler<>());
    }

}
