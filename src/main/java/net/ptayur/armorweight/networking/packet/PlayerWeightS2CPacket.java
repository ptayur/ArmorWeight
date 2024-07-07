package net.ptayur.armorweight.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.ptayur.armorweight.client.ClientWeightData;

import java.util.function.Supplier;

public class PlayerWeightS2CPacket {
    private final int playerWeight;

    public PlayerWeightS2CPacket(int playerWeight) {
        this.playerWeight = playerWeight;
    }

    public PlayerWeightS2CPacket(FriendlyByteBuf buf) {
        this.playerWeight = buf.readInt();
    }
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(playerWeight);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> ClientWeightData.setPlayerWeight(playerWeight));
    }
}
