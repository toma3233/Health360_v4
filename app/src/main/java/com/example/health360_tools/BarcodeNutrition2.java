package com.example.health360_tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BarcodeNutrition2 extends Nutrition {
    public BarcodeNutrition2(String barcode) throws IOException {
        // Get JSONObject from api
        String page = getPage(getURL(barcode));

        servingSize = getJSONField(page, "serving_size");

        calories = Double.parseDouble(getJSONField(page, "energy-kcal_serving"));

        fat = getStandardField(page, "fat");
        carbohydrates = getStandardField(page, "carbohydrates");
        fiber = getStandardField(page, "fiber");
        protein = getStandardField(page, "proteins");
        sodium = getStandardField(page, "sodium");


    }

    private String getJSONField(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\":\\s*\"?(?<value>.+?)\"?[,}]");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return matcher.group("value");
        }

        return null;
    }

    private double getStandardField(String json, String field) {
        String fieldValue = getJSONField(json, field + "_serving");
        double value = Double.parseDouble(fieldValue);

        value /= unitConversions.get(getJSONField(json, field + "_unit"));

        return value;
    }

    private static String getURL(String barcode) {
        return "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
    }

    private static String getPage(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();

        while (reader.ready()) {
            builder.append(reader.readLine());
        }

        connection.disconnect();

        return builder.toString();
    }
}
