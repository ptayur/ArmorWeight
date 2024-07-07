package net.ptayur.armorweight.client;

import java.util.Map;

public class ClientWeightData {
    private static int playerWeight;
    private static Map<String, Integer> weightMap;

    public static void setPlayerWeight(int weight) {
        ClientWeightData.playerWeight = weight;
    }

    public static int getPlayerWeight() {
        return playerWeight;
    }

    public static void setWeightMap(Map<String, Integer> weightMap) {
        ClientWeightData.weightMap = weightMap;
    }

    public static Map<String, Integer> getWeightMap() {
        return weightMap;
    }
}
