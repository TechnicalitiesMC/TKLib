package com.technicalitiesmc.lib.item.ifo;

import com.technicalitiesmc.lib.util.Validator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

public abstract class BlockIFOBase implements ItemFunctionalOverride {

    private final Type type;
    private final BlockPos origin;
    private final Validator validator;

    public BlockIFOBase(RegistryObject<Type> type, BlockPos origin, Validator validator) {
        this.type = type.get();
        this.origin = origin;
        this.validator = validator;
    }

    public BlockIFOBase(RegistryObject<Type> type, FriendlyByteBuf buf) {
        this.type = type.get();
        this.validator = Validator.always();
        this.origin = buf.readBlockPos();
    }

    public BlockPos origin() {
        return origin;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public boolean isValid(Level level, Player player) {
        return validator.isValid();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeBlockPos(origin);
    }

}