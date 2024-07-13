package net.ptayur.armorweight.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.ptayur.armorweight.ArmorWeight;
import net.ptayur.armorweight.client.ClientWeightData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Unique
    private static final ResourceLocation ARMORWEIGHT_ICONS = new ResourceLocation(ArmorWeight.MOD_ID, "textures/gui/sprites/hud/icons.png");
    @Unique
    private static final ResourceLocation EMPTY_ARMOR = new ResourceLocation("textures/gui/sprites/hud/armor_empty.png");

    @Inject(method = "renderArmor", at = @At("TAIL"))
    private static void renderWeight(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, CallbackInfo ci) {
        int clientWeight = ClientWeightData.getPlayerWeight();
        if (clientWeight == 0) {
            return;
        }
        int[] thresholds = {8, 14, 18, 20};
        int[] startIndices = {0, 4, 7, 9};
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ARMORWEIGHT_ICONS);
        for (int i = 0; i < thresholds.length; i++) {
            if (clientWeight >= thresholds[i]) {
                for (int j = startIndices[i]; j < clientWeight / 2; j++) {
                    guiGraphics.blit(ARMORWEIGHT_ICONS, x + j * 8, y - 10, i * 9, 0, 9, 9, 36, 18);
                }
            } else {
                for (int j = startIndices[i]; j < clientWeight / 2; j++) {
                    guiGraphics.blit(ARMORWEIGHT_ICONS, x + j * 8,  y - 10, i * 9, 0, 9, 9, 36, 18);
                }
                if (clientWeight % 2 != 0) {
                    guiGraphics.blit(ARMORWEIGHT_ICONS, x + clientWeight / 2 * 8, y - 10, i * 9, 9, 9, 9, 36, 18);
                }
                break;
            }
        }
    }

    @Inject(method = "renderArmor", at = @At("HEAD"))
    private static void renderEmptyArmor(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, CallbackInfo ci) {
        int clientWeight = ClientWeightData.getPlayerWeight();
        if (clientWeight == 0 && player.getArmorValue() == 0) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, EMPTY_ARMOR);
        for (int i = 0; i < 10; i++) {
            guiGraphics.blit(EMPTY_ARMOR, x + i * 8, y - 10, 0, 0, 9, 9, 9, 9);
        }
    }
}
