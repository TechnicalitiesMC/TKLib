package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class TKLibSoundEvents {

    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(SoundEvent.class, TKLib.MODID);

    public static final RegistryObject<SoundEvent> WRENCH = register("wrench", "wrench");

    @Nonnull
    private static RegistryObject<SoundEvent> register(String name, String path) {
        return REGISTRY.register(name, () -> new SoundEvent(new ResourceLocation(TKLib.MODID, path)));
    }

}
