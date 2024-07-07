package net.ptayur.armorweight.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.ptayur.armorweight.ArmorWeight;

public class WeightHudOverlay {
    private static final ResourceLocation ARMORWEIGHT_ICONS = new ResourceLocation(ArmorWeight.MOD_ID, "textures/gui/icons.png");
    private static final ResourceLocation DEFAULT_ICONS = new ResourceLocation("textures/gui/icons.png");

    public static final IGuiOverlay HUD_WEIGHT = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        int clientWeight = ClientWeightData.getPlayerWeight();
        if (clientWeight == 0) {
            return;
        }
        int x = (screenWidth / 2) - 91;
        int y = screenHeight - 49;
        int[] thresholds = {8, 14, 18, 20};
        int[] startIndices = {0, 4, 7, 9};
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ARMORWEIGHT_ICONS);
        for (int i = 0; i < thresholds.length; i++) {
            if (clientWeight >= thresholds[i]) {
                for (int j = startIndices[i]; j < clientWeight / 2; j++) {
                    guiGraphics.blit(ARMORWEIGHT_ICONS, x + j * 8, y, i * 9, 0, 9, 9, 36, 18);
                }
            } else {
                for (int j = startIndices[i]; j < clientWeight / 2; j++) {
                    guiGraphics.blit(ARMORWEIGHT_ICONS, x + j * 8, y, i * 9, 0, 9, 9, 36, 18);
                }
                if (clientWeight % 2 != 0) {
                    guiGraphics.blit(ARMORWEIGHT_ICONS, x + clientWeight / 2 * 8, y, i * 9, 9, 9, 9, 36, 18);
                }
                break;
            }
        }
    }));

    public static final IGuiOverlay HUD_EMPTY_ARMOR = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        int clientWeight = ClientWeightData.getPlayerWeight();
        if (clientWeight == 0) {
            return;
        }
        int x = (screenWidth / 2) - 91;
        int y = screenHeight - 49;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, DEFAULT_ICONS);
        for (int i = 0; i < 10; i++) {
            guiGraphics.blit(DEFAULT_ICONS, x + i * 8, y, 16, 9, 9, 9);
        }
    }));
}
