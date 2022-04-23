package com.technicalitiesmc.lib.container.item;

import com.technicalitiesmc.lib.container.item.adapter.ContainerToItemContainerAdapter;
import com.technicalitiesmc.lib.container.item.adapter.ItemHandlerToItemContainerAdapter;
import com.technicalitiesmc.lib.container.item.adapter.ItemContainerToContainerAdapter;
import com.technicalitiesmc.lib.container.item.adapter.ItemContainerToItemHandlerAdapter;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An {@link ItemStack} container.
 *
 * @see ItemContainer.Serializable
 */
public interface ItemContainer {

    /**
     * Creates a new serializable item holder with the specified number of slots.
     *
     * @param slots The number of slots.
     * @return A new serializable item holder.
     */
    @Contract(value="_ -> new",pure=true)
    static @Nonnull ItemContainer.Serializable of(int slots) {
        return new SimpleItemContainer(slots);
    }

    /**
     * Creates a new serializable item holder that replicates the specified items.
     *
     * @param items The items to replicate.
     * @return A new serializable item holder.
     */
    @Contract(value="_ -> new",pure=true)
    static @Nonnull ItemContainer.Serializable copyOf(@Nonnull NonNullList<ItemStack> items) {
        return new SimpleItemContainer(items);
    }

    /**
     * Creates a new item holder that wraps the specified vanilla {@link Container}.
     *
     * @param container The vanilla container.
     * @return A wrapping item holder.
     */
    @Contract(value="_ -> new",pure=true)
    static @Nonnull
    ItemContainer wrap(@Nonnull Container container) {
        return new ContainerToItemContainerAdapter(container);
    }

    /**
     * Creates a new item holder that wraps the specified Forge {@link IItemHandler}.
     *
     * @param itemHandler The item handler.
     * @return A wrapping item holder.
     */
    @Contract(value="_ -> new",pure=true)
    static @Nonnull
    ItemContainer wrap(@Nonnull IItemHandler itemHandler) {
        return new ItemHandlerToItemContainerAdapter(itemHandler);
    }

    /**
     * Gets the size of this item holder.
     *
     * @return The size.
     */
    @Contract(pure = true)
    int size();

    /**
     * Gets the {@link ItemStack} in the specified slot.<br/>
     * This stack must <b>NEVER</b> be modified directly.
     * Any updates must be carried out by calling {@link #set(int, ItemStack)}.
     *
     * @param slot The slot.
     * @return The stack in the slot.
     */
    @Nonnull ItemStack get(int slot);

    /**
     * Sets the {@link ItemStack} in the specified slot.<br/>
     * Callers should call {@link #isValid(int, ItemStack)} before calling this.
     *
     * @param slot The slot.
     * @param stack The new stack.
     */
    void set(int slot, @Nonnull ItemStack stack);

    /**
     * Checks whether an {@link ItemStack} can be put in the specified slot.
     *
     * @param slot The slot.
     * @param stack The stack.
     * @return True if valid, false otherwise.
     */
    @Contract(pure = true)
    boolean isValid(int slot, @Nonnull ItemStack stack);

    /**
     * Gets the maximum stack size allowed in the specified slot.
     *
     * @param slot The slot.
     * @return The maximum allowed stack size.
     */
    @Contract(pure = true)
    default int getMaxStackSize(int slot) {
        return 64;
    }

    /**
     * Clears all the slots in this item holder.
     */
    default void clear() {
        for (int i = 0; i < size(); i++) {
            set(i, ItemStack.EMPTY);
        }
    }

    /**
     * Checks whether this item holder is completely empty.
     *
     * @return True if empty, false otherwise.
     */
    @Contract(pure = true)
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            if (!get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a slice view of this item holder.<br/>
     * Any changes made to the returned item holder will be reflected in this one.
     *
     * @param from Starting index (inclusive).
     * @param to Final index (exclusive).
     * @return The item holder slice.
     */
    @Contract(value = "_, _ -> new", pure = true)
    default @Nonnull
    ItemContainer slice(int from, int to) {
        Objects.checkFromToIndex(from, to, size());
        return new ItemContainerSlice(this, from, to - from);
    }

    /**
     * Creates a Forge {@link IItemHandlerModifiable} instance that reflects this item holder.
     *
     * @return The item handler.
     */
    @Contract(pure = true)
    default @Nonnull IItemHandlerModifiable asItemHandler() {
        return new ItemContainerToItemHandlerAdapter(this);
    }

    /**
     * Creates a vanilla {@link Container} instance that reflects this item holder.
     *
     * @return The container.
     */
    @Contract(pure = true)
    default @Nonnull Container asVanillaContainer() {
        return new ItemContainerToContainerAdapter(this);
    }

    /**
     * Copies all the contents of this item holder to a non-null list of the same size.
     *
     * @return The non-null list.
     */
    @Contract(pure = true)
    default @Nonnull NonNullList<ItemStack> copyToList() {
        var list = NonNullList.withSize(size(), ItemStack.EMPTY);
        for (int i = 0; i < size(); i++) {
            list.set(i, get(i).copy());
        }
        return list;
    }

    /**
     * An {@link ItemContainer} that is guaranteed to be serializable.
     *
     * @see ItemContainer
     */
    interface Serializable extends ItemContainer, INBTSerializable<CompoundTag> {

        /**
         * Creates a {@link CompoundTag} that describes this inventory.
         *
         * @return The tag.
         */
        @Contract(pure = true)
        @Nonnull CompoundTag save();

        /**
         * Loads a {@link CompoundTag} that describes this inventory.
         *
         * @param tag The tag.
         */
        void load(@Nonnull CompoundTag tag);

        @Override
        default CompoundTag serializeNBT() {
            return save();
        }

        @Override
        default void deserializeNBT(CompoundTag tag) {
            load(tag);
        }

    }

}
