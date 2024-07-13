package net.ptayur.armorweight.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class EncumbranceEffect extends MobEffect {
    private static final UUID SLOWNESS_MODIFIER_UUID = UUID.fromString("e5d155b8-7a24-11ec-90d6-0242ac120003");

    public EncumbranceEffect(MobEffectCategory mobEffectCategory, int color){
        super(mobEffectCategory, color);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SLOWNESS_MODIFIER_UUID.toString(), -0.15D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
