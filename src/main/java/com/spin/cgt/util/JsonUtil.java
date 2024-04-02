package com.spin.cgt.util;

import com.google.gson.Gson;

public class JsonUtil {
    private final static Gson gson = new Gson();

    public static String toJson(Object data) {
        return gson.toJson(data);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
