package com.technicalitiesmc.lib.inventory;

import com.technicalitiesmc.lib.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class ItemSet implements Iterable<ItemSet.Entry> {

    public static ItemSet of(IItemHandler inventory) {
        var set = new ItemSet();
        var slots = inventory.getSlots();
        for (var i = 0; i < slots; i++) {
            set.add(inventory.getStackInSlot(i));
        }
        return set;
    }

    private final Set<Entry> entries = Utils.newIdentityHashSet();

    @Nullable
    public Entry get(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        for (var entry : entries) {
            if (ItemHandlerHelper.canItemStacksStack(stack, entry.identifier())) {
                return !entry.isEmpty() ? entry : null;
            }
        }
        return null;
    }

    public int count(ItemStack stack) {
        var entry = get(stack);
        return entry != null ? entry.amount() : 0;
    }

    public int add(ItemStack stack) {
        return add(stack, stack.getCount());
    }

    public int add(ItemStack stack, int amount) {
        if (stack.isEmpty()) {
            return 0;
        }
        var entry = get(stack);
        if (entry != null) {
            entry.amount += amount;
            return entry.amount;
        }
        entries.add(new Entry(stack, amount));
        return amount;
    }

    public RemovalResult remove(ItemStack stack) {
        return remove(stack, stack.getCount());
    }

    public RemovalResult remove(ItemStack stack, int amount) {
        if (stack.isEmpty() || amount == 0) {
            return RemovalResult.NONE;
        }
        var entry = get(stack);
        if (entry != null) {
            return entry.shrink(amount);
        }
        return RemovalResult.NONE;
    }

    public ItemSet copy() {
        var set = new ItemSet();
        for (var entry : entries) {
            set.entries.add(new Entry(entry.identifier(), entry.amount()));
        }
        return set;
    }

    @Nonnull
    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public final class Entry {

        private final ItemStack identifier;
        private int amount;

        private Entry(ItemStack identifier, int amount) {
            this.identifier = identifier;
            this.amount = amount;
        }

        public ItemStack identifier() {
            return identifier;
        }

        public int amount() {
            return amount;
        }

        public boolean isEmpty() {
            return amount <= 0;
        }

        public int grow(int amount) {
            this.amount += amount;
            return this.amount;
        }

        public RemovalResult shrink(int amount) {
            var removed = Math.min(this.amount, amount);
            this.amount -= removed;
            if (removed == amount) {
                entries.remove(this);
            }
            return new RemovalResult(amount, this.amount);
        }

        public int split(int amount) {
            var removed = Math.min(this.amount, amount);
            this.amount -= removed;
            if (removed == amount) {
                entries.remove(this);
            }
            return removed;
        }

    }

    public record RemovalResult(int removed, int leftover) {
        private static final RemovalResult NONE = new RemovalResult(0, 0);
    }

}
