package com.technicalitiesmc.lib.circuit.component;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Arrays;
import java.util.function.Supplier;

public final class ComponentType extends ForgeRegistryEntry<ComponentType> {

    private final Factory factory;
    private final ClientComponent clientComponent;
    private final ComponentSlot slot;
    private final ComponentSlot[] allSlots;
    private final Supplier<ResourceLocation> lootTable = Suppliers.memoize(() -> {
        var name = getRegistryName();
        return new ResourceLocation(name.getNamespace(), "scmcomponents/" + name.getPath());
    });

    private final StateDefinition<ComponentType, ComponentState> stateDefinition;
    private final ComponentState defaultState;

    public ComponentType(Factory factory, StateBuilder stateBuilder, ClientComponent clientComponent, ComponentSlot slot, ComponentSlot... additionalSlots) {
        this.factory = factory;
        this.clientComponent = clientComponent;
        this.slot = slot;
        this.allSlots = Arrays.copyOf(additionalSlots, additionalSlots.length + 1);
        this.allSlots[additionalSlots.length] = slot;

        var builder = new ComponentStateBuilder(this);
        stateBuilder.createStateDefinition(builder);
        this.stateDefinition = builder.create();
        this.defaultState = stateDefinition.any();
    }

    public CircuitComponent create(ComponentContext context) {
        return factory.create(context);
    }

    public ClientComponent getClientComponent() {
        return clientComponent;
    }

    public StateDefinition<ComponentType, ComponentState> getStateDefinition() {
        return stateDefinition;
    }

    public ComponentState getDefaultState() {
        return defaultState;
    }

    public ComponentSlot getSlot() {
        return slot;
    }

    public ComponentSlot[] getAllSlots() {
        return allSlots;
    }

    public ResourceLocation getLootTable() {
        return lootTable.get();
    }

    @Override
    public String toString() {
        return "ComponentType[" + getRegistryName() + ']';
    }

    @FunctionalInterface
    public interface Factory {

        CircuitComponent create(ComponentContext context);

    }

    @FunctionalInterface
    public interface StateBuilder {

        void createStateDefinition(ComponentStateBuilder builder);

    }

}
