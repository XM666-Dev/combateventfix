package com.xm666.combateventfix.handler;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import yesman.epicfight.api.event.impl.VanillaEntityEventHooks;

@EventBusSubscriber
public class EpicAttackHandler {
    public static boolean isEpicFightAttack;
    public static boolean isEpicFightCritAttack;

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        VanillaEntityEventHooks.onCalculateDamagePre(event.getEntity(), event.getSource(), event.getAmount(), event::setAmount);
    }
}
