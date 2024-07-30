package net.ptayur.armorweight.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.ptayur.armorweight.client.ClientData;

public class PlayerWeightS2CPacket {
    private final float playerWeight;

    public PlayerWeightS2CPacket(float playerWeight) {
        this.playerWeight = playerWeight;
    }

    public PlayerWeightS2CPacket(FriendlyByteBuf buf) {
        this.playerWeight = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(playerWeight);
    }

    public void handle(CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> ClientData.setPlayerWeight(playerWeight));
    }
}
