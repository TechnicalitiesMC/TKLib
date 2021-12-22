package com.technicalitiesmc.lib.circuit.component;

import com.technicalitiesmc.lib.math.VecDirectionFlags;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public interface ComponentContext {

    boolean isValidPosition(Vec3i offset);

    boolean isTopSolid(Vec3i offset);

    CircuitComponent getComponentAt(Vec3i offset, ComponentSlot slot);

    void removeComponentAt(Vec3i offset, ComponentSlot slot, boolean notify);

    void scheduleRemoval();

    void sendEventAt(Vec3i offset, ComponentSlot slot, CircuitEvent event, boolean adjacentOnly, VecDirectionFlags directions);

    void updateExternalState(boolean reRender, Runnable action);

    void scheduleSequential();

    void scheduleTick(int delay);

    void playSound(SoundEvent sound, SoundSource source, float volume, float pitch);

}
