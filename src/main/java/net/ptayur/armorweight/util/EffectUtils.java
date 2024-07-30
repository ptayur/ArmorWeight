package net.ptayur.armorweight.util;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.ptayur.armorweight.effect.ModEffects;
import net.ptayur.armorweight.config.ModCommonConfig;

import java.util.List;

public class EffectUtils {
    public static void applyEffect(LivingEntity entity, float weight) {
        List<Integer> thresholds = ModCommonConfig.getConfigThresholds();
        entity.removeEffect(ModEffects.ENCUMBRANCE.get());
        if (weight > thresholds.get(2)) {
            entity.addEffect(new MobEffectInstance(ModEffects.ENCUMBRANCE.get(), -1, 2, false, false, true));
        } else if (weight > thresholds.get(1)) {
            entity.addEffect(new MobEffectInstance(ModEffects.ENCUMBRANCE.get(), -1, 1, false, false, true));
        } else if (weight > thresholds.get(0)) {
            entity.addEffect(new MobEffectInstance(ModEffects.ENCUMBRANCE.get(), -1, 0, false, false, true));
        } else {
            entity.removeEffect(ModEffects.ENCUMBRANCE.get());
        }
    }
}
