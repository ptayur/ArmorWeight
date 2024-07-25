package net.ptayur.armorweight.client;

import java.util.List;
import java.util.Map;

public class ClientData {
    private static float playerWeight;
    private static Map<String, Float> weightMapping;
    private static List<Integer> thresholds;

    public static void setPlayerWeight(float weight) {
        ClientData.playerWeight = weight;
    }

    public static float getPlayerWeight() {
        return playerWeight;
    }

    public static void setWeightMapping(Map<String, Float> weightMapping) {
        ClientData.weightMapping = weightMapping;
    }

    public static Map<String, Float> getWeightMapping() {
        return weightMapping;
    }

    public static void setThresholds(List<Integer> thresholds) {
        ClientData.thresholds = thresholds;
    }

    public static List<Integer> getThresholds() {
        return thresholds;
    }
}
