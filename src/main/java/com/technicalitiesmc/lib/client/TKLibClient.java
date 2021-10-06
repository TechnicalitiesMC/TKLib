package com.technicalitiesmc.lib.client;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.init.TKLibMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TKLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TKLibClient {

    @SubscribeEvent
    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerScreens();
        });
    }

    private static void registerScreens() {
        MenuScreens.register(TKLibMenus.LOCKED_GENERIC_9x3.get(), ContainerScreen::new);
        MenuScreens.register(TKLibMenus.LOCKED_GENERIC_9x6.get(), ContainerScreen::new);
    }

}
