package com.xm666.combateventfix.handler;

import com.xm666.combateventfix.CombatEventFix;
import com.xm666.combateventfix.compat.EpicFightHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@Mod(CombatEventFix.MODID)
public class EpicAttackHandler {
    public static boolean isEpicFightAttack;
    public static boolean isEpicFightCritAttack;

    public EpicAttackHandler(IEventBus eventBus) {
        eventBus.addListener(EpicAttackHandler::onLivingIncomingDamage);
    }

    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        EpicFightHandler.handleDamege(event);
    }
}
