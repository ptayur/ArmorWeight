package net.ptayur.armorweight.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.ptayur.armorweight.effect.ModEffects;
import net.ptayur.armorweight.config.ModCommonConfig;

import java.util.List;

public class EffectUtils {
    public static void applyEffect(LivingEntity entity, float weight) {
        List<Integer> thresholds = ModCommonConfig.getConfigThresholds();
        Holder<MobEffect> encumbranceHolder = entity.registryAccess().registryOrThrow(Registries.MOB_EFFECT).getHolderOrThrow(ModEffects.ENCUMBRANCE.getKey());
        entity.removeEffect(encumbranceHolder);
        if (weight > thresholds.get(2)) {
            entity.addEffect(new MobEffectInstance(encumbranceHolder, -1, 2, false, false, true));
        } else if (weight > thresholds.get(1)) {
            entity.addEffect(new MobEffectInstance(encumbranceHolder, -1, 1, false, false, true));
        } else if (weight > thresholds.get(0)) {
            entity.addEffect(new MobEffectInstance(encumbranceHolder, -1, 0, false, false, true));
        } else {
            entity.removeEffect(encumbranceHolder);
        }
    }
}
