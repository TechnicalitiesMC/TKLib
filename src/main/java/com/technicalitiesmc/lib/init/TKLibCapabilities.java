package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.circuit.placement.ComponentPlacement;
import com.technicalitiesmc.lib.item.TKItem;
import com.technicalitiesmc.lib.util.DyeHolder;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class TKLibCapabilities {

    public static void onCapabilityRegistration(RegisterCapabilitiesEvent event) {
        event.register(TKItem.DataStore.class);
        event.register(ComponentPlacement.class);
        event.register(DyeHolder.class);
    }

}
