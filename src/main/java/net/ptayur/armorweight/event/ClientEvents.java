package net.ptayur.armorweight.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ptayur.armorweight.ArmorWeight;
import net.ptayur.armorweight.client.WeightHudOverlay;
import net.ptayur.armorweight.util.EquipmentUtils;
import net.ptayur.armorweight.util.WeightUtils;

import java.util.List;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = ArmorWeight.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.ARMOR_LEVEL.id(), "weight_level", WeightHudOverlay.HUD_WEIGHT);
        }
    }

    @Mod.EventBusSubscriber(modid = ArmorWeight.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientForgeBusEvents {
        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            if (event.getEntity() == null) {
                return;
            }
            ItemStack itemStack = event.getItemStack();
            if (!EquipmentUtils.isWearable(itemStack)) {
                return;
            }
            float plainWeight = WeightUtils.getItemWeight(itemStack, false);
            if (plainWeight == 0) {
                return;
            }
            Number weight;
            if ((int) plainWeight == plainWeight) {
                weight = (int) plainWeight;
            } else {
                weight = plainWeight;
            }
            List<Component> tooltip = event.getToolTip();
            int insertIndex = tooltip.size();
            boolean itemModifiers = false;
            for (Component entry : tooltip) {
                if (entry.getStyle().getColor() == TextColor.fromLegacyFormat(ChatFormatting.BLUE)) {
                    insertIndex = tooltip.indexOf(entry) + 1;
                    itemModifiers = true;
                }
            }
            if (itemModifiers) {
                tooltip.add(insertIndex, Component.translatable("tooltip.armorweight.weight", weight)
                        .withStyle(ChatFormatting.BLUE));
            } else {
                for (Component entry : tooltip) {
                    if (entry.getStyle().getColor() == TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY)) {
                        insertIndex = tooltip.indexOf(entry);
                        break;
                    }
                }
                tooltip.add(insertIndex, Component.literal(""));
                String slot = EquipmentUtils.getEquipmentSlot(itemStack).getName().toLowerCase();
                tooltip.add(insertIndex + 1, Component.translatable("item.modifiers." + slot)
                         .withStyle(ChatFormatting.GRAY));
                tooltip.add(insertIndex + 2, Component.translatable("tooltip.armorweight.weight", weight)
                        .withStyle(ChatFormatting.BLUE));
            }
        }
    }
}
