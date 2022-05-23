package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.item.ifo.ItemFunctionalOverride;
import com.technicalitiesmc.lib.item.ifo.SelectRangeOverride;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class TKLibIFOs {

    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(TKLib.MODID, "item_functional_override");
    public static final DeferredRegister<ItemFunctionalOverride.Type> REGISTRY = DeferredRegister.create(REGISTRY_NAME, TKLib.MODID);
    public static final Supplier<IForgeRegistry<ItemFunctionalOverride.Type>> FORGE_REGISTRY = REGISTRY.makeRegistry(
            ItemFunctionalOverride.Type.class,
            () -> new RegistryBuilder<ItemFunctionalOverride.Type>()
                    .setMaxID(Integer.MAX_VALUE - 1)
    );

    public static final RegistryObject<ItemFunctionalOverride.Type> SELECT_RANGE = register("select_range", SelectRangeOverride::new);

    // Helpers
    private static RegistryObject<ItemFunctionalOverride.Type> register(
            String name, Function<FriendlyByteBuf, ItemFunctionalOverride> decoder
    ) {
        return REGISTRY.register(name, () -> new ItemFunctionalOverride.Type(decoder));
    }

}
