package net.ptayur.armorweight.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.ptayur.armorweight.ArmorWeight;
import net.ptayur.armorweight.networking.packet.ThresholdsS2CPacket;
import net.ptayur.armorweight.networking.packet.WeightMappingS2CPacket;
import net.ptayur.armorweight.networking.packet.PlayerWeightS2CPacket;

public class ModPackets {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return  packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ArmorWeight.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(PlayerWeightS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayerWeightS2CPacket::new)
                .encoder(PlayerWeightS2CPacket::toBytes)
                .consumerMainThread(PlayerWeightS2CPacket::handle)
                .add();

        net.messageBuilder(WeightMappingS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(WeightMappingS2CPacket::new)
                .encoder(WeightMappingS2CPacket::toBytes)
                .consumerMainThread(WeightMappingS2CPacket::handle)
                .add();

        net.messageBuilder(ThresholdsS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ThresholdsS2CPacket::new)
                .encoder(ThresholdsS2CPacket::toBytes)
                .consumerMainThread(ThresholdsS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
