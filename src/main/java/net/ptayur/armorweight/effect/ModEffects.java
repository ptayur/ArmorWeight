package net.ptayur.armorweight.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ptayur.armorweight.ArmorWeight;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ArmorWeight.MOD_ID);

    public static final RegistryObject<MobEffect> ENCUMBRANCE
            = MOB_EFFECTS.register("encumbrance", () -> new EncumbranceEffect(MobEffectCategory.HARMFUL, 0x5A6C81));

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
