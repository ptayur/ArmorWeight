package net.ptayur.armorweight.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.ptayur.armorweight.config.ModCommonConfig;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.ptayur.armorweight.ArmorWeight.LOGGER;

public class ConfigUtils {
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

    public static void setSectionData(CommentedFileConfig config, String sectionName, List<Map<String, ?>> sectionData) {
        setConfigComments(config, sectionName, sectionData.get(1));
        setConfigEntries(config, sectionName, sectionData.get(0));
    }

    public static void createCommentedConfig(CommentedFileConfig config, Map<String, List<Map<String, ?>>> sectionsMapping) {
        for (Map.Entry<String, List<Map<String, ?>>> section : sectionsMapping.entrySet()) {
            setSectionData(config, section.getKey(), section.getValue());
        }
    }

    /**
     * @param config The config file to be validated
     * @param sectionName The name of the section
     * @param defaultValues The default values of the section
     * @param validateOnlyDefaultValues If true, only entries that appear in defaultValues map
     *                                  will be validated
     * @param parser A function that verifies types
     * @param validator A predicate that tests a valid T
     * @param onInvalid Callback for when parser fails or validator rejects
     * @param <T> Type parameter
     */
    public static <T> void validateConfigSection(CommentedFileConfig config,
                                          String sectionName,
                                          Map<String, T> defaultValues,
                                          boolean validateOnlyDefaultValues,
                                          Function<Object, Optional<T>> parser,
                                          Predicate<T> validator,
                                          Function<String, T> onInvalid
    ) {
        CommentedConfig section = config.get(sectionName);
        Map<String, T> rebuild = new LinkedHashMap<>();

        // Iterate over default values to preserve their presence in the config

        if (section == null) {
            List<Map<String, ?>> sectionData = ModCommonConfig.getSectionsMapping(sectionName);
            setSectionData(config, sectionName, sectionData);
            return;
        }
        for (Map.Entry<String, T> defaultEntry : defaultValues.entrySet()) {
            String key = defaultEntry.getKey();
            T value = defaultEntry.getValue();
            if (section.contains(key)) {
                value = section.get(key);
            } else {
                LOGGER.warn("Entry \"{}.{}\" is missing. Restored to default.", sectionName, key);
            }
            rebuild.put(key, value);
        }

        // Iterate over existing config file (or keys in defaultValues) and find invalid values

        Map<String, Object> sectionMap = section.valueMap();
        Set<String> keysToValidate = validateOnlyDefaultValues ? defaultValues.keySet() : sectionMap.keySet();
        for (String key : keysToValidate) {
            Object raw = sectionMap.get(key);
            T value = parser.apply(raw).filter(validator).orElseGet(()-> {
                LOGGER.warn("Entry \"{}.{}\" is invalid. Restored from default values.", sectionName, key);
                return onInvalid.apply(key);
            });
            rebuild.put(key, value);
        }

        for (Map.Entry<String, T> validatedEntry : rebuild.entrySet()) {
            section.set(validatedEntry.getKey(), validatedEntry.getValue());
        }
        config.set(sectionName, section);
    }

    public static void validateThresholdsOrder(CommentedFileConfig config) {
        List<String> keysToValidate = List.of("Level1EffectThreshold", "Level2EffectThreshold", "Level3EffectThreshold");
        CommentedConfig effectSection = config.get("Effect");
        List<Number> entries = new ArrayList<>();
        for (String key : keysToValidate) {
            entries.add(effectSection.get(key));
        }
        List<Number> sortedEntries = entries.stream().sorted(Comparator.comparingInt(Number::intValue)).toList();
        if (!entries.equals(sortedEntries)) {
            for (int i = 0; i < sortedEntries.size(); i++) {
                effectSection.set(keysToValidate.get(i), sortedEntries.get(i));
            }
            config.set("Effect", effectSection);
            LOGGER.warn("Effect thresholds were not in strictly increasing order ({}); " +
                    "they have been reordered to ascending values ({}).", entries, sortedEntries);
        }
    }
}
