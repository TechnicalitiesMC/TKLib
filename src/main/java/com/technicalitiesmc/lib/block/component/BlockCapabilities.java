package com.technicalitiesmc.lib.block.component;

import com.technicalitiesmc.lib.block.BlockComponent;
import com.technicalitiesmc.lib.block.BlockComponentData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class BlockCapabilities extends BlockComponent.WithData<BlockCapabilities.Data> {

    private final Map<Capability<?>, CapabilityInfo<?>> providers;
    private final boolean shouldAutoInvalidate;

    private BlockCapabilities(Context context, Map<Capability<?>, CapabilityInfo<?>> providers) {
        super(context, Data::new);
        this.providers = providers;
        this.shouldAutoInvalidate = providers.values().stream().anyMatch(CapabilityInfo::autoInvalidate);
    }

    public static Builder of() {
        return new Builder();
    }

    public void invalidate(Level level, BlockPos pos, BlockState state, Capability<?> capability) {
        var data = getData(level, pos, state);
        if (data == null) {
            return;
        }
        var cache = data.capabilities.remove(capability);
        if (cache != null) {
            cache.values().forEach(LazyOptional::invalidate);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (newState.is(state.getBlock())) {
            var data = getData(level, pos, state);
            if (data != null) {
                var toRemove = new ArrayList<Capability<?>>();
                data.capabilities.forEach((cap, values) -> {
                    var info = providers.get(cap);
                    for (var property : info.invalidatingProperties()) {
                        if (!state.getValue(property).equals(newState.getValue(property))) {
                            toRemove.add(cap);
                            values.values().forEach(LazyOptional::invalidate);
                            break;
                        }
                    }
                });
                toRemove.forEach(data.capabilities::remove);
            }
        }
    }

    public static class Data extends BlockComponentData<BlockCapabilities> {

        private final Map<Capability<?>, Map<Direction, LazyOptional<?>>> capabilities = new IdentityHashMap<>();
        private final List<LazyOptional<?>> toInvalidate;

        private Data(Context context) {
            super(context);
            this.toInvalidate = getComponent().shouldAutoInvalidate ? new ArrayList<>() : null;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            var cachedMap = capabilities.get(cap);
            if (cachedMap != null) {
                var cached = cachedMap.get(side);
                // Dumb way of checking if still valid, because forge
                if (cached == LazyOptional.empty() || (cached != null && cached.isPresent())) {
                    return cached.cast(); // Still valid
                }
            }

            var info = getComponent().providers.get(cap);
            // If no provider is supported for the given cap, skip
            if (info == null) {
                return LazyOptional.empty();
            }

            // Compute the new value and cache it
            var map = capabilities.computeIfAbsent(cap, $ -> new HashMap<>()); // Not EnumMap because of null keys
            var value = info.provider().getCapability(getLevel(), getBlockPos(), getBlockState(), side);
            map.put(side, value);
            if (info.autoInvalidate()) {
                toInvalidate.add(value);
            }
            return value.cast();
        }

        @Override
        public void invalidateCaps() {
            if (toInvalidate != null) {
                toInvalidate.forEach(LazyOptional::invalidate);
            }
        }

    }

    public static class Builder implements Constructor<BlockCapabilities> {

        private final Map<Capability<?>, CapabilityInfo<?>> providers = new IdentityHashMap<>();

        @Contract("_, _, _, _ -> this")
        public <T> Builder with(Capability<T> capability, Provider<T> provider, boolean autoInvalidate, Property<?>... invalidatingProperties) {
            providers.put(capability, new CapabilityInfo<>(provider, autoInvalidate, invalidatingProperties));
            return this;
        }

        @Contract("_, _, _ -> this")
        public <T> Builder withManaged(Capability<T> capability, ManagedProvider<T> provider, Property<?>... invalidatingProperties) {
            return with(capability, provider, true, invalidatingProperties);
        }

        @Override
        public BlockCapabilities create(Context context) {
            return new BlockCapabilities(context, providers);
        }

    }

    private record CapabilityInfo<T>(Provider<T> provider, boolean autoInvalidate, Property<?>[] invalidatingProperties) {
    }

    @FunctionalInterface
    public interface Provider<T> {

        LazyOptional<T> getCapability(Level level, BlockPos pos, BlockState state, @Nullable Direction side);

    }

    @FunctionalInterface
    public interface ManagedProvider<T> extends Provider<T> {

        @Nullable
        T get(Level level, BlockPos pos, BlockState state, @Nullable Direction side);

        default LazyOptional<T> getCapability(Level level, BlockPos pos, BlockState state, @Nullable Direction side) {
            var value = get(level, pos, state, side);
            return value == null ? LazyOptional.empty() : LazyOptional.of(() -> value);
        }

    }

}
