package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.item.TKItem;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class TKLibCapabilities {

    public static void onCapabilityRegistration(RegisterCapabilitiesEvent event) {
        event.register(TKItem.DataStore.class);
    }

}
