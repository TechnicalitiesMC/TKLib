package com.technicalitiesmc.lib;

import com.technicalitiesmc.lib.init.TKLibCapabilities;
import com.technicalitiesmc.lib.init.TKLibMenus;
import com.technicalitiesmc.lib.network.TKLibNetworkHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TKLib.MODID)
public class TKLib {

    public static final String MODID = "tklib";

    public TKLib() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        bus.addListener(TKLibCapabilities::onCapabilityRegistration);

        TKLibMenus.REGISTRY.register(bus);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TKLibNetworkHandler.registerPackets();
        });
    }

}
