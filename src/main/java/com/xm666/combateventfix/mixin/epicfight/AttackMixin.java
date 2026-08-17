package com.xm666.combateventfix.mixin.epicfight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.combateventfix.handler.EpicAttackHandler;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AirSlashAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class AttackMixin {
    @Mixin(PlayerPatch.class)
    private static class PlayerPatchMixin {
        @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;attack(Lnet/minecraft/world/entity/Entity;)V"))
        private void wrapAttack(Player instance, Entity target, Operation<Void> original) {
            EpicAttackHandler.isEpicFightAttack = true;
            original.call(instance, target);
            EpicAttackHandler.isEpicFightAttack = false;
        }
    }

    private static class CritMixin {
        @Mixin(AttackAnimation.class)
        private static class AttackAnimationMixin {
            @WrapOperation(method = "hurtCollidingEntities", at = @At(value = "INVOKE", target = "Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;attack(Lyesman/epicfight/world/damagesource/EpicFightDamageSource;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lyesman/epicfight/api/utils/AttackResult;"))
            private AttackResult wrapAttack(LivingEntityPatch<?> instance, EpicFightDamageSource damageSource, Entity target, InteractionHand hand, Operation<AttackResult> original) {
                EpicAttackHandler.isEpicFightCritAttack = (Object) this instanceof AirSlashAnimation;
                var result = original.call(instance, damageSource, target, hand);
                EpicAttackHandler.isEpicFightCritAttack = false;
                return result;
            }
        }

        @Mixin(PlayerPatch.class)
        private static class PlayerPatchMixin {
            @Redirect(method = "attack", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;fallDistance:F", opcode = Opcodes.PUTFIELD))
            private void redirectFallDistance(Player instance, float value) {
            }
        }

        @Mixin(Player.class)
        private static class PlayerMixin {
            @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;fireCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/neoforged/neoforge/event/entity/player/CriticalHitEvent;"))
            private static boolean modifyVanillaCritical(boolean original) {
                if (!EpicAttackHandler.isEpicFightAttack) return original;

                return EpicAttackHandler.isEpicFightCritAttack;
            }

            @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;fireCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/neoforged/neoforge/event/entity/player/CriticalHitEvent;"))
            private static float modifyDamageModifier(float original) {
                if (!EpicAttackHandler.isEpicFightAttack) return original;

                return EpicAttackHandler.isEpicFightCritAttack ? 1.5F : 1.0F;
            }
        }

        @Mixin(AirSlashAnimation.class)
        private static class AirSlashAnimationMixin {
            @Redirect(method = "<init>(FLyesman/epicfight/api/animation/AnimationManager$AnimationAccessor;Lyesman/epicfight/api/asset/AssetAccessor;[Lyesman/epicfight/api/animation/types/AttackAnimation$Phase;)V", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/animation/types/AirSlashAnimation;addProperty(Lyesman/epicfight/api/animation/property/AnimationProperty$AttackPhaseProperty;Ljava/lang/Object;)Lyesman/epicfight/api/animation/types/AttackAnimation;"))
            private AttackAnimation redirectAccessorDamageModifier(AirSlashAnimation instance, AnimationProperty.AttackPhaseProperty<?> attackPhaseProperty, Object o) {
                return null;
            }

            @Redirect(method = "<init>(FLjava/lang/String;Lyesman/epicfight/api/asset/AssetAccessor;[Lyesman/epicfight/api/animation/types/AttackAnimation$Phase;)V", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/animation/types/AirSlashAnimation;addProperty(Lyesman/epicfight/api/animation/property/AnimationProperty$AttackPhaseProperty;Ljava/lang/Object;)Lyesman/epicfight/api/animation/types/AttackAnimation;"))
            private AttackAnimation redirectPathDamageModifier(AirSlashAnimation instance, AnimationProperty.AttackPhaseProperty<?> attackPhaseProperty, Object o) {
                return null;
            }

            @Redirect(method = "spawnHitParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
            private <T extends ParticleOptions> int redirectParticles(ServerLevel instance, T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
                return 0;
            }
        }
    }

    private static class SweepMixin {
        @Mixin(PlayerPatch.class)
        private static class PlayerPatchMixin {
            @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setOnGround(Z)V"))
            private void redirectOnGround(Player instance, boolean b) {
            }
        }

        @Mixin(Player.class)
        private static class PlayerMixin {
            @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/CommonHooks;fireSweepAttack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Z)Lnet/neoforged/neoforge/event/entity/player/SweepAttackEvent;"))
            private boolean modifyVanillaSweep(boolean original) {
                return original && !EpicAttackHandler.isEpicFightAttack;
            }
        }
    }
}
