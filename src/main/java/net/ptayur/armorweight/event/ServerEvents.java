package net.ptayur.armorweight.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.ptayur.armorweight.ArmorWeight;
import net.ptayur.armorweight.config.ModCommonConfig;
import net.ptayur.armorweight.effect.ModEffects;
import net.ptayur.armorweight.enchantment.ModEnchantments;
import net.ptayur.armorweight.networking.ModPackets;
import net.ptayur.armorweight.networking.packet.WeightMapS2CPacket;
import net.ptayur.armorweight.networking.packet.PlayerWeightS2CPacket;

import java.util.Map;

public class ServerEvents {
    @Mod.EventBusSubscriber(modid = ArmorWeight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ServerForgeBusEvents {
        @SubscribeEvent
        public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event){
            if (!event.getSlot().isArmor()) {
                return;
            }
            LivingEntity entity = event.getEntity();
            if (entity instanceof ServerPlayer player) {
                if (player.gameMode.getGameModeForPlayer().isSurvival()) {
                    int weight = calculateWeight(player);
                    applyEffect(player, weight);
                    ModPackets.sendToClient(new PlayerWeightS2CPacket(weight), player);
                }
            } else {
                if (ModCommonConfig.getConfigSettings("isMobsAffected")) {
                    int weight = calculateWeight(entity);
                    applyEffect(entity, weight);
                }
            }
        }

        @SubscribeEvent
        public static void onGameModeChange(PlayerEvent.PlayerChangeGameModeEvent event) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            int weight = 0;
            if (event.getNewGameMode().isSurvival()) {
                weight = calculateWeight(player);
            }
            applyEffect(player, weight);
            ModPackets.sendToClient(new PlayerWeightS2CPacket(weight), player);
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            Map<String, Integer> weightMap = ModCommonConfig.getWeightMap();
            ModPackets.sendToClient(new WeightMapS2CPacket(weightMap), player);
            ModPackets.sendToClient(new PlayerWeightS2CPacket(0), player);
        }

        private static int calculateWeight(LivingEntity entity) {
            float weight = 0;
            for (ItemStack itemStack : entity.getArmorSlots()) {
                Item item = itemStack.getItem();
                ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(item);
                if (registryKey != null) {
                    String registryName = registryKey.toString();
                    int armorItemWeight = ModCommonConfig.getConfigWeight(registryName);
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
                    int featherArmorLevel = enchantments.getOrDefault(ModEnchantments.LIGHTNESS.get(), 0);
                    weight += (float) (armorItemWeight * (1 - 0.15 * featherArmorLevel));
                }
            }
            return Math.min(Math.round(weight), 20);
        }

        private static void applyEffect(LivingEntity entity, int weight) {
            entity.removeEffect(ModEffects.ENCUMBRANCE.get());
            if (weight > 18) {
                entity.addEffect(new MobEffectInstance(ModEffects.ENCUMBRANCE.get(), -1, 2, false, false, true));
            } else if (weight > 14) {
                entity.addEffect(new MobEffectInstance(ModEffects.ENCUMBRANCE.get(), -1, 1, false, false, true));
            } else if (weight > 8) {
                entity.addEffect(new MobEffectInstance(ModEffects.ENCUMBRANCE.get(), -1, 0, false, false, true));
            } else {
                entity.removeEffect(ModEffects.ENCUMBRANCE.get());
            }
        }
    }
}
