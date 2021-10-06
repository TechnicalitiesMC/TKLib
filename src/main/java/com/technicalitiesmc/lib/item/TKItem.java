package com.technicalitiesmc.lib.item;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TKItem extends Item {

    @CapabilityInject(DataStore.class)
    private static final Capability<DataStore> DATA_STORE_CAPABILITY = null;

    private final List<DataHandle<?>> dataClasses = new ArrayList<>();

    public TKItem(Properties properties) {
        super(properties);
    }

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

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        var dataStore = LazyOptional.of(() -> new DataStore(stack));
        return new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                if (cap == DATA_STORE_CAPABILITY) {
                    return dataStore.cast();
                }
                return TKItem.this.getCapability(stack, cap);
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
