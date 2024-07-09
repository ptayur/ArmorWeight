package net.ptayur.armorweight.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ptayur.armorweight.client.ClientWeightData;

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

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> ClientWeightData.setPlayerWeight(playerWeight));
    }
}
