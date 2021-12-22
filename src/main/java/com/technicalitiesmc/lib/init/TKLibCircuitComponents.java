package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.circuit.component.ComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

public class TKLibCircuitComponents {

    public static final DeferredRegister<ComponentType> REGISTRY = DeferredRegister.create(ComponentType.class, TKLib.MODID);

    static {
        REGISTRY.makeRegistry("component_type",
                () -> new RegistryBuilder<ComponentType>()
                        .setName(new ResourceLocation(TKLib.MODID, "component_type"))
                        .setType(ComponentType.class)
                        .setMaxID(Integer.MAX_VALUE - 1)
        );
    }

}
