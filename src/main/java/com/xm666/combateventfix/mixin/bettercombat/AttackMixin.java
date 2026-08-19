package com.xm666.combateventfix.mixin.bettercombat;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.combateventfix.handler.BetterAttackHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;

public class AttackMixin {
    @Mixin(value = Minecraft.class, priority = 2000)
    private static class MinecraftMixin {
        @TargetHandler(mixin = "net.bettercombat.mixin.client.MinecraftClientInject", name = "pre_doAttack")
        @WrapMethod(method = "@MixinSquared:Handler")
        private void wrapDoAttack(CallbackInfoReturnable<Boolean> info, Operation<Void> original) {
            if (!BetterAttackHandler.isBetterCombatAttack) return;

            original.call(info);
        }

        @WrapOperation(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/event/InputEvent$InteractionKeyMappingTriggered;isCanceled()Z"))
        private boolean wrapCanceled(InputEvent.InteractionKeyMappingTriggered instance, Operation<Boolean> original) {
            var canceled = original.call(instance);
            if (canceled) return true;

            try {
                var method = BetterAttackHandler.getBetterCombatAttackMethod();
                var cir = new CallbackInfoReturnable<Boolean>("", true);
                method.setAccessible(true);

                BetterAttackHandler.isBetterCombatAttack = true;
                method.invoke(this, cir);
                BetterAttackHandler.isBetterCombatAttack = false;

                if (cir.isCancelled()) {
                    instance.setSwingHand(false);
                    return true;
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return false;
        }
    }
}
