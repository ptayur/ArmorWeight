package net.ptayur.armorweight.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.ptayur.armorweight.client.ClientData;
import net.ptayur.armorweight.config.ModCommonConfig;
import net.ptayur.armorweight.enchantment.ModEnchantments;

import java.util.Map;

public class WeightUtils {
    public static float getItemWeight(ItemStack itemStack, boolean isServerSide) {
        ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        if (registryKey == null) {
            return 0f;
        }
        float armorItemWeight;
        if (isServerSide) {
            armorItemWeight = ModCommonConfig.getConfigWeight(registryKey.toString());
        } else {
            Map<String, Float> weightMapping = ClientData.getWeightMapping();
            if (weightMapping == null) {
                return 0f;
            } else {
                armorItemWeight = weightMapping.getOrDefault(registryKey.toString(), 0f);
            }
        }
        itemStack.getEnchantments();
        int lightnessLevel = itemStack.getEnchantments().getLevel(ModEnchantments.LIGHTNESS.get());
        return (float) Math.round(armorItemWeight * (1 - 0.15 * lightnessLevel) * 100) / 100;
    }

    public static float getTotalEntityWeight(LivingEntity entity) {
        double weight = 0;
        for (ItemStack itemStack : entity.getArmorSlots()) {
            weight += getItemWeight(itemStack, true);
        }
        return (float) Math.min(weight, 20);
    }
}
