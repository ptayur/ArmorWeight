package net.ptayur.armorweight.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.ptayur.armorweight.ArmorWeight;
import net.ptayur.armorweight.util.OverlayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WeightHudOverlay {
    private static final ResourceLocation ARMORWEIGHT_ICONS = new ResourceLocation(ArmorWeight.MOD_ID, "textures/gui/sprites/hud/icons.png");
    private static final ResourceLocation ARMOR_EMPTY = new ResourceLocation("textures/gui/sprites/hud/armor_empty.png");

    public static final IGuiOverlay HUD_WEIGHT = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        int clientWeight = (int) Math.ceil(ClientData.getPlayerWeight());
        if (clientWeight == 0) {
            return;
        }
        int x = (screenWidth / 2) - 91;
        int y = screenHeight - 49;
        List<Integer> thresholds = ClientData.getThresholds();
        List<Integer> startIndices = new ArrayList<>(List.of(
                0,
                thresholds.get(0),
                thresholds.get(1),
                thresholds.get(2)
        ));
        if (Objects.requireNonNull(gui.getMinecraft().player).getArmorValue() == 0) {
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
