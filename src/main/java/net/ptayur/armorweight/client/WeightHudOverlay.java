package net.ptayur.armorweight.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.ptayur.armorweight.ArmorWeight;
import net.ptayur.armorweight.util.OverlayUtils;

import java.util.ArrayList;
import java.util.List;

public class WeightHudOverlay {
    private static final ResourceLocation ARMORWEIGHT_ICONS = new ResourceLocation(ArmorWeight.MOD_ID, "textures/gui/sprites/hud/icons.png");
    private static final ResourceLocation ARMOR_EMPTY = new ResourceLocation("textures/gui/sprites/hud/armor_empty.png");
    private static long lastHealthTime = 0L;
    private static int lastHealth = -1;
    private static int displayHealth = -1;

    public static final IGuiOverlay HUD_WEIGHT = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        int clientWeight = (int) Math.ceil(ClientData.getPlayerWeight());
        if (clientWeight == 0) {
            return;
        }
        Player player = gui.getMinecraft().player;
        if (player == null) {
            return;
        }
        int playerHealth = (int)Math.ceil(player.getHealth());
        long currentTime = Util.getMillis();
        if (playerHealth != lastHealth && player.invulnerableTime > 0) {
            lastHealthTime = currentTime;
        }
        if (currentTime - lastHealthTime > 1000L) {
            displayHealth = playerHealth;
            lastHealthTime = currentTime;
        }
        lastHealth = playerHealth;
        int heartsRows = (int)Math.ceil((Math.max(player.getMaxHealth(), Math.max(lastHealth, displayHealth)) + player.getAbsorptionAmount()) / 20f);
        int rowHeight = Math.max(10 - (heartsRows - 2), 3);
        int x = screenWidth / 2 - 91;
        int y = screenHeight - 49 - (heartsRows - 1) * rowHeight;
        List<Integer> thresholds = ClientData.getThresholds();
        List<Integer> startIndices = new ArrayList<>(List.of(
                0,
                thresholds.get(0),
                thresholds.get(1),
                thresholds.get(2)
        ));
        if (player.getArmorValue() == 0) {
            OverlayUtils.renderEmptyArmor(guiGraphics, ARMOR_EMPTY, x, y);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ARMORWEIGHT_ICONS);
        for (int i = 0; i < thresholds.size(); i++) {
            int threshold = thresholds.get(i);
            int thresholdStart = startIndices.get(i) / 2;
            boolean isOddStart = startIndices.get(i) % 2 != 0;
            if (clientWeight > threshold) {
                OverlayUtils.renderWeight(guiGraphics, ARMORWEIGHT_ICONS, x, y, i, thresholdStart, threshold, isOddStart, threshold % 2 != 0);
            } else {
                OverlayUtils.renderWeight(guiGraphics, ARMORWEIGHT_ICONS, x, y, i, thresholdStart, clientWeight, isOddStart, clientWeight % 2 != 0);
                break;
            }
        }
    }));
}
