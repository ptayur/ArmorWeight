package net.ptayur.armorweight.util;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

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
            config.setComment(path + "." + entry.getKey(), (String) entry.getValue());
        }
    }

    public static void createCommentedConfig(CommentedFileConfig config, Map<String, List<Map<String, ?>>> sectionsMapping) {
        for (Map.Entry<String, List<Map<String, ?>>> section : sectionsMapping.entrySet()) {
            setConfigEntries(config, section.getKey(), section.getValue().get(0));
            setConfigComments(config, section.getKey(), section.getValue().get(1));
        }
    }

    public static void checkMissingEntries(CommentedFileConfig config, Map<String, List<Map<String, ?>>> sectionsMapping) {
        Map<String, Object> configMap = new HashMap<>(config.valueMap());
        config.clear();
        for (Map.Entry<String, List<Map<String, ?>>> section : sectionsMapping.entrySet()) {
            Map<String, ?> defaultSectionEntries = section.getValue().get(0);
            Map<String, ?> sectionComments = section.getValue().get(1);
            if (defaultSectionEntries == null) {
                continue;
            }
            if (!configMap.containsKey(section.getKey())) {
                setConfigEntries(config, section.getKey(), defaultSectionEntries);
                setConfigComments(config, section.getKey(), sectionComments);
                continue;
            }
            CommentedConfig tempSection = InMemoryCommentedFormat.defaultInstance().createConfig(LinkedHashMap::new);
            CommentedConfig sectionEntries = (CommentedConfig)configMap.get(section.getKey());
            for (Map.Entry<String, ?> mapEntry : defaultSectionEntries.entrySet()) {
                if (!sectionEntries.contains(mapEntry.getKey())) {
                    tempSection.set(mapEntry.getKey(), mapEntry.getValue());
                    LOGGER.warn("Config entry \"{}\" in section [{}] is missing. The entry has been restored with the default value.",
                            mapEntry.getKey(), section.getKey());
                    continue;
                }
                tempSection.set(mapEntry.getKey(), sectionEntries.get(mapEntry.getKey()));
                sectionEntries.remove(mapEntry.getKey());
            }
            if (!sectionEntries.isEmpty()) {
                for (Map.Entry<String, ?> mapEntry : sectionEntries.valueMap().entrySet()) {
                    tempSection.set(mapEntry.getKey(), mapEntry.getValue());
                }
            }
            config.set(section.getKey(), tempSection);
            setConfigComments(config, section.getKey(), sectionComments);
        }
    }

    public static void checkSettingsValues(CommentedFileConfig config, Map<String, Boolean> defaultValues) {
        CommentedConfig settings = config.get("Settings");
        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, Object> mapEntry : settings.valueMap().entrySet()) {
            String entryKey = mapEntry.getKey();
            if (!defaultValues.containsKey(entryKey)) {
                keysToRemove.add(entryKey);
                LOGGER.warn("The entry \"{}\" in section [Settings] is redundant. Entry deleted.",
                        entryKey);
                continue;
            }
            if (!(mapEntry.getValue() instanceof Boolean)) {
                settings.set(entryKey, defaultValues.get(entryKey));
                LOGGER.warn("The entry \"{}\" in section [Settings] is incorrect. The value is changed to the default value.",
                        entryKey);
            }
        }
        for (String key : keysToRemove) {
            settings.remove(key);
        }
        config.set("Settings", settings);
    }

    public static void checkThresholdsValues(CommentedFileConfig config, Map<String, Integer> defaultValues) {
        CommentedConfig thresholds = config.get("Thresholds");
        List<String> keysToRemove = new ArrayList<>();
        List<Integer> thresholdValues = new ArrayList<>();
        for (Map.Entry<String, Object> mapEntry : thresholds.valueMap().entrySet()) {
            String entryKey = mapEntry.getKey();
            int defaultValue = defaultValues.get(entryKey);
            if (!(mapEntry.getValue() instanceof Integer)) {
                thresholds.set(entryKey, defaultValue);
                LOGGER.warn("The entry \"{}\" in section [Thresholds] is incorrect. The value is changed to the default value.",
                        entryKey);
            }
            Integer entryValue = (Integer) mapEntry.getValue();
            if (!defaultValues.containsKey(entryKey)) {
                keysToRemove.add(entryKey);
                LOGGER.warn("The entry \"{}\" in section [Thresholds] is redundant. Entry deleted.",
                        entryKey);
                continue;
            }
            if (entryValue < 0) {
                thresholds.set(entryKey, defaultValue);
                LOGGER.warn("The entry \"{}\" in section [Thresholds] is less than 0. The value is changed to the default value.",
                        entryKey);
            } else if (entryValue > 19) {
                thresholds.set(entryKey, defaultValue);
                LOGGER.warn("The entry \"{}\" in section [Thresholds] is greater than 19. The value is changed to the default value.",
                        entryKey);
            }
            thresholdValues.add(entryValue);
            if (thresholdValues.size() > 1) {
                int thresholdIndex = thresholdValues.indexOf(entryValue);
                int previousThreshold = thresholdValues.get(thresholdIndex - 1);
                if (entryValue < previousThreshold) {
                    thresholds.set(entryKey, defaultValue);
                    thresholdValues.set(thresholdIndex, defaultValue);
                    LOGGER.warn("The entry \"{}\" in section [Thresholds] is less than the previous threshold. The value is changed to the default value.",
                            entryKey);
                }
            }
        }
        for (String key : keysToRemove) {
            thresholds.remove(key);
        }
        config.set("Thresholds", thresholds);
    }

    public static void checkWeightValues(CommentedFileConfig config, Map<String, Number> defaultValues) {
        CommentedConfig weightMapping = config.get("Weight");
        List<String> keysToRemove = new ArrayList<>();
        for (Map.Entry<String, Object> entry : weightMapping.valueMap().entrySet()) {
            String entryKey = entry.getKey();
            ResourceLocation registryKey = new ResourceLocation(entryKey);
            Item item = ForgeRegistries.ITEMS.getValue(registryKey);
            if (!ForgeRegistries.ITEMS.containsKey(registryKey) || item == null) {
                keysToRemove.add(entryKey);
                LOGGER.warn("The entry \"{}\" in section [Weight] doesn't exist in the registry. Entry deleted",
                        entryKey);
                continue;
            }
            if (!EquipmentUtils.isWearable(new ItemStack(item))) {
                keysToRemove.add(entryKey);
                LOGGER.warn("The entry \"{}\" in section [Weight] can't be worn. Entry deleted",
                        entryKey);
                continue;
            }
            if(!(entry.getValue() instanceof Number)) {
                weightMapping.set(entryKey, defaultValues.getOrDefault(entryKey, 0f));
                LOGGER.warn("The entry \"{}\" in section [Weight] is incorrect. Value changed to \"0.0\"",
                        entryKey);
                continue;
            }
            float weight = ((Number) entry.getValue()).floatValue();
            if (weight < 0) {
                weightMapping.set(entryKey, 0f);
                LOGGER.warn("The entry \"{}\" in section [Weight] is less than 0.0. Value changed to \"0.0\"",
                        entryKey);
            } else if (weight > 20) {
                weightMapping.set(entryKey, 20f);
                LOGGER.warn("The entry \"{}\" in section [Weight] is greater than 20.0. Value changed to \"20.0\"",
                        entryKey);
            }
        }
        for (String key : keysToRemove) {
            weightMapping.remove(key);
        }
        config.set("Weight", weightMapping);
    }

    public static void loadSettingsSection(CommentedFileConfig config, Map<String, Boolean> settingsMap) {
        CommentedConfig settingsSection = config.get("Settings");
        for (Map.Entry<String, Object> entry : settingsSection.valueMap().entrySet()) {
            settingsMap.put(entry.getKey(), (Boolean) entry.getValue());
        }
    }

    public static void loadThresholdsSection(CommentedFileConfig config, List<Integer> thresholds) {
        CommentedConfig thresholdsSection = config.get("Thresholds");
        for (Map.Entry<String, Object> entry : thresholdsSection.valueMap().entrySet()) {
            thresholds.add((Integer) entry.getValue());
        }
        thresholds.add(20);
    }

    public static void loadWeightSection(CommentedFileConfig config, Map<String, Float> weightMapping) {
        if ((Boolean) config.get("Settings.isDependsOnProtection")) {
            for (Item item: ForgeRegistries.ITEMS.getValues()) {
                if (item instanceof ArmorItem) {
                    weightMapping.put(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString(), (float) ((ArmorItem) item).getDefense());
                }
            }
        }
        CommentedConfig weightSection = config.get("Weight");
        for (Map.Entry<String, Object> entry : weightSection.valueMap().entrySet()) {
            weightMapping.putIfAbsent(entry.getKey(), ((Number) entry.getValue()).floatValue());
        }
    }
}
