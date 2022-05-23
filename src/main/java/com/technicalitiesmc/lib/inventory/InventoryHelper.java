package com.technicalitiesmc.lib.inventory;

import com.technicalitiesmc.lib.container.item.ItemContainer;
import com.technicalitiesmc.lib.item.ItemPredicate;
import com.technicalitiesmc.lib.menu.EmptyMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.PrimitiveIterator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class InventoryHelper {

    private static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() { });
    private static final Capability<ItemPredicate> ITEM_PREDICATE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() { });

    private static final IItemHandler EMPTY = new ItemStackHandler(0);

    public static IItemHandler emptyItemHandler() {
        return EMPTY;
    }

    public static Iterable<Integer> find(IItemHandler inventory, Predicate<ItemStack> filter) {
        return () -> IntStream.range(0, inventory.getSlots())
                .filter(s -> filter.test(inventory.getStackInSlot(s)))
                .iterator();
    }

    public static Set<ItemStack> index(IItemHandler inventory) {
        var set = new HashSet<ItemStack>();
        var slots = inventory.getSlots();
        visit:
        for (int i = 0; i < slots; i++) {
            var stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            for (var indexed : set) {
                if (ItemHandlerHelper.canItemStacksStack(indexed, stack)) {
                    indexed.grow(stack.getCount());
                    continue visit;
                }
            }
            set.add(stack.copy());
        }
        return set;
    }

    public static Set<ItemStack> copy(Set<ItemStack> items) {
        var set = new HashSet<ItemStack>();
        for (var stack : items) {
            set.add(stack.copy());
        }
        return set;
    }

    public static CraftingContainer createCraftingContainer(ItemContainer inventory, int offset) {
        var craftingContainer = new CraftingContainer(new EmptyMenu(), 3, 3);
        for (int i = 0; i < 9; i++) {
            craftingContainer.setItem(i, inventory.get(i + offset));
        }
        return craftingContainer;
    }

    public static boolean matchesFilter(ItemStack filter, ItemStack stack) {
        return matchesFilter(filter, stack, false);
    }

    public static boolean matchesFilter(ItemStack filter, ItemStack stack, boolean resultIfEmptyFilter) {
        if (filter.isEmpty()) {
            return resultIfEmptyFilter;
        }
        var cap = filter.getCapability(ITEM_PREDICATE_CAPABILITY);
        if (cap.isPresent()) {
            return cap.orElse(null).test(stack);
        }
        return ItemHandlerHelper.canItemStacksStack(filter, stack);
    }

    @Nullable
    public static IItemHandler getNeighborItemHandler(Level level, BlockPos pos, Direction side) {
        var entity = level.getBlockEntity(pos.relative(side));
        if (entity == null) {
            return null;
        }
        return entity.getCapability(ITEM_HANDLER_CAPABILITY, side.getOpposite()).orElse(null);
    }

    @Nullable
    @Contract("_, _, _, _, true -> !null")
    public static IItemHandler getNeighborItemHandlerOrDropping(Level level, BlockPos pos, Direction side, int maxStacks, boolean dropIfOccluded) {
        if (!level.getBlockState(pos.relative(side)).isAir()) {
            var neighbor = getNeighborItemHandler(level, pos, side);
            if (neighbor != null || !dropIfOccluded) {
                return neighbor;
            }
        }
        return new DroppingItemHandler(level, pos, side, maxStacks);
    }

    @Nullable
    @Contract("_, _, _, _, true -> !null")
    public static IItemHandler getNeighborItemHandlerOrPickingUp(Level level, BlockPos pos, Direction side, AABB area, boolean pickUpIfOccluded) {
        if (!level.getBlockState(pos.relative(side)).isAir()) {
            var neighbor = getNeighborItemHandler(level, pos, side);
            if (neighbor != null || !pickUpIfOccluded) {
                return neighbor;
            }
        }
        return new DroppedItemHandler(level, area);
    }

    public static boolean transferStack(IItemHandler src, IItemHandler dst) {
        return transferStack(src, dst, ItemFilter.atMost(64).ofAnyItem());
    }

    public static boolean transferStack(IItemHandler src, IItemHandler dst, ItemFilter filter) {
        return transferStack(src, dst, filter, ItemHandlerExtractionQuery.defaultVisitOrder(src.getSlots()));
    }

    public static boolean transferStack(IItemHandler src, IItemHandler dst, ItemFilter filter, PrimitiveIterator.OfInt visitOrder) {
        var extractionQuery = new ItemHandlerExtractionQuery(src);
        var extraction = extractionQuery.extract(filter, visitOrder);
        if (extraction.getExtracted().isEmpty()) {
            return false;
        }

        var insertionQuery = new ItemHandlerInsertionQuery(dst);
        var insertion = insertionQuery.insert(extraction.getExtracted());

        var extracted = extraction.getExtracted().getCount();
        var leftover = insertion.getLeftover().getCount();
        var inserted = extracted - leftover;
        if (inserted == 0) {
            return false;
        }

        if (!extraction.commitAtMost(inserted)) {
            return false;
        }

        insertion.commit();
        extractionQuery.commit();
        insertionQuery.commit();
        return true;
    }

    public static ItemStack transferStack(ItemStack stack, IItemHandler dst, boolean simulate) {
        var insertionQuery = new ItemHandlerInsertionQuery(dst);
        var insertion = insertionQuery.insert(stack);
        if (!simulate) {
            insertion.commit();
            insertionQuery.commit();
        }
        return insertion.getLeftover();
    }

    public static ItemStack mergeCountUnchecked(ItemStack first, ItemStack second) {
        if (first.isEmpty()) {
            return second;
        }
        if (second.isEmpty()) {
            return first;
        }
        var result = first.copy();
        result.grow(second.getCount());
        return result;
    }

}
