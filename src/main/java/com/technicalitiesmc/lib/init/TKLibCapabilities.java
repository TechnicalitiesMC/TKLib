package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.circuit.placement.ComponentPlacement;
import com.technicalitiesmc.lib.item.TKItem;
import com.technicalitiesmc.lib.util.AccurateTime;
import com.technicalitiesmc.lib.util.DyeHolder;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TKLibCapabilities {

    private static final ResourceLocation CLIENT_ACCURATE_TIME_NAME = new ResourceLocation(TKLib.MODID, "client_accurate_time");
    private static final Capability<AccurateTime> ACCURATE_TIME_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void onCapabilityRegistration(RegisterCapabilitiesEvent event) {
        event.register(TKItem.DataStore.class);
        event.register(ComponentPlacement.class);
        event.register(DyeHolder.class);
        event.register(AccurateTime.class);
    }

    public static void onAttachLevelCapabilities(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject().isClientSide()) {
            var value = LazyOptional.of(() -> new AccurateTime.Client());
            attachCapability(event, CLIENT_ACCURATE_TIME_NAME, ACCURATE_TIME_CAPABILITY, value);
        }
    }

    private static <T> void attachCapability(AttachCapabilitiesEvent<?> event, ResourceLocation name, Capability<T> capability, LazyOptional<? extends T> value) {
        event.addCapability(name, new ICapabilityProvider() {
            @Nonnull
            @Override
            public <V> LazyOptional<V> getCapability(@Nonnull Capability<V> cap, @Nullable Direction side) {
                if (cap == capability) {
                    return value.cast();
                }
                return LazyOptional.empty();
            }
        });
        event.addListener(value::invalidate);
    }

}
