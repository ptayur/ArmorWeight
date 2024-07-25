package net.ptayur.armorweight;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ptayur.armorweight.config.ModCommonConfig;
import net.ptayur.armorweight.effect.ModEffects;
import net.ptayur.armorweight.enchantment.ModEnchantments;

import net.ptayur.armorweight.networking.ModPackets;
import org.slf4j.Logger;

@Mod(ArmorWeight.MOD_ID)
public class ArmorWeight {
    public static final String MOD_ID = "armorweight";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArmorWeight() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        ModEffects.register(modEventBus);
        ModEnchantments.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModPackets::register);
        ModCommonConfig.initConfig();
    }
}
