package com.technicalitiesmc.lib.block;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BlockComponentData implements ICapabilityProvider {

    private final BlockComponentDataContext context;

    protected BlockComponentData(BlockComponentDataContext context) {
        this.context = context;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return LazyOptional.empty();
    }

    public void invalidateCaps() {
    }

    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    public void load(CompoundTag tag) {
    }

    protected final void markUnsaved() {
        context.markUnsaved();
    }

    protected final void updateComparators() {
        context.updateComparators();
    }

}
