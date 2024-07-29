package net.ptayur.armorweight.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ptayur.armorweight.client.ClientData;

import java.util.Map;

public class WeightMappingS2CPacket {
    private final Map<String, Float> weightMapping;

    public WeightMappingS2CPacket(Map<String, Float> weightMapping) {
        this.weightMapping = weightMapping;
    }

    public WeightMappingS2CPacket(FriendlyByteBuf buf) {
        this.weightMapping = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readFloat);
    }
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(weightMapping, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeFloat);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> ClientData.setWeightMapping(weightMapping));
    }
}
