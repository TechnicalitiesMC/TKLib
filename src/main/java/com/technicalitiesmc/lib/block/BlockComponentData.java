package com.technicalitiesmc.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BlockComponentData<C extends BlockComponent.WithData<?>> implements ICapabilityProvider {

    private final Context context;

    protected BlockComponentData(Context context) {
        this.context = context;
    }

    public void onLoad() {
    }

    public void onChunkUnloaded() {
    }

    public void onRemoved() {
    }

    public void addModelData(ModelDataMap.Builder builder) {

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

    public CompoundTag saveDescription(CompoundTag tag) {
        return tag;
    }

    public void loadDescription(CompoundTag tag) {
    }

    protected final Level getLevel() {
        return context.getLevel();
    }

    protected final BlockPos getBlockPos() {
        return context.getBlockPos();
    }

    protected final BlockState getBlockState() {
        return context.getBlockState();
    }

    protected final C getComponent() {
        return (C) context.getComponent();
    }

    protected final void markUnsaved() {
        context.markUnsaved();
    }

    protected final void updateComparators() {
        context.updateComparators();
    }
    
    protected final void markDataUpdated() {
        context.markDataUpdated();
    }

    @FunctionalInterface
    public interface Constructor<T extends BlockComponentData> {

        T create(Context context);

    }

    public interface Context {

        Level getLevel();

        BlockPos getBlockPos();

        BlockState getBlockState();

        BlockComponent.WithData<?> getComponent();

        void markUnsaved();

        void updateComparators();

        void markDataUpdated();

    }

}
