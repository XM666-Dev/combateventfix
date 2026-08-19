package com.xm666.combateventfix.mixin.epicfight;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.combateventfix.handler.EpicAttackHandler;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
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
            @WrapOperation(method = "hurtCollidingEntities", at = @At(value = "INVOKE", target = "Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;attack(Lyesman/epicfight/world/damagesource/EpicFightDamageSource;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lyesman/epicfight/api/utils/AttackResult;", remap = false), remap = false)
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
            @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;", remap = false))
            private boolean modifyVanillaCritical(boolean original) {
                if (!EpicAttackHandler.isEpicFightAttack) return original;

                return EpicAttackHandler.isEpicFightCritAttack;
            }

            @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/ForgeHooks;getCriticalHit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;ZF)Lnet/minecraftforge/event/entity/player/CriticalHitEvent;", remap = false))
            private float modifyDamageModifier(float original) {
                if (!EpicAttackHandler.isEpicFightAttack) return original;

                return EpicAttackHandler.isEpicFightCritAttack ? 1.5F : 1.0F;
            }
        }

        @Mixin(AirSlashAnimation.class)
        private static class AirSlashAnimationMixin {
            @Redirect(method = "<init>(FLyesman/epicfight/api/animation/AnimationManager$AnimationAccessor;Lyesman/epicfight/api/asset/AssetAccessor;[Lyesman/epicfight/api/animation/types/AttackAnimation$Phase;)V", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/animation/types/AirSlashAnimation;addProperty(Lyesman/epicfight/api/animation/property/AnimationProperty$AttackPhaseProperty;Ljava/lang/Object;)Lyesman/epicfight/api/animation/types/AttackAnimation;", remap = false), remap = false)
            private AttackAnimation redirectAccessorDamageModifier(AirSlashAnimation instance, AnimationProperty.AttackPhaseProperty<?> attackPhaseProperty, Object o) {
                return null;
            }

            @Redirect(method = "<init>(FLjava/lang/String;Lyesman/epicfight/api/asset/AssetAccessor;[Lyesman/epicfight/api/animation/types/AttackAnimation$Phase;)V", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/animation/types/AirSlashAnimation;addProperty(Lyesman/epicfight/api/animation/property/AnimationProperty$AttackPhaseProperty;Ljava/lang/Object;)Lyesman/epicfight/api/animation/types/AttackAnimation;", remap = false), remap = false)
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
            @Redirect(method = "attack", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;onGround:Z", opcode = Opcodes.PUTFIELD))
            private void redirectOnGround(Player instance, boolean b) {
            }
        }

        @Mixin(Player.class)
        private static class PlayerMixin {
            @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/minecraftforge/common/ToolAction;)Z", remap = false))
            private boolean modifyVanillaSweep(ItemStack instance, ToolAction toolAction, Operation<Boolean> original) {
                if (!EpicAttackHandler.isEpicFightAttack) return original.call(instance, toolAction);

                return false;
            }
        }
    }
}
