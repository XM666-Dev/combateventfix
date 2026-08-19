package com.xm666.combateventfix.handler;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;

public class BetterAttackHandler {
    public static boolean isBetterCombatAttack;
    private static Method betterCombatAttackMethod;

    public static Method getBetterCombatAttackMethod() {
        if (betterCombatAttackMethod != null) return betterCombatAttackMethod;

        for (var method : Minecraft.class.getDeclaredMethods()) {
            if (!method.getName().matches("^handler\\$[^$]+\\$bettercombat\\$pre_doAttack$")) continue;

            betterCombatAttackMethod = method;
            return method;
        }

        throw new NullPointerException();
    }
}
