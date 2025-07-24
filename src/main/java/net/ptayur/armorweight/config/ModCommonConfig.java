package net.ptayur.armorweight.config;

import com.electronwill.nightconfig.core.CommentedConfig;
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

    private static final Map<String, Boolean> GENERAL = new LinkedHashMap<>() {{
        put("isMobsAffected", true);
    }};

    private static final Map<String, String> GENERAL_COMMENTS = new LinkedHashMap<>() {{
        put("isMobsAffected", "Determines whether weight effects should be applied to mobs.");
        }};

    private static final Map<String, Number> EFFECT = new LinkedHashMap<>() {{
        put("EffectSpeedModifier", 0.15D);
        put("Level1EffectThreshold", 8);
        put("Level2EffectThreshold", 14);
        put("Level3EffectThreshold", 18);
    }};

    private static final Map<String, String> EFFECT_COMMENTS = new LinkedHashMap<>() {{
        put("EffectSpeedModifier", """
                Multiplier for reducing movement speed per effect level. The value must be in range [0, 1].
                For example, 0.15 means a 15% speed reduction per level.""");
        put("Level1EffectThreshold", """
                Weight thresholds for applying each effect level.
                The values must be in the range [0, 19] and greater than the previous threshold.
                Encumbrance I threshold.""");
        put("Level2EffectThreshold", "Encumbrance II threshold.");
        put("Level3EffectThreshold", "Encumbrance III threshold.");
    }};

    private static final Map<String, Float> WEIGHT = ConfigUtils.initWeightMap();

    private static final Map<String, String> WEIGHT_COMMENTS = new LinkedHashMap<>() {{
        put("Weight", """
                Determines the weight of armor items.
                Format example: "[mod_id]:[item_id]" = [weight].""");
    }};

    private static final Map<String, List<Map<String, ?>>> SECTIONS_MAPPING = new LinkedHashMap<>() {{
        put("General", new ArrayList<>() {{
            add(GENERAL);
            add(GENERAL_COMMENTS);
        }});
        put("Effect", new ArrayList<>() {{
            add(EFFECT);
            add(EFFECT_COMMENTS);
        }});
        put("Weight", new ArrayList<>() {{
            add(WEIGHT);
            add(WEIGHT_COMMENTS);
        }});
    }};

    public static void initConfig() {
        COMMON_CONFIG.load();
        if (COMMON_CONFIG.isEmpty()) {
            ConfigUtils.createCommentedConfig(COMMON_CONFIG, SECTIONS_MAPPING);
        } else {
            ConfigUtils.validateConfigSection(COMMON_CONFIG,
                    "General",
                    GENERAL,
                    true,
                    raw -> (raw instanceof Boolean b) ? Optional.of(b) : Optional.empty(),
                    value -> true,
                    GENERAL::get
            );

            // validate thresholds in Effect section

            ConfigUtils.validateConfigSection(COMMON_CONFIG,
                    "Effect",
                    Map.of(
                            "Level1EffectThreshold", 8,
                            "Level2EffectThreshold", 14,
                            "Level3EffectThreshold", 18
                    ),
                    true,
                    raw -> (raw instanceof Integer i) ? Optional.of(i) : Optional.empty(),
                    value -> value.intValue() >= 0 && value.intValue() <= 19,
                    EFFECT::get
            );

            // validate modifier in Effect section

            ConfigUtils.validateConfigSection(COMMON_CONFIG,
                    "Effect",
                    Map.of(
                            "EffectSpeedModifier", 0.15D
                    ),
                    true,
                    raw -> (raw instanceof Double d) ? Optional.of(d) : Optional.empty(),
                    value -> value.doubleValue() >= 0 && value.doubleValue() <= 1,
                    EFFECT::get
            );
            ConfigUtils.validateConfigSection(COMMON_CONFIG,
                    "Weight",
                    WEIGHT,
                    false,
                    raw -> (raw instanceof Number n) ? Optional.of(n.floatValue()) : Optional.empty(),
                    value -> true,
                    key -> WEIGHT.getOrDefault(key, 0f)
            );
            ConfigUtils.validateThresholdsOrder(COMMON_CONFIG);
        }
    }

    public static List<Map<String, ?>> getSectionsMapping(String section) {
        return SECTIONS_MAPPING.get(section);
    }

    public static boolean getConfigGeneral(String setting) {
        return COMMON_CONFIG.get("General." + setting);
    }

    public static List<Integer> getConfigEffectThresholds() {
        return new ArrayList<>(){{
            add(COMMON_CONFIG.get("Effect.Level1EffectThreshold"));
            add(COMMON_CONFIG.get("Effect.Level2EffectThreshold"));
            add(COMMON_CONFIG.get("Effect.Level3EffectThreshold"));
            add(20);
        }};
    }

    public static float getConfigEffectModifier() {
        CommentedConfig effectSection = COMMON_CONFIG.get("Effect");
        Object value = effectSection.get("EffectSpeedModifier");
        if (value instanceof Number number) {
            return number.floatValue();
        } else {
            return 0.15f;
        }
    }

    public static float getConfigWeight(String registryName) {
        CommentedConfig weightSection = COMMON_CONFIG.get("Weight");
        Object value = weightSection.get(registryName);
        if (value instanceof Number number) {
            return number.floatValue();
        } else {
            return 0f;
        }
    }

    public static Map<String, Float> getConfigWeightMap() {
        Map<String, Float> weightMap = new HashMap<>();
        CommentedConfig weightSection = COMMON_CONFIG.get("Weight");
        if (weightSection != null) {
            for (String key : weightSection.valueMap().keySet()) {
                Object value = weightSection.get(key);
                if (value instanceof Number number) {
                    weightMap.put(key, number.floatValue());
                }
            }
        }
        return weightMap;
    }
}