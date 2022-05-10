package com.technicalitiesmc.lib.item;

import com.technicalitiesmc.lib.util.value.Value;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TKItem extends Item {

    private static final Capability<DataStore> DATA_STORE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private final List<DataHandle<?>> dataClasses = new ArrayList<>();
    private final Map<Capability, Function<ItemStack, ? extends LazyOptional>> capabilitySuppliers = new HashMap<>();

    public TKItem(Properties properties) {
        super(properties);
    }

    // Data management and initialization

    protected final <T extends INBTSerializable<CompoundTag>> DataHandle<T> addData(String name, DataFactory<T> factory) {
        return addData(name, INBTSerializable::serializeNBT, (stack, saveCallback, tag) -> {
            var instance = factory.create(stack, saveCallback);
            instance.deserializeNBT(tag);
            return instance;
        });
    }

    protected final <T> DataHandle<T> addData(String name, DataSerializer<T> serializer, DataDeserializer<T> deserializer) {
        var handle = new DataHandle<>(name, serializer, deserializer);
        dataClasses.add(handle);
        return handle;
    }

    protected final <T extends INBTSerializable<CompoundTag>> DataHandle<Value<T>> addDataValue(String name, DataFactory<T> factory) {
        return addData(name, val -> val.get().serializeNBT(), (stack, saveCallback, tag) -> {
            var instance = factory.create(stack, saveCallback);
            if (instance != null) {
                instance.deserializeNBT(tag);
            }
            return new Value.Notifying<>(instance, saveCallback);
        });
    }

    protected final <T> DataHandle<Value<T>> addDataValue(String name, DataSerializer<T> serializer, DataDeserializer<T> deserializer) {
        return addData(name, val -> serializer.serialize(val.get()), (stack, saveCallback, tag) -> {
            var instance = deserializer.deserialize(stack, saveCallback, tag);
            return new Value.Notifying<>(instance, saveCallback);
        });
    }

    protected final <T> void addCapability(Capability<T> capability, Function<ItemStack, LazyOptional<T>> supplier) {
        capabilitySuppliers.put(capability, supplier);
    }

    protected final <T> void addRawCapability(Capability<T> capability, Function<ItemStack, T> supplier) {
        capabilitySuppliers.put(capability, stack -> LazyOptional.of(() -> supplier.apply(stack)));
    }

    // Helpers

    protected final InteractionResultHolder<ItemStack> openMenu(Level level, Player player, ItemStack stack, MenuConstructor constructor) {
        return openMenu(level, player, stack, constructor, getDefaultContainerName());
    }

    protected final InteractionResultHolder<ItemStack> openMenu(Level level, Player player, ItemStack stack, MenuConstructor constructor, Component title) {
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider(constructor, title));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    // Implementation

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        var dataStore = LazyOptional.of(() -> new DataStore(stack));
        var capabilityCache = new HashMap<Capability, LazyOptional>();
        return new ICapabilityProvider() {
            private <T> LazyOptional<T> computeCached(Capability<T> capability) {
                var supplier = capabilitySuppliers.get(capability);
                return supplier != null ? supplier.apply(stack) : null;
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
                if (capability == DATA_STORE_CAPABILITY) {
                    return dataStore.cast();
                }

                var customCap = TKItem.this.getCapability(stack, capability);
                if (customCap.isPresent()) {
                    return customCap;
                }

                var cached = capabilityCache.computeIfAbsent(capability, this::computeCached);
                return cached != null ? cached : LazyOptional.empty();
            }
        };
    }

    protected <T> LazyOptional<T> getCapability(ItemStack stack, Capability<T> capability) {
        return LazyOptional.empty();
    }

    public final Component getDefaultContainerName() {
        var name = getRegistryName();
        return new TranslatableComponent("container." + name.getNamespace() + "." + name.getPath());
    }

    public class DataHandle<T> {

        private final String name;
        private final DataSerializer<T> serializer;
        private final DataDeserializer<T> deserializer;

        private DataHandle(
                String name,
                DataSerializer<T> serializer,
                DataDeserializer<T> deserializer
        ) {
            this.name = name;
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        public T of(ItemStack stack) {
            return stack.getCapability(DATA_STORE_CAPABILITY).orElse(null).get(this);
        }

        public void save(ItemStack stack) {
            stack.getCapability(DATA_STORE_CAPABILITY).orElse(null).save(this);
        }

    }

    public class DataStore {

        private final ItemStack stack;
        private final Map<DataHandle, Object> instances = new HashMap<>();

        private DataStore(ItemStack stack) {
            this.stack = stack;
        }

        protected <T> T get(DataHandle<T> handle) {
            return (T) instances.computeIfAbsent(handle, h -> h.deserializer.deserialize(stack, () -> save(h), stack.getOrCreateTagElement("d_" + h.name)));
        }

        protected <T> void save(DataHandle<T> handle) {
            var data = (T) instances.get(handle);
            if (data != null) {
                stack.addTagElement("d_" + handle.name, handle.serializer.serialize(data));
            }
        }

    }

    @FunctionalInterface
    public interface DataFactory<T> {

        T create(ItemStack stack, Runnable saveCallback);

    }

    @FunctionalInterface
    public interface DataSerializer<T> {

        CompoundTag serialize(T data);

    }

    @FunctionalInterface
    public interface DataDeserializer<T> {

        T deserialize(ItemStack stack, Runnable saveCallback, CompoundTag tag);

    }

}
