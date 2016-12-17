package com.jomofisher.cmakeserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jomofisher.cmakeserver.model.BaseMessage;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class JsonUtils {

  static <T extends BaseMessage> void checkForExtraFields(String message, Class<T> clazz) {
    checkJsonElementAgainstJava(new JsonParser().parse(message), clazz);
  }

  private static void checkJsonArrayAgainstJava(JsonArray array, Class clazz) {
    Class elementType = clazz.getComponentType();
    if (elementType == null) {
      throw new RuntimeException(String.format("Element type for %s was null", clazz));
    }
    for (JsonElement arrayElement : array) {
      checkJsonElementAgainstJava(arrayElement, elementType);
    }
  }

  private static void checkJsonObjectAgainstJava(JsonObject jsonObject, Class clazz) {
    Map<String, Field> fieldNames = getFieldNames(clazz);
    for (Entry<String, JsonElement> element : jsonObject.entrySet()) {
      Field field = fieldNames.get(element.getKey());
      if (fieldNames.get(element.getKey()) == null) {
        throw new RuntimeException(String.format("Did not find field %s in class %s for JSon: %s",
          element.getKey(), clazz.getSimpleName(), jsonObject));
      }
      checkJsonElementAgainstJava(element.getValue(), field.getType());
    }
  }

  private static void checkJsonElementAgainstJava(JsonElement element, Class clazz) {
    if (element.isJsonArray()) {
      checkJsonArrayAgainstJava(element.getAsJsonArray(), clazz);
    } else if (element.isJsonObject()) {
      checkJsonObjectAgainstJava(element.getAsJsonObject(), clazz);
    }
  }

  private static Map<String, Field> getFieldNames(Class clazz) {
    Map<String, Field> fields = new HashMap<>();
    for (Field field : clazz.getFields()) {
      fields.put(field.getName(), field);
    }
    Class parent = clazz.getSuperclass();
    if (parent != null) {
      fields.putAll(getFieldNames(parent));
    }
    return fields;
  }
}
