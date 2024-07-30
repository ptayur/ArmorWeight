package net.ptayur.armorweight.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.ptayur.armorweight.ArmorWeight;
import net.ptayur.armorweight.client.ClientData;
import net.ptayur.armorweight.util.OverlayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Unique
    private static final ResourceLocation ARMORWEIGHT_ICONS = new ResourceLocation(ArmorWeight.MOD_ID, "textures/gui/sprites/hud/icons.png");
    @Unique
    private static final ResourceLocation ARMOR_EMPTY = new ResourceLocation("textures/gui/sprites/hud/armor_empty.png");

    @Inject(method = "renderArmor", at = @At("TAIL"))
    private static void renderWeight(GuiGraphics guiGraphics, Player player, int y, int heartRows, int height, int x, CallbackInfo ci) {
        int clientWeight = (int) Math.ceil(ClientData.getPlayerWeight());
        if (clientWeight == 0) {
            return;
        }
        List<Integer> thresholds = ClientData.getThresholds();
        List<Integer> startIndices = new ArrayList<>(List.of(
                0,
                thresholds.get(0),
                thresholds.get(1),
                thresholds.get(2)
        ));
        if (Objects.requireNonNull(Minecraft.getInstance().player).getArmorValue() == 0) {
            OverlayUtils.renderEmptyArmor(guiGraphics, ARMOR_EMPTY, x, y - 10);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ARMORWEIGHT_ICONS);
        for (int i = 0; i < thresholds.size(); i++) {
            int threshold = thresholds.get(i);
            int thresholdStart = startIndices.get(i) / 2;
            boolean isOddStart = startIndices.get(i) % 2 != 0;
            if (clientWeight > threshold) {
                OverlayUtils.renderWeight(guiGraphics, ARMORWEIGHT_ICONS, x, y - 10, i, thresholdStart, threshold, isOddStart, threshold % 2 != 0);
            } else {
                OverlayUtils.renderWeight(guiGraphics, ARMORWEIGHT_ICONS, x, y - 10, i, thresholdStart, clientWeight, isOddStart, clientWeight % 2 != 0);
                break;
            }
        }
    }
}
