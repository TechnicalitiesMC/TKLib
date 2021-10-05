package com.technicalitiesmc.lib.inventory;

import net.minecraft.nbt.CompoundTag;

public interface SerializableItemHolder extends ItemHolder {

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag tag);

}
