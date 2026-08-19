package com.xm666.combateventfix.handler;

import com.xm666.combateventfix.CombatEventFix;
import com.xm666.combateventfix.compat.EpicFightHandler;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@Mod(CombatEventFix.MODID)
public class EpicAttackHandler {
    public static boolean isEpicFightAttack;
    public static boolean isEpicFightCritAttack;

    public EpicAttackHandler() {
        NeoForge.EVENT_BUS.addListener(EpicAttackHandler::onLivingIncomingDamage);
    }

    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        EpicFightHandler.handleDamage(event);
    }
}
