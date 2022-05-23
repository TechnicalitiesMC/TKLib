package com.technicalitiesmc.lib.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public final class TKBlockEntity extends BlockEntity {

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
            var data = component.createData(new Context(component));
            if (component.isSerialized()) {
                namedData.put(name, data);
            }
            componentData.put(component, data);
        });
        this.namedData = namedData.build();
        this.componentData = componentData.build();
    }

    public <T extends BlockComponentData> T get(BlockComponent.WithData<T> component) {
        return (T) componentData.get(component);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        for (var data : componentData.values()) {
            data.onLoad();
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        for (var data : componentData.values()) {
            data.onChunkUnloaded();
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (var data : componentData.values()) {
            data.onRemoved();
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        var builder = new ModelDataMap.Builder();
        for (var data : componentData.values()) {
            data.addModelData(builder);
        }
        return builder.build();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        for (var data : componentData.values()) {
            var result = data.getCapability(cap, side);
            if (result.isPresent()) {
                return result;
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        for (var data : componentData.values()) {
            data.invalidateCaps();
        }
        super.invalidateCaps();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        var componentsTag = new CompoundTag();
        namedData.forEach((name, data) -> {
            componentsTag.put(name, data.save(new CompoundTag()));
        });
        tag.put("components", componentsTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        var componentsTag = tag.getCompound("components");
        namedData.forEach((name, data) -> {
            data.load(componentsTag.getCompound(name));
        });
    }

    @Override
    public CompoundTag getUpdateTag() {
        var tag = super.getUpdateTag();
        var componentsTag = new CompoundTag();
        namedData.forEach((name, data) -> {
            componentsTag.put(name, data.saveDescription(new CompoundTag()));
        });
        tag.put("components", componentsTag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        var componentsTag = tag.getCompound("components");
        namedData.forEach((name, data) -> {
            data.loadDescription(componentsTag.getCompound(name));
        });
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, e -> e.getUpdateTag());
    }

    private class Context implements BlockComponentData.Context {

        private final BlockComponent.WithData<?> component;

        public Context(BlockComponent.WithData<?> component) {
            this.component = component;
        }

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
        public BlockComponent.WithData<?> getComponent() {
            return component;
        }

        @Override
        public void markUnsaved() {
            var level = getLevel();
            if (level != null) {
                var pos = getBlockPos();
                if (level.hasChunkAt(pos)) {
                    level.getChunkAt(pos).setUnsaved(true);
                }
            }
        }

        @Override
        public void updateComparators() {
            var level = getLevel();
            if (level != null) {
                level.updateNeighbourForOutputSignal(getBlockPos(), getBlockState().getBlock());
            }
        }

        @Override
        public void markDataUpdated() {
            TKBlockEntity.this.requestModelDataUpdate();
        }

    }

}
