package net.ptayur.armorweight.enchantment;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
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
                    () -> new LightnessEnchantment(Enchantment.definition(ItemTags.ARMOR_ENCHANTABLE, 2, 3, new Enchantment.Cost(15, 9), new Enchantment.Cost(15, 50), 3, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.FEET)));

    public static void register(IEventBus eventBus) {
        ENCHANTMENT.register(eventBus);
    }
}
