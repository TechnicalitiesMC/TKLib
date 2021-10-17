package com.technicalitiesmc.lib.block.multipart;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface BlockSlot extends IForgeRegistryEntry<BlockSlot> {

    @Override
    default BlockSlot setRegistryName(ResourceLocation name) {
        return this;
    }

    @Override
    default Class<BlockSlot> getRegistryType() {
        return BlockSlot.class;
    }

}
