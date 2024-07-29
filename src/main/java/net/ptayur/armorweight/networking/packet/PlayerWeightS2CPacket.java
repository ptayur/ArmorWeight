package net.ptayur.armorweight.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.ptayur.armorweight.client.ClientData;

import java.util.function.Supplier;

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

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> ClientData.setPlayerWeight(playerWeight));
    }
}
