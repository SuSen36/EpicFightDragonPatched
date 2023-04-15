package com.epicdragonfight.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.DragonGroundBattlePhase;

@Mixin(value = DragonGroundBattlePhase.class)
public class MixinDragonGroundBattlePhase {

    @Overwrite
    public float onHurt(DamageSource damagesource, float amount) {
        if (damagesource.isProjectile()) {
            if (damagesource.getDirectEntity() instanceof AbstractArrow) {
                damagesource.getDirectEntity().setSecondsOnFire(1);
            }

            return 0.0F;
        }
        if (!(damagesource instanceof ExtendedDamageSource)) {
            return amount / 2;
        }
            return amount / 1.5f;
    }

}
