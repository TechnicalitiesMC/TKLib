package com.technicalitiesmc.lib.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public final class TKBlockEntity extends BlockEntity {

    private final Context context = new Context();
    private final Map<String, BlockComponentData> namedData;
    private final Map<BlockComponent, BlockComponentData> componentData;

    public TKBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, (TKBlock.WithEntity) state.getBlock());
    }

    private TKBlockEntity(BlockPos pos, BlockState state, TKBlock.WithEntity block) {
        super(block.entityType.get(), pos, state);
        var namedData = ImmutableMap.<String, BlockComponentData>builder();
        var componentData = ImmutableMap.<BlockComponent, BlockComponentData>builder();
        block.components.forEach((name, component) -> {
            var data = component.createData(context);
            namedData.put(name, data);
            componentData.put(component, data);
        });
        this.namedData = namedData.build();
        this.componentData = componentData.build();
    }

    <T extends BlockComponentData> T get(BlockComponent.WithData<T> component) {
        return (T) componentData.get(component);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        for (BlockComponentData data : componentData.values()) {
            var result = data.getCapability(cap, side);
            if (result.isPresent()) {
                return result;
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        for (BlockComponentData data : componentData.values()) {
            data.invalidateCaps();
        }
        super.invalidateCaps();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag = super.save(tag);
        var componentsTag = new CompoundTag();
        namedData.forEach((name, data) -> {
            componentsTag.put(name, data.save(new CompoundTag()));
        });
        tag.put("components", componentsTag);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        var componentsTag = tag.getCompound("components");
        namedData.forEach((name, data) -> {
            data.load(componentsTag.getCompound(name));
        });
    }

    private class Context implements BlockComponentDataContext {

        @Override
        public Level getLevel() {
            return TKBlockEntity.this.getLevel();
        }

        @Override
        public BlockPos getBlockPos() {
            return TKBlockEntity.this.getBlockPos();
        }

        @Override
        public BlockState getBlockState() {
            return TKBlockEntity.this.getBlockState();
        }

        @Override
        public void markUnsaved() {
            var level = getLevel();
            var pos = getBlockPos();
            if (level.hasChunkAt(pos)) {
                level.getChunkAt(pos).markUnsaved();
            }
        }

        @Override
        public void updateComparators() {
            getLevel().updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
        }

    }

}
