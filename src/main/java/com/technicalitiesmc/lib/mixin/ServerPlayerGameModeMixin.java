package com.technicalitiesmc.lib.mixin;

import com.technicalitiesmc.lib.TKLibEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    public void afterDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        TKLibEventHandler.onStopBreaking();
    }

}
