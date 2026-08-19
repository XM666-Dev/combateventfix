package com.xm666.combateventfix.compat;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;

public class EpicFightHandler {
    public static void handleDamege(LivingIncomingDamageEvent event) {
        VanillaEntityEventHooks.onCalculateDamagePre(event.getEntity(), event.getSource(), event.getAmount(), event::setAmount);
    }
}
