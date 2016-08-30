package com.caij.emore.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;

/**
 * Created by Caij on 2015/8/24.
 */
public class GsonUtils {

    private static final Gson gson;

    static {
        TypeAdapterFactory dataTypeAdapterFactory = new TypeAdapterFactory() {
            @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
            @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
                return typeToken.getRawType() == Date.class ? (TypeAdapter<T>) new DateTypeAdapter() : null;
            }
        };
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(dataTypeAdapterFactory)
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

    private static class DateTypeAdapter extends TypeAdapter<Date> {

        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            String dateFormatAsString = DateUtil.createTimeSimpleDateFormat.format(value);
            out.value(dateFormatAsString);
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return deserializeToDate(in.nextString());
        }

        private synchronized Date deserializeToDate(String json) {
            try {
                return  DateUtil.createTimeSimpleDateFormat.parse(json);
            } catch (ParseException ignored) {
            }

            try {
                return ISO8601Utils.parse(json, new ParsePosition(0));
            } catch (ParseException e) {
                throw new JsonSyntaxException(json, e);
            }
        }
    }

}
