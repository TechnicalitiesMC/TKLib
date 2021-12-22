package com.technicalitiesmc.lib.circuit.component;

import com.mojang.math.Vector3f;
import com.technicalitiesmc.lib.math.VecDirection;
import com.technicalitiesmc.lib.math.VecDirectionFlags;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class CircuitComponent {

    public static final AABB FULL_BLOCK = new AABB(0, 0, 0, 1, 1, 1);

    private final ComponentType type;
    private final ComponentContext context;

    protected CircuitComponent(ComponentType type, ComponentContext context) {
        this.type = type;
        this.context = context;
    }

    public abstract CircuitComponent copyRotated(ComponentContext context, Rotation rotation);

    public ComponentState getState() {
        return type.getDefaultState();
    }

    public AABB getBoundingBox() {
        return FULL_BLOCK;
    }

    public List<ItemStack> getDrops(ServerLevel level, boolean isCreative) {
        if (isCreative) {
            return Collections.emptyList();
        }
        var path = getType().getLootTable();
        var table = level.getServer().getLootTables().get(path);
        var context = new LootContext.Builder(level).create(LootContextParamSets.EMPTY);
        return table.getRandomItems(context);
    }

    @Nullable
    public <T> T getInterface(VecDirection side, Class<T> type) {
        return null;
    }

    public void onAdded() {
    }

    public void beforeRemove() {
    }

    /**
     * Fired at the start of a wave if there are any events queued up or the component is ticking.
     */
    public void update(ComponentEventMap events, boolean tick) {
    }

    /**
     * Fired after {@link #update(ComponentEventMap, boolean)} but before scheduled {@link #updateExternalState(boolean, Runnable)}
     * calls if scheduled via {@link #scheduleSequential()}.
     */
    public void updateSequential() {
    }

    public InteractionResult use(Player player, InteractionHand hand, VecDirection sideHit, Vector3f hit) {
        return InteractionResult.PASS;
    }

    public boolean isTopSolid() {
        return false;
    }

    public void spawnDrops(ComponentHarvestContext context) {
        getDrops(context.getLevel(), context.isCreative()).forEach(context::drop);
    }

    public void harvest(ComponentHarvestContext context) {
        spawnDrops(context);
        removeComponentAt(Vec3i.ZERO, getType().getSlot(), true);
    }

    public void receiveEvent(VecDirection side, CircuitEvent event, ComponentEventMap.Builder builder) {
        builder.add(side, event);
    }

    public CompoundTag save(CompoundTag tag) {
        return tag;
    }

    public void load(CompoundTag tag) {
    }

    // Helpers

    public final ComponentType getType() {
        return type;
    }

    protected final void updateExternalState(boolean reRender, Runnable action) {
        context.updateExternalState(reRender, action);
    }

    protected final void scheduleSequential() {
        context.scheduleSequential();
    }

    protected final void scheduleTick(int delay) {
        context.scheduleTick(delay);
    }

    protected final void sendEvent(CircuitEvent event, boolean adjacentOnly, VecDirection... directions) {
        sendEvent(event, adjacentOnly, VecDirectionFlags.of(directions));
    }

    protected final void sendEvent(CircuitEvent event, boolean adjacentOnly, VecDirectionFlags directions) {
        sendEventAt(Vec3i.ZERO, event, adjacentOnly, directions);
    }

    protected final void sendEventAt(Vec3i offset, CircuitEvent event, boolean adjacentOnly, VecDirectionFlags directions) {
        context.sendEventAt(offset, getType().getSlot(), event, adjacentOnly, directions);
    }

    protected final void removeComponentAt(Vec3i offset, ComponentSlot slot, boolean notifyNeighbors) {
        context.removeComponentAt(offset, slot, notifyNeighbors);
    }

    protected final void scheduleRemoval() {
        context.scheduleRemoval();
    }

    protected final void playSound(SoundEvent sound, SoundSource source, float volume, float pitch) {
        context.playSound(sound, source, volume, pitch);
    }

    protected final boolean isValidPosition(Vec3i offset) {
        return context.isValidPosition(offset);
    }

    protected final boolean isTopSolid(Vec3i offset) {
        return context.isTopSolid(offset);
    }

    @Nullable
    protected final CircuitComponent getComponentAt(Vec3i offset, ComponentSlot slot) {
        return context.getComponentAt(offset, slot);
    }

    @Nullable
    protected final CircuitComponent getSibling(ComponentSlot slot) {
        return getComponentAt(Vec3i.ZERO, slot);
    }

    @Nullable
    protected final CircuitComponent getNeighbor(VecDirection direction, ComponentSlot slot) {
        return getComponentAt(direction.getOffset(), slot);
    }

    @Nullable
    protected final CircuitComponent getNeighbor(VecDirection direction, boolean adjacentOnly) {
        if (direction.getAxis() != Direction.Axis.Y) {
            return getNeighbor(direction, ComponentSlot.DEFAULT);
        }

        var dir = direction.getAxisDirection();
        var slot = this.type.getSlot();
        var pos = Vec3i.ZERO;
        do {
            pos = pos.offset(slot.getOffsetTowards(dir));
            var next = slot.next(dir);
            var component = getComponentAt(pos, next);
            if (component != null) {
                return component;
            }
            slot = next;
        } while (!adjacentOnly && slot != ComponentSlot.DEFAULT);

        return null;
    }

    @Nullable
    protected final <T> T getNeighborInterface(VecDirection direction, Class<T> type, boolean adjacentOnly) {
        var neighbor = getNeighbor(direction, adjacentOnly);
        return neighbor != null ? neighbor.getInterface(direction.getOpposite(), type) : null;
    }

}
