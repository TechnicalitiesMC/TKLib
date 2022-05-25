package com.technicalitiesmc.lib.mixin.client;

import com.technicalitiesmc.lib.TKLibEventHandler;
import com.technicalitiesmc.lib.network.TKLibNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    protected int missTime;

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    public void startAttack(CallbackInfoReturnable<Boolean> cir) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || !(mc.hitResult instanceof BlockHitResult blockHit)) {
            return;
        }

        var pos = blockHit.getBlockPos();
        var state = mc.level.getBlockState(pos);
        if (TKLibEventHandler.validate(mc.player, state)) {
            TKLibNetworkHandler.sendServerboundQuickBreak(pos);

            mc.player.swing(InteractionHand.MAIN_HAND);
            mc.particleEngine.destroy(pos, state);
            var sound = state.getSoundType(mc.level, pos, mc.player);
            mc.level.playLocalSound(pos, sound.getBreakSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F, false);

            missTime = 2;
            cir.setReturnValue(false);
            cir.cancel(); // Prevents the rest of this method, as well as the continue breaking method from running
        }
    }

}
