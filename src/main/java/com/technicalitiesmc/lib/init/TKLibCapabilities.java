package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.circuit.placement.ComponentPlacement;
import com.technicalitiesmc.lib.item.ItemPredicate;
import com.technicalitiesmc.lib.item.TKItem;
import com.technicalitiesmc.lib.item.ifo.IFOStorage;
import com.technicalitiesmc.lib.util.AccurateTime;
import com.technicalitiesmc.lib.util.DyeHolder;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TKLibCapabilities {

    private static final ResourceLocation CLIENT_ACCURATE_TIME_NAME = new ResourceLocation(TKLib.MODID, "client_accurate_time");
    private static final ResourceLocation IFO_STORAGE_NAME = new ResourceLocation(TKLib.MODID, "item_functional_override");
    private static final Capability<AccurateTime> ACCURATE_TIME_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private static final Capability<IFOStorage> IFO_STORAGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static void onCapabilityRegistration(RegisterCapabilitiesEvent event) {
        event.register(TKItem.DataStore.class);
        event.register(ComponentPlacement.class);
        event.register(DyeHolder.class);
        event.register(AccurateTime.class);
        event.register(ItemPredicate.class);
        event.register(IFOStorage.class);
    }

    public static void onAttachLevelCapabilities(AttachCapabilitiesEvent<Level> event) {
        if (event.getObject().isClientSide()) {
            var value = LazyOptional.of(() -> new AccurateTime.Client());
            attachCapability(event, CLIENT_ACCURATE_TIME_NAME, ACCURATE_TIME_CAPABILITY, value);
        }
    }

    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            var value = LazyOptional.of(IFOStorage::new);
            attachCapability(event, IFO_STORAGE_NAME, IFO_STORAGE_CAPABILITY, value);
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
