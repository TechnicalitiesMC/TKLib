package com.technicalitiesmc.lib.client.circuit;

import com.technicalitiesmc.lib.circuit.component.ComponentType;
import net.minecraft.client.renderer.RenderType;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class ComponentRenderTypes {

    private static final Set<RenderType> REQUESTED_TYPES = new HashSet<>();
    private static final Map<ComponentType, Predicate<RenderType>> RENDER_TYPES = new IdentityHashMap<>();

    static {
        REQUESTED_TYPES.add(RenderType.solid());
    }

    public static void requestRenderType(RenderType type) {
        REQUESTED_TYPES.add(type);
    }

    public static Set<RenderType> getRequestedTypes() {
        return REQUESTED_TYPES;
    }

    public static void setRenderType(ComponentType component, RenderType type) {
        setRenderTypeFilter(component, t -> t == type);
        requestRenderType(type);
    }

    public static void setRenderTypeFilter(ComponentType component, Predicate<RenderType> predicate) {
        RENDER_TYPES.put(component, predicate);
    }

    public static boolean shouldRender(ComponentType component, RenderType type) {
        return RENDER_TYPES.getOrDefault(component, ComponentRenderTypes::isSolid).test(type);
    }

    private static boolean isSolid(RenderType type) {
        return type == RenderType.solid();
    }

}
