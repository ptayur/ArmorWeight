package net.ptayur.armorweight.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.ptayur.armorweight.client.ClientWeightData;

import java.util.Map;
import java.util.function.Supplier;

public class WeightMapS2CPacket {
    private final Map<String, Integer> weightMap;

    public WeightMapS2CPacket(Map<String, Integer> weightMap) {
        this.weightMap = weightMap;
    }

    public WeightMapS2CPacket(FriendlyByteBuf buf) {
        this.weightMap = buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
    }
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(weightMap, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> ClientWeightData.setWeightMap(weightMap));
    }
}
