package com.technicalitiesmc.lib.inventory;

import com.technicalitiesmc.lib.util.AbstractFlags8;
import net.minecraftforge.items.IItemHandler;

import java.util.IdentityHashMap;
import java.util.Map;

public class InventoryHintFlags extends AbstractFlags8<InventoryHint, InventoryHintFlags> {

    private static final InventoryHintFlags NONE = new InventoryHintFlags((byte) 0);
    private static final InventoryHintFlags ALL = new InventoryHintFlags((byte) ((1 << InventoryHint.values().length) - 1));

    private static final Map<Class<? extends IItemHandler>, InventoryHintFlags> HINTS = new IdentityHashMap<>();

    public static InventoryHintFlags none() {
        return NONE;
    }

    public static InventoryHintFlags all() {
        return ALL;
    }

    public static InventoryHintFlags of(InventoryHint... hints) {
        return new InventoryHintFlags(makeMask(hints));
    }

    public static InventoryHintFlags deserialize(byte value) {
        return new InventoryHintFlags(value);
    }

    public static InventoryHintFlags of(Class<? extends IItemHandler> type) {
        return HINTS.computeIfAbsent(type, $ -> InventoryHintFlags.none());
    }

    public static InventoryHintFlags of(IItemHandler inventory) {
        return of(inventory.getClass());
    }

    public static void add(Class<? extends IItemHandler> type, InventoryHint... hints) {
        var current = of(type);
        HINTS.put(type, current.and(hints));
    }

    private InventoryHintFlags(byte value) {
        super(value);
    }

    @Override
    protected Class<InventoryHint> getType() {
        return InventoryHint.class;
    }

    @Override
    protected InventoryHintFlags create(byte value) {
        return new InventoryHintFlags(value);
    }

}
