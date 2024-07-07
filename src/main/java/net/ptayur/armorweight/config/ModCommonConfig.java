package net.ptayur.armorweight.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static net.ptayur.armorweight.ArmorWeight.LOGGER;


public class ModCommonConfig {
    private static final CommentedFileConfig COMMON_CONFIG = CommentedFileConfig.builder(Paths.get("config", "armorweight_common.toml"))
            .sync()
            .autosave()
            .preserveInsertionOrder()
            .build();

    public static void initConfigs() {
        if (!Files.exists(COMMON_CONFIG.getNioPath())) {
            initCommon();
        } else {
            validateCommonConfig();
        }
    }

    private static void initCommon() {
        COMMON_CONFIG.set("Settings.isMobsAffected", true);
        COMMON_CONFIG.setComment("Settings.isMobsAffected", "Determines whether weight will be applied to mobs.");
        COMMON_CONFIG.set("Weight.minecraft:leather_helmet", 1);
        COMMON_CONFIG.setComment("Weight.minecraft:leather_helmet", "Determines the weight of armor elements. Example:\n\"[mod_id]:[item_id]\" = [value]. Values range from 0 to 20");
        COMMON_CONFIG.set("Weight.minecraft:leather_chestplate", 1);
        COMMON_CONFIG.set("Weight.minecraft:leather_leggings", 1);
        COMMON_CONFIG.set("Weight.minecraft:leather_boots", 1);
        COMMON_CONFIG.set("Weight.minecraft:chainmail_helmet", 2);
        COMMON_CONFIG.set("Weight.minecraft:chainmail_chestplate", 2);
        COMMON_CONFIG.set("Weight.minecraft:chainmail_leggings", 2);
        COMMON_CONFIG.set("Weight.minecraft:chainmail_boots", 2);
        COMMON_CONFIG.set("Weight.minecraft:iron_helmet", 2);
        COMMON_CONFIG.set("Weight.minecraft:iron_chestplate", 3);
        COMMON_CONFIG.set("Weight.minecraft:iron_leggings", 2);
        COMMON_CONFIG.set("Weight.minecraft:iron_boots", 2);
        COMMON_CONFIG.set("Weight.minecraft:golden_helmet", 3);
        COMMON_CONFIG.set("Weight.minecraft:golden_chestplate", 4);
        COMMON_CONFIG.set("Weight.minecraft:golden_leggings", 4);
        COMMON_CONFIG.set("Weight.minecraft:golden_boots", 3);
        COMMON_CONFIG.set("Weight.minecraft:diamond_helmet", 4);
        COMMON_CONFIG.set("Weight.minecraft:diamond_chestplate", 4);
        COMMON_CONFIG.set("Weight.minecraft:diamond_leggings", 4);
        COMMON_CONFIG.set("Weight.minecraft:diamond_boots", 4);
        COMMON_CONFIG.set("Weight.minecraft:netherite_helmet", 5);
        COMMON_CONFIG.set("Weight.minecraft:netherite_chestplate", 5);
        COMMON_CONFIG.set("Weight.minecraft:netherite_leggings", 5);
        COMMON_CONFIG.set("Weight.minecraft:netherite_boots", 5);
        COMMON_CONFIG.set("Weight.minecraft:turtle_helmet", 3);
    }

    private static void validateCommonConfig() {
        COMMON_CONFIG.load();
        CommentedConfig settings = COMMON_CONFIG.get("Settings");
        for (Map.Entry<String, Object> entry : settings.valueMap().entrySet()) {
            if (!(entry.getValue() instanceof Boolean)) {
                String registryName = entry.getKey();
                COMMON_CONFIG.set("Settings." + registryName, true);
                LOGGER.warn("The entry \"{}\" in \"armorweight_common.toml\" is incorrect. Value changed to \"true\"",
                        "Settings." + registryName);
            }
        }
        CommentedConfig weightMap = COMMON_CONFIG.get("Weight");
        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, Object> entry : weightMap.valueMap().entrySet()) {
            String registryName = entry.getKey();
            ResourceLocation registryKey = new ResourceLocation(registryName);
            Item item = ForgeRegistries.ITEMS.getValue(registryKey);
            if (!ForgeRegistries.ITEMS.containsKey(registryKey) || item == null) {
                keysToRemove.add("Weight." + registryName);
                LOGGER.warn("The entry \"{}\" in \"armorweight_common.toml\" doesn't exist in the registry. The entry has been deleted",
                        "Weight." + registryName);
                continue;
            }
            if (!isWearable(new ItemStack(item))) {
                keysToRemove.add("Weight." + registryName);
                LOGGER.warn("The entry \"{}\" in \"armorweight_common.toml\" can't be worn. The entry has been deleted",
                        "Weight." + registryName);
                continue;
            }
            if(!(entry.getValue() instanceof Integer)) {
                COMMON_CONFIG.set("Weight." + registryName, 0);
                LOGGER.warn("The entry \"{}\" in \"armorweight_common.toml\" is incorrect. Value changed to \"0\"",
                        "Weight." + registryName);
                continue;
            }
            int weight = (int) entry.getValue();
            if (weight < 0) {
                COMMON_CONFIG.set("Weight." + registryName, 0);
                LOGGER.warn("The entry \"{}\" in \"armorweight_common.toml\" is below 0. Value changed to \"0\"",
                        "Weight." + registryName);
            } else if (weight > 20) {
                COMMON_CONFIG.set("Weight." + registryName, 20);
                LOGGER.warn("The entry \"{}\" in \"armorweight_common.toml\" is above 20. Value changed to \"20\"",
                        "Weight." + registryName);
            }
        }
        for (String key : keysToRemove) {
            COMMON_CONFIG.remove(key);
        }
        COMMON_CONFIG.load();
    }

    private static boolean isWearable(ItemStack itemStack) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR && itemStack.getItem().canEquip(itemStack, slot, null)) {
                return true;
            }
        }
        return false;
    }

    public static boolean getConfigSettings(String setting) {
        return COMMON_CONFIG.getOrElse("Settings." + setting, true);
    }

    public static int getConfigWeight(String registryName) {
        return COMMON_CONFIG.getOrElse("Weight." + registryName, 0);
    }

    public static Map<String, Integer> getWeightMap() {
        CommentedConfig config = COMMON_CONFIG.get("Weight");
        Map<String, Integer> weightMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : config.valueMap().entrySet()) {
            weightMap.put(entry.getKey(), (int) entry.getValue());
        }
        return weightMap;
    }
}