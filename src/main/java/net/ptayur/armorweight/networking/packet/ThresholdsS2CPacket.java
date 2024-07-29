package net.ptayur.armorweight.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ptayur.armorweight.client.ClientData;

import java.util.List;

public class ThresholdsS2CPacket {
    private final List<Integer> thresholds;

    public ThresholdsS2CPacket(List<Integer> thresholds) {
        this.thresholds = thresholds;
    }

    public ThresholdsS2CPacket(FriendlyByteBuf buf) {
        this.thresholds = buf.readList(FriendlyByteBuf::readVarInt);
    }
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(thresholds.size());
        for (int threshold : thresholds) {
            buf.writeVarInt(threshold);
        }
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> ClientData.setThresholds(thresholds));
    }
}
