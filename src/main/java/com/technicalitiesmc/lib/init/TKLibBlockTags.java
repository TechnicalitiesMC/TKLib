package com.technicalitiesmc.lib.init;

import com.technicalitiesmc.lib.TKLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class TKLibBlockTags {

    public static final TagKey<Block> WRENCH_BREAKS_INSTANTLY = BlockTags.create(new ResourceLocation(TKLib.MODID, "wrench_breaks_instantly"));
    public static final TagKey<Block> WRENCH_SNEAK_COMPATIBLE = BlockTags.create(new ResourceLocation(TKLib.MODID, "wrench_sneak_compatible"));

}
