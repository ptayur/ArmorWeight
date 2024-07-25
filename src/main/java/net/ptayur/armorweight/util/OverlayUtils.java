package net.ptayur.armorweight.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class OverlayUtils {
    public static void renderWeight(GuiGraphics guiGraphics, ResourceLocation armorweightIcons, int x, int y, int weightLevel, int thresholdStart, int threshold, boolean isOddStart, boolean isOddThreshold) {
        if (isOddStart) {
            guiGraphics.blit(armorweightIcons, x + thresholdStart * 8, y, weightLevel * 9, 18, 9, 9, 36, 27);
            thresholdStart++;
        }
        for (int j = thresholdStart; j < threshold / 2; j++) {
            guiGraphics.blit(armorweightIcons, x + j * 8, y, weightLevel * 9, 0, 9, 9, 36, 27);
        }
        if (isOddThreshold) {
            guiGraphics.blit(armorweightIcons, x + threshold / 2 * 8, y, weightLevel * 9, 9, 9, 9, 36, 27);
        }
    }

    public static void renderEmptyArmor(GuiGraphics guiGraphics, ResourceLocation defaultIcons, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, defaultIcons);
        for (int i = 0; i < 10; i++) {
            guiGraphics.blit(defaultIcons, x + i * 8, y, 16, 9, 9, 9);
        }
    }
}
