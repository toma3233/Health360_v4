package com.example.health360_tools;

import java.util.HashMap;
import java.util.Map;

public abstract class Nutrition {
    // Unit conversion
    protected static Map<String, Integer> unitConversions = new HashMap<String, Integer>() {{
        put("g", 1);
        put("kcal", 1);
        put("mg", 1000);
        put("mcg", 1000000);
    }};

    protected double calories;
    protected double fat;
    protected double carbohydrates;
    protected double fiber;
    protected double protein;
    protected double sodium;

    protected String servingSize;

    @Override
    public String toString() {
        return "Nutrition{" +
                "calories=" + calories +
                ", fat=" + fat +
                ", carbohydrates=" + carbohydrates +
                ", fiber=" + fiber +
                ", protein=" + protein +
                ", sodium=" + sodium +
                ", servingSize='" + servingSize + '\'' +
                '}';
    }

    public double getSodium() {
        return sodium;
    }

    public double getProtein() {
        return protein;
    }

    public double getFiber() {
        return fiber;
    }

    public double getFat() {
        return fat;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public double getCalories() {
        return calories;
    }

    public String getServingSize() {
        return servingSize;
    }
}
