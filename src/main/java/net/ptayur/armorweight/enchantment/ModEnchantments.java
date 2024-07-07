package net.ptayur.armorweight.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ptayur.armorweight.ArmorWeight;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENT =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ArmorWeight.MOD_ID);

    public  static RegistryObject<Enchantment> LIGHTNESS =
            ENCHANTMENT.register("lightness",
                    () -> new LightnessEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.FEET));

    public static void register(IEventBus eventBus) {
        ENCHANTMENT.register(eventBus);
    }
}
