package com.technicalitiesmc.lib.circuit.component;

import com.technicalitiesmc.lib.util.PropertyMap;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.function.Function;

public class ComponentStateBuilder extends StateDefinition.Builder<ComponentType, ComponentState> {

    private final PropertyMap.Builder extendedStateBuilder = new PropertyMap.Builder();

    public ComponentStateBuilder(ComponentType componentType) {
        super(componentType);
    }

    @Override
    public ComponentStateBuilder add(Property<?>... properties) {
        return (ComponentStateBuilder) super.add(properties);
    }

    public ComponentStateBuilder addExtended(Property<?>... properties) {
        extendedStateBuilder.add(properties);
        return this;
    }

    public StateDefinition<ComponentType, ComponentState> create() {
        var extendedState = extendedStateBuilder.build();
        var def = super.create(
                ComponentType::getDefaultState,
                (owner, values, propertiesCodec) -> new ComponentState(owner, values, propertiesCodec, extendedState)
        );
        var i = 0;
        for (var state : def.getPossibleStates()) {
            state.setID(i++);
        }
        return def;
    }

    @Deprecated
    @Override
    public StateDefinition<ComponentType, ComponentState> create(
            Function<ComponentType, ComponentState> defaultStateGetter,
            StateDefinition.Factory<ComponentType, ComponentState> stateFactory
    ) {
        return super.create(defaultStateGetter, stateFactory);
    }

}
