package com.technicalitiesmc.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.TickEvent;

@FunctionalInterface
public interface AccurateTime {

    static long of(Level level) {
        if (level.isClientSide()) {
            return level.getCapability(Client.ACCURATE_TIME_CAPABILITY).map(AccurateTime::getTime).orElse(0L);
        }
        return level.getGameTime();
    }

    long getTime();

    class Client implements AccurateTime {

        private static final Capability<AccurateTime> ACCURATE_TIME_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

        private long time;

        @Override
        public long getTime() {
            return time;
        }

        public void tick() {
            time++;
        }

        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.START) {
                var level = Minecraft.getInstance().level;
                if (level != null) {
                    var timeProvider = level.getCapability(Client.ACCURATE_TIME_CAPABILITY).orElse(null);
                    if (timeProvider instanceof Client ctp) {
                        ctp.tick();
                    }
                }
            }
        }

    }

}
