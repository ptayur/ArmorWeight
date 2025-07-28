package net.ptayur.armorweight.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

import static net.ptayur.armorweight.ArmorWeight.LOGGER;

public class ConfigUtils {
    public static Map<String, Float> initWeightMap() {
        Map<String, Float> weightMap = new LinkedHashMap<>();
        for (Item item : ForgeRegistries.ITEMS) {
            if (item instanceof ArmorItem armorItem) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                if (id != null) {
                    weightMap.put(id.toString(), (float) armorItem.getDefense());
                }
            }
        }
        return weightMap;
    }

    private static void setConfigEntries(CommentedFileConfig config, String path, Map<String, ?> map) {
        if (map == null) {
            return;
        }
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            config.set(path + "." + entry.getKey(), entry.getValue());
        }
    }

    private static void setConfigComments(CommentedFileConfig config, String path, Map<String, ?> map) {
        if (map == null) {
            return;
        }
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (Objects.equals(entry.getKey(), path)) {
                config.setComment(path, (String) entry.getValue());
            } else {
                config.setComment(path + "." + entry.getKey(), (String) entry.getValue());
            }
        }
    }

    public static void setDefaultSections(CommentedFileConfig config, Map<String, List<Map<String, ?>>> sectionsMapping) {
        for (Map.Entry<String, List<Map<String, ?>>> section : sectionsMapping.entrySet()) {
            setConfigComments(config, section.getKey(), section.getValue().get(1));
            setConfigEntries(config, section.getKey(), section.getValue().get(0));
        }
    }

    public static <T> Map<String, T> ensureDefaultPresent(CommentedConfig section, String sectionName, Map<String, T> defaultValues) {
        Map<String, T> rebuild = new LinkedHashMap<>();
        if (section == null) {
            rebuild = defaultValues;
            LOGGER.warn("Section \"{}\" is empty. Restored to default.", sectionName);
            return rebuild;
        }

        // Iterate over default values to preserve their presence in the section

        for (Map.Entry<String, T> defaultEntry : defaultValues.entrySet()) {
            String key = defaultEntry.getKey();
            T value = defaultEntry.getValue();
            if (section.contains(key)) {
                value = section.get(key);
            } else {
                LOGGER.warn("Entry \"{}.{}\" is missing. Restored to default ({}).",
                        sectionName,
                        key,
                        value);
            }
            rebuild.put(key, value);
        }

        return rebuild;
    }

    public static void validateGeneralSection(CommentedFileConfig config, Map<String, List<Map<String, ?>>> sectionsMapping) {
        CommentedConfig generalSection = config.get("General");
        List<Map<String, ?>> generalMapping = sectionsMapping.get("General");
        @SuppressWarnings("unchecked")
        Map<String, Boolean> generalDefault = (Map<String, Boolean>) generalMapping.get(0);
        @SuppressWarnings("unchecked")
        Map<String, String> generalComments = (Map<String, String>) generalMapping.get(1);
        Map<String, Boolean> rebuild = ensureDefaultPresent(generalSection, "General", generalDefault);

        Object isMobsAffected = rebuild.get("isMobsAffected");
        Boolean isMobsAffectedDefault = generalDefault.get("isMobsAffected");
        if (!(isMobsAffected instanceof Boolean)) {
            rebuild.put("isMobsAffected", isMobsAffectedDefault);
            LOGGER.warn("Entry \"General.isMobsAffected\" has invalid type ({}): value is not a boolean. Restored to default ({}).",
                    isMobsAffected,
                    isMobsAffectedDefault);
        }

        // Rewrite section entries

        generalSection.clear();
        for (Map.Entry<String, Boolean> entry : rebuild.entrySet()) {
            generalSection.set(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> commentEntry : generalComments.entrySet()) {
            generalSection.setComment(commentEntry.getKey(), commentEntry.getValue());
        }
        config.set("General", generalSection);
    }

    public static void validateEffectSection(CommentedFileConfig config, Map<String, List<Map<String, ?>>> sectionsMapping) {
        CommentedConfig effectSection = config.get("Effect");
        List<Map<String, ?>> effectMapping = sectionsMapping.get("Effect");
        @SuppressWarnings("unchecked")
        Map<String, Number> effectDefault = (Map<String, Number>) effectMapping.get(0);
        @SuppressWarnings("unchecked")
        Map<String, String> effectComments = (Map<String, String>) effectMapping.get(1);
        Map<String, Number> rebuild = ensureDefaultPresent(effectSection, "Effect", effectDefault);

        // Validate EffectSpeedModifier

        Object effectModifier = rebuild.get("EffectSpeedModifier");
        Number modifierDefault = effectDefault.get("EffectSpeedModifier");
        if (!(effectModifier instanceof Double modifierValue)) {
            rebuild.put("EffectSpeedModifier", modifierDefault);
            LOGGER.warn("Entry \"Effect.EffectSpeedModifier\" has invalid type ({}): value is not a double. Restored to default ({}).",
                    effectModifier,
                    modifierDefault);
        } else {
            if (modifierValue < 0 || modifierValue > 1) {
                rebuild.put("EffectSpeedModifier", modifierDefault);
                LOGGER.warn("Entry \"Effect.EffectSpeedModifier\" has invalid value ({}): it's outside the valid range [0.0, 1.0]." +
                                " Restored to default ({}).",
                        modifierValue,
                        modifierDefault);
            }
        }

        // Validate Thresholds

        List<String> thresholds = List.of("Level1EffectThreshold", "Level2EffectThreshold", "Level3EffectThreshold");
        for (String key : thresholds) {
            Object thresholdValue = rebuild.get(key);
            if (!(thresholdValue instanceof Integer)) {
                Number thresholdDefault = effectDefault.get(key);
                rebuild.put(key, thresholdDefault);
                LOGGER.warn("Entry \"Effect.{}\" has invalid type ({}): value is not an integer. Restored to default ({}).",
                        key,
                        thresholdValue,
                        thresholdDefault);
            }
        }

        boolean continueValidation = true;
        while (continueValidation) {
            continueValidation = false;
            int previousValue = Integer.MIN_VALUE;
            String previousKey = null;
            for (String key : thresholds) {
                int value = rebuild.get(key).intValue();
                if (value <= previousValue) {
                    int currentDefault = effectDefault.get(key).intValue();
                    if (currentDefault > previousValue) {
                        rebuild.put(key, currentDefault);
                        LOGGER.warn("Entry \"Effect.{}\" has invalid value ({}): it falls below the previous threshold ({}). Restored to default ({}).",
                                key,
                                value,
                                previousValue,
                                currentDefault);
                    } else {
                        rebuild.put(previousKey, effectDefault.get(previousKey));
                        LOGGER.warn("Entry \"Effect.{}\" has invalid value ({}): it exceeds the next threshold ({}). Restored to default ({}).",
                                previousKey,
                                previousValue,
                                value,
                                effectDefault.get(previousKey));
                    }
                    continueValidation = true;
                    break;
                }
                previousValue = value;
                previousKey = key;
            }
        }

        // Rewrite section entries

        effectSection.clear();
        for (Map.Entry<String, Number> entry : rebuild.entrySet()) {
            effectSection.set(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> commentEntry : effectComments.entrySet()) {
            effectSection.setComment(commentEntry.getKey(), commentEntry.getValue());
        }
        config.set("Effect", effectSection);
    }

    public static void validateWeightSection(CommentedFileConfig config, Map<String, List<Map<String, ?>>> sectionsMapping) {
        CommentedConfig weightSection = config.get("Weight");
        List<Map<String, ?>> weightMapping = sectionsMapping.get("Weight");
        @SuppressWarnings("unchecked")
        Map<String, Number> weightDefault = (Map<String, Number>) weightMapping.get(0);
        @SuppressWarnings("unchecked")
        Map<String, String> weightComments = (Map<String, String>) weightMapping.get(1);
        Map<String, Number> rebuild = ensureDefaultPresent(weightSection, "Weight", weightDefault);

        // Validate default and custom entries

        for (Map.Entry<String, Number> entry : rebuild.entrySet()) {
            Object weightValue = entry.getValue();
            if (!(weightValue instanceof Number)) {
                String weightKey = entry.getKey();
                Number defaultValue = weightDefault.get(weightKey);
                rebuild.put(weightKey, defaultValue);
                LOGGER.warn("Default entry \"Weight.{}\" has invalid type ({}): value is not a number. Restored to default ({}).",
                        weightKey,
                        weightValue,
                        defaultValue);
            }
        }

        for (Map.Entry<String, Object> entry : weightSection.valueMap().entrySet()) {
            String weightKey = entry.getKey();
            if (!rebuild.containsKey(weightKey)) {
                Object weightValue = entry.getValue();
                if (!(weightValue instanceof Number)) {
                    rebuild.put(weightKey, 0f);
                    LOGGER.warn("Custom entry \"Weight.{}\" has invalid type ({}): value is not a number. Restored to (0.0).",
                            weightKey,
                            weightValue);
                } else {
                    rebuild.put(weightKey, (Number) weightValue);
                }
            }
        }

        // Rewrite section entries

        weightSection.clear();
        for (Map.Entry<String, Number> entry : rebuild.entrySet()) {
            weightSection.set(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> commentEntry : weightComments.entrySet()) {
            weightSection.setComment(commentEntry.getKey(), commentEntry.getValue());
        }
        config.set("Weight", weightSection);
    }
}
