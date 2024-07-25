package net.ptayur.armorweight.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.ptayur.armorweight.client.ClientData;

import java.util.List;
import java.util.function.Supplier;

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

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> ClientData.setThresholds(thresholds));
    }
}
