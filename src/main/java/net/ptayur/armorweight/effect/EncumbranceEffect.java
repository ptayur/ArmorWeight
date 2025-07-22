package net.ptayur.armorweight.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.ptayur.armorweight.config.ModCommonConfig;

import java.util.UUID;

public class EncumbranceEffect extends MobEffect {
    private static final UUID SLOWNESS_MODIFIER_UUID = UUID.fromString("e5d155b8-7a24-11ec-90d6-0242ac120003");

    public EncumbranceEffect(MobEffectCategory mobEffectCategory, int color){
        super(mobEffectCategory, color);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        AttributeInstance attribute = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
        if (attribute != null) {
            float modifierValue = -ModCommonConfig.getConfigEffectModifier() * (amplifier + 1);
            AttributeModifier existing = attribute.getModifier(SLOWNESS_MODIFIER_UUID);
            if (existing != null) {
                attribute.removeModifier(existing);
            }
            attribute.addTransientModifier(new AttributeModifier(
                    SLOWNESS_MODIFIER_UUID,
                    "Encumbrance slow",
                    modifierValue,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        AttributeInstance attribute = attributeMap.getInstance(Attributes.MOVEMENT_SPEED);
        if (attribute != null) {
            attribute.removeModifier(SLOWNESS_MODIFIER_UUID);
        }
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
    }
}
