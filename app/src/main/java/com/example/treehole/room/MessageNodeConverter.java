package com.example.treehole.room;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MessageNodeConverter {
    @TypeConverter
    public static List<MessageNode> fromString(String value) {
        Type listType = new TypeToken<List<MessageNode>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<MessageNode> list) {
        return new Gson().toJson(list);
    }
}
