package net.ptayur.armorweight.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.ptayur.armorweight.ArmorWeight;
import net.ptayur.armorweight.client.ClientWeightData;
import net.ptayur.armorweight.client.WeightHudOverlay;

import java.util.List;
import java.util.Map;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = ArmorWeight.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), "hud_weight", WeightHudOverlay.HUD_WEIGHT);
            event.registerBelow(VanillaGuiOverlay.ARMOR_LEVEL.id(), "hud_empty_armor", WeightHudOverlay.HUD_EMPTY_ARMOR);
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
             ResourceLocation registryKey = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
             Map<String, Integer> weightMap = ClientWeightData.getWeightMap();
             if (registryKey == null) {
                 return;
             }
             if (!weightMap.containsKey(registryKey.toString())) {
                 return;
             }
             int weight = weightMap.get(registryKey.toString());
             if (weight == 0) {
                 return;
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
                 String slot = getEquipmentSlot(itemStack).getName().toLowerCase();
                 tooltip.add(insertIndex + 1, Component.translatable("item.modifiers." + slot)
                         .withStyle(ChatFormatting.GRAY));
                 tooltip.add(insertIndex + 2, Component.translatable("tooltip.armorweight.weight", weight)
                         .withStyle(ChatFormatting.BLUE));
             }
         }

         private static EquipmentSlot getEquipmentSlot(ItemStack itemStack) {
             if (itemStack.canEquip(EquipmentSlot.HEAD, null)) {
                 return EquipmentSlot.HEAD;
             } else if (itemStack.canEquip(EquipmentSlot.CHEST, null)) {
                 return EquipmentSlot.CHEST;
             } else if (itemStack.canEquip(EquipmentSlot.LEGS, null)) {
                 return EquipmentSlot.LEGS;
             }
             return EquipmentSlot.FEET;
         }
    }
}
