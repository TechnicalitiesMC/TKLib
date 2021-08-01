package com.technicalitiesmc.lib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("tklib")
public class TKLib {

    public TKLib() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

}
