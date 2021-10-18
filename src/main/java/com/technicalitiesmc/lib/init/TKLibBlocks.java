package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.block.multipart.MultipartBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class TKLibBlocks {

    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, TKLib.MODID);

    public static final RegistryObject<Block> CIRCUIT = REGISTRY.register("circuit", MultipartBlock::new);

}
