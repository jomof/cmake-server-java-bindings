/*
 * Copyright 2016 Jomo Fisher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jomofisher.cmakeserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jomofisher.cmakeserver.model.BaseMessage;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class JsonUtils {

    static <T extends BaseMessage> void checkForExtraFields(@NotNull String message, @NotNull Class<T> clazz) {
        checkJsonElementAgainstJava(new JsonParser().parse(message), clazz);
    }

    private static void checkJsonArrayAgainstJava(@NotNull JsonArray array, @NotNull Class clazz) {
        Class elementType = clazz.getComponentType();
        if (elementType == null) {
            throw new RuntimeException(String.format("Element type for %s was null", clazz));
        }
        for (JsonElement arrayElement : array) {
            checkJsonElementAgainstJava(arrayElement, elementType);
        }
    }

    private static void checkJsonObjectAgainstJava(@NotNull JsonObject jsonObject, @NotNull Class clazz) {
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

    private static void checkJsonElementAgainstJava(@NotNull JsonElement element, @NotNull Class clazz) {
        if (element.isJsonArray()) {
            checkJsonArrayAgainstJava(element.getAsJsonArray(), clazz);
        } else if (element.isJsonObject()) {
            checkJsonObjectAgainstJava(element.getAsJsonObject(), clazz);
        }
    }

    @NotNull
    private static Map<String, Field> getFieldNames(@NotNull Class clazz) {
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
