package com.technicalitiesmc.lib.inventory;

import com.technicalitiesmc.lib.container.item.ItemContainer;
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
import java.util.PrimitiveIterator;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public final class InventoryHelper {

    private static final Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static final IItemHandler EMPTY = new ItemStackHandler(0);

    public static IItemHandler emptyItemHandler() {
        return EMPTY;
    }

    public static Iterable<Integer> find(IItemHandler inventory, Predicate<ItemStack> filter) {
        return () -> IntStream.range(0, inventory.getSlots())
                .filter(s -> filter.test(inventory.getStackInSlot(s)))
                .iterator();
    }

    public static CraftingContainer createCraftingContainer(ItemContainer inventory, int offset) {
        var craftingContainer = new CraftingContainer(new EmptyMenu(), 3, 3);
        for (int i = 0; i < 9; i++) {
            craftingContainer.setItem(i, inventory.get(i + offset));
        }
        return craftingContainer;
    }

    public static boolean matchesFilter(ItemStack filter, ItemStack stack) {
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

}
