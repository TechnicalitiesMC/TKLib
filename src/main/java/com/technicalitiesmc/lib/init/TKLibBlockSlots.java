package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.block.multipart.BlockSlot;
import com.technicalitiesmc.lib.block.multipart.FaceSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

public class TKLibBlockSlots {

    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(TKLib.MODID, "block_slot");
    public static final DeferredRegister<BlockSlot> REGISTRY = DeferredRegister.create(REGISTRY_NAME, TKLib.MODID);

    static {
        REGISTRY.makeRegistry(BlockSlot.class,
                () -> new RegistryBuilder<BlockSlot>()
                        .setMaxID(Integer.MAX_VALUE - 1)
        );
        registerAll(FaceSlot.class);
    }

    // Helpers
    private static <T extends Enum<T> & BlockSlot> void registerAll(Class<T> type) {
        for (T value : type.getEnumConstants()) {
            REGISTRY.register(value.getRegistryName().getPath(), () -> value);
        }
    }

}
