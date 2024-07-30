package net.ptayur.armorweight.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class EquipmentUtils {
    public static boolean isWearable(ItemStack itemStack) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR && itemStack.getItem().canEquip(itemStack, slot, null)) {
                return true;
            }
        }
        return false;
    }

    public static EquipmentSlot getEquipmentSlot(ItemStack itemStack) {
        if (itemStack.canEquip(EquipmentSlot.HEAD, null)) {
            return EquipmentSlot.HEAD;
        } else if (itemStack.canEquip(EquipmentSlot.CHEST, null)) {
            return EquipmentSlot.CHEST;
        } else if (itemStack.canEquip(EquipmentSlot.LEGS, null)) {
            return EquipmentSlot.LEGS;
        }
        return EquipmentSlot.FEET;
    }
}
