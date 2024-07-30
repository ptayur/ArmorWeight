package net.ptayur.armorweight.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.ptayur.armorweight.util.ConfigUtils;

import java.nio.file.Paths;
import java.util.*;

public class ModCommonConfig {
    private static final CommentedFileConfig COMMON_CONFIG = CommentedFileConfig
            .builder(Paths.get("config", "armorweight_common.toml"))
            .sync()
            .autosave()
            .autoreload()
            .preserveInsertionOrder()
            .build();

    private static final Map<String, Boolean> SETTINGS = new LinkedHashMap<>() {{
        put("isMobsAffected", true);
        put("isDependsOnProtection", false);
    }};

    private static final Map<String, String> SETTINGS_COMMENTS = new LinkedHashMap<>() {{
        put("isMobsAffected", "Determines whether a weight will be applied to mobs.");
        put("isDependsOnProtection", "Determines whether weight will depend on the protection value.");
        }};

    private static final Map<String, Integer> THRESHOLDS = new LinkedHashMap<>() {{
        put("Level1EffectThreshold", 8);
        put("Level2EffectThreshold", 14);
        put("Level3EffectThreshold", 18);
    }};

    private static final Map<String, String> THRESHOLDS_COMMENTS = new LinkedHashMap<>() {{
        put("Level1EffectThreshold", """
                Determines from which weight level the corresponding effect will be applied.\

                The values must be in the range [0, 19] and be greater than the previous threshold value.\
                
                Encumbrance I threshold.""");
        put("Level2EffectThreshold", "Encumbrance II threshold.");
        put("Level3EffectThreshold", "Encumbrance III threshold.");
    }};

    private static final Map<String, Number> WEIGHT = new LinkedHashMap<>() {{
        put("minecraft:leather_helmet", 1f);
        put("minecraft:leather_chestplate", 1f);
        put("minecraft:leather_leggings", 1f);
        put("minecraft:leather_boots", 1f);
        put("minecraft:chainmail_helmet", 2f);
        put("minecraft:chainmail_chestplate", 2f);
        put("minecraft:chainmail_leggings", 2f);
        put("minecraft:chainmail_boots", 2f);
        put("minecraft:iron_helmet", 2f);
        put("minecraft:iron_chestplate", 3f);
        put("minecraft:iron_leggings", 2f);
        put("minecraft:iron_boots", 2f);
        put("minecraft:golden_helmet", 3f);
        put("minecraft:golden_chestplate", 4f);
        put("minecraft:golden_leggings", 4f);
        put("minecraft:golden_boots", 3f);
        put("minecraft:diamond_helmet", 4f);
        put("minecraft:diamond_chestplate", 4f);
        put("minecraft:diamond_leggings", 4f);
        put("minecraft:diamond_boots", 4f);
        put("minecraft:netherite_helmet", 5f);
        put("minecraft:netherite_chestplate", 5f);
        put("minecraft:netherite_leggings", 5f);
        put("minecraft:netherite_boots", 5f);
        put("minecraft:turtle_helmet", 3f);
    }};

    private static final Map<String, String> WEIGHT_COMMENTS = new LinkedHashMap<>() {{
        put("minecraft:leather_helmet", """
                Determines the weight of armor elements. Values must be in the range [0, 20].\
                
                Example:"[mod_id]:[item_id]" = [value].""");
    }};

    private static final Map<String, List<Map<String, ?>>> SECTIONS_MAPPING = new LinkedHashMap<>() {{
        put("Settings", new ArrayList<>() {{
            add(SETTINGS);
            add(SETTINGS_COMMENTS);
        }});
        put("Thresholds", new ArrayList<>() {{
            add(THRESHOLDS);
            add(THRESHOLDS_COMMENTS);
        }});
        put("Weight", new ArrayList<>() {{
            add(WEIGHT);
            add(WEIGHT_COMMENTS);
        }});
    }};

    private static final Map<String, Boolean> LOADED_SETTINGS = new HashMap<>();

    private static final List<Integer> LOADED_THRESHOLDS = new ArrayList<>();

    private static final Map<String, Float> LOADED_WEIGHT_MAPPING = new HashMap<>();

    public static void initConfig() {
        COMMON_CONFIG.load();
        if (COMMON_CONFIG.isEmpty()) {
            ConfigUtils.createCommentedConfig(COMMON_CONFIG, SECTIONS_MAPPING);
        } else {
            ConfigUtils.checkMissingEntries(COMMON_CONFIG, SECTIONS_MAPPING);
            ConfigUtils.checkSettingsValues(COMMON_CONFIG, SETTINGS);
            ConfigUtils.checkThresholdsValues(COMMON_CONFIG, THRESHOLDS);
            ConfigUtils.checkWeightValues(COMMON_CONFIG, WEIGHT);
        }
        ConfigUtils.loadSettingsSection(COMMON_CONFIG, LOADED_SETTINGS);
        ConfigUtils.loadThresholdsSection(COMMON_CONFIG, LOADED_THRESHOLDS);
        ConfigUtils.loadWeightSection(COMMON_CONFIG, LOADED_WEIGHT_MAPPING);
    }

    public static boolean getConfigSettings(String setting) {
        return LOADED_SETTINGS.get(setting);
    }

    public static List<Integer> getConfigThresholds() {
        return LOADED_THRESHOLDS;
    }

    public static float getConfigWeight(String registryName) {
        return LOADED_WEIGHT_MAPPING.getOrDefault(registryName, 0f);
    }

    public static Map<String, Float> getConfigWeightMapping() {
        return LOADED_WEIGHT_MAPPING;
    }
}