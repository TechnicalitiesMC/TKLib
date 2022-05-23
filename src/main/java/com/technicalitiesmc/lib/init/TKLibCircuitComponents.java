package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.circuit.component.ComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

public class TKLibCircuitComponents {

    // TODO: Rename registry to "circuit_component_type"
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(TKLib.MODID, "component_type");
    public static final DeferredRegister<ComponentType> REGISTRY = DeferredRegister.create(REGISTRY_NAME, TKLib.MODID);

    static {
        REGISTRY.makeRegistry(ComponentType.class,
                () -> new RegistryBuilder<ComponentType>()
                        .setMaxID(Integer.MAX_VALUE - 1)
        );
    }

}
