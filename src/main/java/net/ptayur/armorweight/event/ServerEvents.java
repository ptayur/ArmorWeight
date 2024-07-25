package net.ptayur.armorweight.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ptayur.armorweight.ArmorWeight;
import net.ptayur.armorweight.config.ModCommonConfig;
import net.ptayur.armorweight.networking.ModPackets;
import net.ptayur.armorweight.networking.packet.ThresholdsS2CPacket;
import net.ptayur.armorweight.networking.packet.WeightMappingS2CPacket;
import net.ptayur.armorweight.networking.packet.PlayerWeightS2CPacket;

import java.util.List;
import java.util.Map;

import static net.ptayur.armorweight.util.EffectUtils.applyEffect;
import static net.ptayur.armorweight.util.WeightUtils.getTotalEntityWeight;

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
                    float weight = getTotalEntityWeight(player);
                    applyEffect(player, weight);
                    ModPackets.sendToClient(new PlayerWeightS2CPacket(weight), player);
                }
            } else {
                if (ModCommonConfig.getConfigSettings("isMobsAffected")) {
                    float weight = getTotalEntityWeight(entity);
                    applyEffect(entity, weight);
                }
            }
        }

        @SubscribeEvent
        public static void onGameModeChange(PlayerEvent.PlayerChangeGameModeEvent event) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            float weight = 0;
            if (event.getNewGameMode().isSurvival()) {
                weight = getTotalEntityWeight(player);
            }
            applyEffect(player, weight);
            ModPackets.sendToClient(new PlayerWeightS2CPacket(weight), player);
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            Map<String, Float> weightMapping = ModCommonConfig.getConfigWeightMapping();
            List<Integer> thresholds = ModCommonConfig.getConfigThresholds();
            ModPackets.sendToClient(new WeightMappingS2CPacket(weightMapping), player);
            ModPackets.sendToClient(new PlayerWeightS2CPacket(0), player);
            ModPackets.sendToClient(new ThresholdsS2CPacket(thresholds), player);
        }
    }
}
