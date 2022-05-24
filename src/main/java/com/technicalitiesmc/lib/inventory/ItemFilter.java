package com.technicalitiesmc.lib.inventory;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;

public interface ItemFilter {

    boolean test(ItemStack stack);

    int getMatchedAmount(ItemStack stack);

    static ItemFilter none() {
        return None.INSTANCE;
    }

    @Nonnull
    static Builder exactly(int amt) {
        return new Builder(AmountMatchMode.EXACTLY, amt);
    }

    @Nonnull
    static Builder atLeast(int amt) {
        return new Builder(AmountMatchMode.AT_LEAST, amt);
    }

    @Nonnull
    static Builder atMost(int amt) {
        return new Builder(AmountMatchMode.AT_MOST, amt);
    }

    @Nonnull
    static ItemFilter anyOf(ItemFilter... filters) {
        return anyOf(Arrays.asList(filters));
    }

    @Nonnull
    static ItemFilter anyOf(Collection<ItemFilter> filters) {
        if (filters.isEmpty()) {
            return none();
        }
        var simpleFilters = new ArrayList<Simple>();
        for (var filter : filters) {
            if (filter instanceof Simple s) {
                simpleFilters.add(s);
                continue;
            }
            if (filter instanceof Combined c) {
                simpleFilters.addAll(c.filters);
                continue;
            }
            throw new IllegalArgumentException("Only simple and combined filters are allowed to be combined.");
        }
        return new Combined(simpleFilters);
    }

    enum AmountMatchMode {
        EXACTLY((in, ref) -> in == ref ? ref : 0),
        AT_LEAST((in, ref) -> in >= ref ? in : 0),
        AT_MOST(Math::min);

        private final IntBinaryOperator test;

        AmountMatchMode(IntBinaryOperator test) {
            this.test = test;
        }

        public int test(int amount, int reference) {
            return test.applyAsInt(amount, reference);
        }

    }

    final class Builder {

        private final AmountMatchMode mode;
        private final int amount;

        private Builder(AmountMatchMode mode, int amount) {
            this.mode = mode;
            this.amount = amount;
        }

        @Nonnull
        public ItemFilter ofAnyItem() {
            return matching(s -> true);
        }

        @Nonnull
        public ItemFilter of(ItemStack stack) {
            return matching(s -> ItemHandlerHelper.canItemStacksStack(stack, s));
        }

        @Nonnull
        public ItemFilter is(ItemLike item) {
            return matching(s -> s.is(item.asItem()));
        }

        @Nonnull
        public ItemFilter is(TagKey<Item> tag) {
            return matching(s -> s.is(tag));
        }

        @Nonnull
        public ItemFilter isNot(TagKey<Item> tag) {
            return matching(s -> !s.is(tag));
        }

        @Nonnull
        public ItemFilter matching(Predicate<ItemStack> predicate) {
            return new Simple(mode, amount, predicate);
        }

    }

    record None() implements ItemFilter {

        public static final ItemFilter INSTANCE = new None();

        @Override
        public boolean test(ItemStack stack) {
            return false;
        }

        @Override
        public int getMatchedAmount(ItemStack stack) {
            return 0;
        }

    }

    final class Simple implements ItemFilter {

        private final AmountMatchMode mode;
        private final int amount;
        private final Predicate<ItemStack> predicate;

        private Simple(AmountMatchMode mode, int amount, Predicate<ItemStack> predicate) {
            this.mode = mode;
            this.amount = amount;
            this.predicate = predicate;
        }

        @Override
        public boolean test(ItemStack stack) {
            return predicate.test(stack);
        }

        @Override
        public int getMatchedAmount(ItemStack stack) {
            return mode.test.applyAsInt(stack.getCount(), amount);
        }

        public AmountMatchMode getMode() {
            return mode;
        }

        public int getAmount() {
            return amount;
        }

    }

    final class Combined implements ItemFilter {

        private final Collection<Simple> filters;

        private Combined(Collection<Simple> filters) {
            this.filters = filters;
        }

        @Override
        public boolean test(ItemStack stack) {
            for (var filter : filters) {
                if (filter.test(stack)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getMatchedAmount(ItemStack stack) {
            for (var filter : filters) {
                if (filter.test(stack)) {
                    return filter.getMatchedAmount(stack);
                }
            }
            return 0;
        }

        @Nullable
        public Simple getMatchingFilter(ItemStack stack) {
            for (var filter : filters) {
                if (filter.test(stack)) {
                    return filter;
                }
            }
            return null;
        }

    }

}
