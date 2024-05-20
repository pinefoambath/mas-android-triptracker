package com.massoftwareengineering.triptracker.utils;

public class JsonUtils {
    public static String createJsonRequestBody(String notes) {
        return "{\"notes\":\"" + notes + "\"}";
    }
}
