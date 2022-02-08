package com.technicalitiesmc.lib.circuit.component;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.math.Vector3f;
import com.mojang.serialization.MapCodec;
import com.technicalitiesmc.lib.math.VecDirection;
import com.technicalitiesmc.lib.util.PropertyMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public class ComponentState extends StateHolder<ComponentType, ComponentState> {

    private static final Supplier<ForgeRegistry<ComponentType>> TYPE_REGISTRY = Suppliers.memoize(() -> {
        return (ForgeRegistry<ComponentType>) RegistryManager.ACTIVE.getRegistry(ComponentType.class);
    });

    private final PropertyMap extendedState;
    private int id = -1;

    protected ComponentState(
            ComponentType owner,
            ImmutableMap<Property<?>, Comparable<?>> values,
            MapCodec<ComponentState> propertiesCodec,
            PropertyMap extendedState
    ) {
        super(owner, values, propertiesCodec);
        this.extendedState = extendedState;
    }

    void setID(int id) {
        this.id = id;
    }

    public ComponentState getRawState() {
        return this;
    }

    public ComponentType getComponentType() {
        return owner;
    }

    public int getID() {
        return id;
    }

    public AABB getBoundingBox() {
        return owner.getClientComponent().getBoundingBox(this);
    }

    public ItemStack getPickedItem() {
        return owner.getClientComponent().getPickedItem(this);
    }

    public void onPicking(Player player) {
        owner.getClientComponent().onPicking(this, player);
    }

    public InteractionResult use(Player player, InteractionHand hand, VecDirection sideHit, Vector3f hit) {
        return owner.getClientComponent().use(this, player, hand, sideHit, hit);
    }

    public boolean isTopSolid() {
        return owner.getClientComponent().isTopSolid(this);
    }

    public int getTint(int tintIndex) {
        return owner.getClientComponent().getTint(this, tintIndex);
    }

    public <T extends Comparable<T>> ComponentState setExtended(Property<T> property, T value) {
        return new Extended(getRawState(), extendedState.setProperty(property, value));
    }

    public <T extends Comparable<T>> T getExtended(Property<T> property) {
        return extendedState.getProperty(property);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentState that = (ComponentState) o;
        return getRawState() == that.getRawState() && extendedState.equals(that.extendedState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRawState(), extendedState);
    }

    public CompoundTag serialize(CompoundTag tag) {
        tag.putString("component", owner.getRegistryName().toString());
        var properties = new CompoundTag();
        getValues().forEach((p, v) -> {
            properties.putString(p.getName(), ((Property)p).getName(v));
        });
        tag.put("properties", properties);
        if (this instanceof Extended) {
            var extendedProperties = new CompoundTag();
            extendedState.getValues().forEach((p, v) -> {
                extendedProperties.putString(p.getName(), p.getName(v));
            });
            tag.put("extended_properties", extendedProperties);
        }
        return tag;
    }

    public void serialize(FriendlyByteBuf buf) {
        buf.writeInt(TYPE_REGISTRY.get().getID(owner));
        buf.writeInt(getRawState().id);
        if (this instanceof Extended) {
            buf.writeBoolean(true);
            extendedState.serialize(buf);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Nullable
    public static ComponentState deserialize(CompoundTag tag) {
        var name = new ResourceLocation(tag.getString("component"));
        var component = TYPE_REGISTRY.get().getValue(name);
        if (component == null) {
            return null;
        }
        var state = component.getDefaultState();

        var properties = tag.getCompound("properties");
        for (var property : state.getProperties()) {
            var val = properties.getString(property.getName());
            var value = property.getValue(val);
            if (value.isPresent()) {
                state = state.setValue((Property) property, (Comparable) value.get());
            }
        }

        if (tag.contains("extended_properties")) {
            var extendedProperties = tag.getCompound("extended_properties");
            for (var property : state.extendedState.getProperties()) {
                var val = extendedProperties.getString(property.getName());
                var value = property.getValue(val);
                if (value.isPresent()) {
                    state = state.setExtended((Property) property, (Comparable) value.get());
                }
            }
        }

        return state;
    }

    @Nullable
    public static ComponentState deserialize(FriendlyByteBuf buf) {
        // Important: Read EVERYTHING at the start in case we return early
        var typeId = buf.readInt();
        var stateId = buf.readInt();
        var partial = buf.readBoolean() ? PropertyMap.PartialDeserialization.deserialize(buf) : null;
        var component = TYPE_REGISTRY.get().getValue(typeId);
        if (component == null) {
            return null;
        }
        var state = component.getStateDefinition().getPossibleStates().get(stateId);
        if (partial != null) {
            var extendedState = state.extendedState.deserialize(partial);
            return new Extended(state, extendedState);
        }
        return state;
    }

    private static class Extended extends ComponentState {

        private final ComponentState parent;

        protected Extended(ComponentState parent, PropertyMap extendedState) {
            super(parent.owner, parent.getValues(), parent.propertiesCodec, extendedState);
            this.parent = parent;
        }

        @Override
        public ComponentState getRawState() {
            return parent;
        }

    }

}
