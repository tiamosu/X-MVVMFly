package com.tiamosu.fly.integration.gson.data;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/GsonFactory
 * time   : 2021/09/30
 * desc   : JSONObject 类型解析适配器
 */
public class JSONArrayTypeAdapter extends TypeAdapter<JSONArray> {
    private final TypeAdapter<JsonElement> mProxy = TypeAdapters.JSON_ELEMENT;

    @Override
    public JSONArray read(JsonReader in) throws IOException {
        final JsonElement read = mProxy.read(in);
        if (read.isJsonArray()) {
            try {
                return new JSONArray(read.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONArray();
    }

    @Override
    public void write(JsonWriter out, JSONArray value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        mProxy.write(out, mProxy.fromJson(value.toString()));
    }
}