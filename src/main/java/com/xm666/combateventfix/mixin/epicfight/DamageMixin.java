package com.xm666.combateventfix.mixin.epicfight;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import yesman.epicfight.platform.neoforge.event.NeoForgeEntityEvent;

public class DamageMixin {
    @Mixin(NeoForgeEntityEvent.class)
    private static class NeoForgeEntityEventMixin {
        @WrapMethod(method = "epicfight$livingDamagePre")
        private static void redirectLivingDamagePre(LivingDamageEvent.Pre event, Operation<Void> original) {
        }
    }
}
