package com.caij.weiyo.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.Type;

/**
 * Created by Caij on 2015/8/24.
 */
public class GsonUtils {

    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .create();
    }

    public static Gson getGson() {
        return gson;
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }
}
