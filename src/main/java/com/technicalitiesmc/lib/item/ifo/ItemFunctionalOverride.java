package com.technicalitiesmc.lib.item.ifo;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ItemFunctionalOverride {

    Type type();

    boolean isValid(Level level, Player player);

    InteractionResult use(Level level, Player player, HitResult hit);

    InteractionResult hit(Level level, Player player, HitResult hit);

    @Nullable
    default Component getTip(Level level, Player player) {
        return null;
    }

    void writeToNetwork(FriendlyByteBuf buf);

    class Type extends ForgeRegistryEntry<Type> {

        private final Function<FriendlyByteBuf, ItemFunctionalOverride> decoder;

        public Type(Function<FriendlyByteBuf, ItemFunctionalOverride> decoder) {
            this.decoder = decoder;
        }

        public ItemFunctionalOverride decode(FriendlyByteBuf buf) {
            return decoder.apply(buf);
        }

    }

}
