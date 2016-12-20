/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jomofisher.cmakeserver;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.collect.MapMaker;
import com.google.gson.*;
import com.google.protobuf.DescriptorProtos.EnumValueOptions;
import com.google.protobuf.DescriptorProtos.FieldOptions;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Extension;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * GSON type adapter for protocol buffers that knows how to serialize enums either by using their
 * values or their names, and also supports custom proto field names.
 * <p>
 * You can specify which case representation is used for the proto fields when writing/reading the
 * JSON payload by calling {@link Builder#setFieldNameSerializationFormat()}.
 * <p>
 * An example of default serialization/deserialization using custom proto field names is shown
 * below:
 * <p>
 * <pre>
 * message MyMessage {
 *   // Will be serialized as 'osBuildID' instead of the default 'osBuildId'.
 *   string os_build_id = 1 [(serialized_name) = "osBuildID"];
 * }
 * </pre>
 * <p>
 *
 * @author Inderjeet Singh
 * @author Emmanuel Cron
 * @author Stanley Wang
 */
class ProtoTypeAdapter
        implements JsonSerializer<GeneratedMessageV3>, JsonDeserializer<GeneratedMessageV3> {
    /**
     * Determines how enum <u>values</u> should be serialized.
     */
    public enum EnumSerialization {
        /**
         * Serializes and deserializes enum values using their <b>number</b>. When this is used, custom
         * value names set on enums are ignored.
         */
        NUMBER,
        /**
         * Serializes and deserializes enum values using their <b>name</b>.
         */
        NAME
    }

    /**
     * Builder for {@link ProtoTypeAdapter}s.
     */
    public static class Builder {
        private final Set<Extension<FieldOptions, String>> serializedNameExtensions;
        private final Set<Extension<EnumValueOptions, String>> serializedEnumValueExtensions;
        private EnumSerialization enumSerialization;
        private Converter<String, String> fieldNameSerializationFormat;

        private Builder() {
            this.serializedNameExtensions = new HashSet<>();
            this.serializedEnumValueExtensions = new HashSet<>();
            setEnumSerialization(EnumSerialization.NAME);
            setFieldNameSerializationFormat();
        }

        public Builder setEnumSerialization(EnumSerialization enumSerialization) {
            this.enumSerialization = checkNotNull(enumSerialization);
            return this;
        }

        /**
         * Sets the field names serialization format. The first parameter defines how to read the format
         * of the proto field names you are converting to JSON. The second parameter defines which
         * format to use when serializing them.
         * <p>
         * For example, if you use the following parameters: {@link CaseFormat#LOWER_UNDERSCORE},
         * {@link CaseFormat#LOWER_CAMEL}, the following conversion will occur:
         * <p>
         * <pre>
         * PROTO     <->  JSON
         * my_field       myField
         * foo            foo
         * n__id_ct       nIdCt
         * </pre>
         */
        public void setFieldNameSerializationFormat() {
            fieldNameSerializationFormat = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);
        }

        public ProtoTypeAdapter build() {
            return new ProtoTypeAdapter(enumSerialization, fieldNameSerializationFormat,
                    serializedNameExtensions, serializedEnumValueExtensions);
        }
    }

    /**
     * Creates a new {@link ProtoTypeAdapter} builder, defaulting enum serialization to
     * {@link EnumSerialization#NAME} and converting field serialization from
     * {@link CaseFormat#LOWER_UNDERSCORE} to {@link CaseFormat#LOWER_CAMEL}.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    private static final com.google.protobuf.Descriptors.FieldDescriptor.Type ENUM_TYPE =
            com.google.protobuf.Descriptors.FieldDescriptor.Type.ENUM;

    private static final ConcurrentMap<String, Map<Class<?>, Method>> mapOfMapOfMethods =
            new MapMaker().makeMap();

    private final EnumSerialization enumSerialization;
    private final Converter<String, String> fieldNameSerializationFormat;
    private final Set<Extension<FieldOptions, String>> serializedNameExtensions;
    private final Set<Extension<EnumValueOptions, String>> serializedEnumValueExtensions;

    private ProtoTypeAdapter(EnumSerialization enumSerialization,
                             Converter<String, String> fieldNameSerializationFormat,
                             Set<Extension<FieldOptions, String>> serializedNameExtensions,
                             Set<Extension<EnumValueOptions, String>> serializedEnumValueExtensions) {
        this.enumSerialization = enumSerialization;
        this.fieldNameSerializationFormat = fieldNameSerializationFormat;
        this.serializedNameExtensions = serializedNameExtensions;
        this.serializedEnumValueExtensions = serializedEnumValueExtensions;
    }

    @Override
    public JsonElement serialize(GeneratedMessageV3 src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        JsonObject ret = new JsonObject();
        final Map<FieldDescriptor, Object> fields = src.getAllFields();

        for (Map.Entry<FieldDescriptor, Object> fieldPair : fields.entrySet()) {
            final FieldDescriptor desc = fieldPair.getKey();
            String name = getCustSerializedName(desc.getOptions(), desc.getName());

            if (desc.getType() == ENUM_TYPE) {
                // Enum collections are also returned as ENUM_TYPE
                if (fieldPair.getValue() instanceof Collection) {
                    // Build the array to avoid infinite loop
                    JsonArray array = new JsonArray();
                    @SuppressWarnings("unchecked")
                    Collection<EnumValueDescriptor> enumDescs =
                            (Collection<EnumValueDescriptor>) fieldPair.getValue();
                    for (EnumValueDescriptor enumDesc : enumDescs) {
                        array.add(context.serialize(getEnumValue(enumDesc)));
                        ret.add(name, array);
                    }
                } else {
                    EnumValueDescriptor enumDesc = ((EnumValueDescriptor) fieldPair.getValue());
                    ret.add(name, context.serialize(getEnumValue(enumDesc)));
                }
            } else {
                ret.add(name, context.serialize(fieldPair.getValue()));
            }
        }
        return ret;
    }

    @Override
    public GeneratedMessageV3 deserialize(JsonElement json, Type typeOfT,
                                          JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObject = json.getAsJsonObject();
            @SuppressWarnings("unchecked")
            Class<? extends GeneratedMessageV3> protoClass = (Class<? extends GeneratedMessageV3>) typeOfT;

            try {
                // Invoke the ProtoClass.newBuilder() method
                GeneratedMessageV3.Builder<?> protoBuilder =
                        (GeneratedMessageV3.Builder<?>) getCachedMethod(protoClass, "newBuilder").invoke(null);

                Descriptor protoDescriptor =
                        (Descriptor) getCachedMethod(protoClass, "getDescriptor").invoke(null);
                // Call setters on all of the available fields
                for (FieldDescriptor fieldDescriptor : protoDescriptor.getFields()) {
                    String jsonFieldName =
                            getCustSerializedName(fieldDescriptor.getOptions(), fieldDescriptor.getName());

                    JsonElement jsonElement = jsonObject.get(jsonFieldName);
                    if (jsonElement != null && !jsonElement.isJsonNull()) {
                        // Do not reuse jsonFieldName here, it might have a custom value
                        Object fieldValue;
                        if (fieldDescriptor.getType() == ENUM_TYPE) {
                            if (jsonElement.isJsonArray()) {
                                // Handling array
                                Collection<EnumValueDescriptor> enumCollection =
                                        new ArrayList<>();
                                for (JsonElement element : jsonElement.getAsJsonArray()) {
                                    enumCollection.add(
                                            findValueByNameAndExtension(fieldDescriptor.getEnumType(), element));
                                }
                                fieldValue = enumCollection;
                            } else {
                                // No array, just a plain value
                                fieldValue =
                                        findValueByNameAndExtension(fieldDescriptor.getEnumType(), jsonElement);
                            }
                            protoBuilder.setField(fieldDescriptor, fieldValue);
                        } else if (fieldDescriptor.isRepeated()) {
                            // If the type is an array, then we have to grab the type from the class.
                            String protoArrayFieldName =
                                    fieldNameSerializationFormat.convert(fieldDescriptor.getName()) + "_";
                            Field protoArrayField = protoClass.getDeclaredField(protoArrayFieldName);
                            Type protoArrayFieldType = protoArrayField.getGenericType();
                            fieldValue = context.deserialize(jsonElement, protoArrayFieldType);
                            protoBuilder.setField(fieldDescriptor, fieldValue);
                        } else {
                            Message prototype = protoBuilder.build();
                            Object field = prototype.getField(fieldDescriptor);
                            fieldValue = context.deserialize(jsonElement, field.getClass());
                            protoBuilder.setField(fieldDescriptor, fieldValue);
                        }
                    }
                }
                return (GeneratedMessageV3) protoBuilder.build();
            } catch (SecurityException | InvocationTargetException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException e) {
                throw new JsonParseException(e);
            }
        } catch (Exception e) {
            throw new JsonParseException("Error while parsing proto", e);
        }
    }

    /**
     * Retrieves the custom field name from the given options, and if not found, returns the specified
     * default name.
     */
    private String getCustSerializedName(FieldOptions options, String defaultName) {
        for (Extension<FieldOptions, String> extension : serializedNameExtensions) {
            if (options.hasExtension(extension)) {
                return options.getExtension(extension);
            }
        }
        return fieldNameSerializationFormat.convert(defaultName);
    }

    /**
     * Retrieves the custom enum value name from the given options, and if not found, returns the
     * specified default value.
     */
    private String getCustSerializedEnumValue(EnumValueOptions options, String defaultValue) {
        for (Extension<EnumValueOptions, String> extension : serializedEnumValueExtensions) {
            if (options.hasExtension(extension)) {
                return options.getExtension(extension);
            }
        }
        return defaultValue;
    }

    /**
     * Returns the enum value to use for serialization, depending on the value of
     * {@link EnumSerialization} that was given to this adapter.
     */
    private Object getEnumValue(EnumValueDescriptor enumDesc) {
        if (enumSerialization == EnumSerialization.NAME) {
            return getCustSerializedEnumValue(enumDesc.getOptions(), enumDesc.getName());
        } else {
            return enumDesc.getNumber();
        }
    }

    /**
     * Finds an enum value in the given {@link EnumDescriptor} that matches the given JSON element,
     * either by name if the current adapter is using {@link EnumSerialization#NAME}, otherwise by
     * number. If matching by name, it uses the extension value if it is defined, otherwise it uses
     * its default value.
     *
     * @throws IllegalArgumentException if a matching name/number was not found
     */
    private EnumValueDescriptor findValueByNameAndExtension(EnumDescriptor desc,
                                                            JsonElement jsonElement) {
        if (enumSerialization == EnumSerialization.NAME) {
            // With enum name
            for (EnumValueDescriptor enumDesc : desc.getValues()) {
                String enumValue = getCustSerializedEnumValue(enumDesc.getOptions(), enumDesc.getName());
                if (enumValue.equals(jsonElement.getAsString())) {
                    return enumDesc;
                }
            }
            throw new IllegalArgumentException(
                    String.format("Unrecognized enum name: %s", jsonElement.getAsString()));
        } else {
            // With enum value
            EnumValueDescriptor fieldValue = desc.findValueByNumber(jsonElement.getAsInt());
            if (fieldValue == null) {
                throw new IllegalArgumentException(
                        String.format("Unrecognized enum value: %s", jsonElement.getAsInt()));
            }
            return fieldValue;
        }
    }

    private static Method getCachedMethod(Class<?> clazz, String methodName,
                                          Class<?>... methodParamTypes) throws NoSuchMethodException {
        Map<Class<?>, Method> mapOfMethods = mapOfMapOfMethods.get(methodName);
        if (mapOfMethods == null) {
            mapOfMethods = new MapMaker().makeMap();
            Map<Class<?>, Method> previous =
                    mapOfMapOfMethods.putIfAbsent(methodName, mapOfMethods);
            mapOfMethods = previous == null ? mapOfMethods : previous;
        }

        Method method = mapOfMethods.get(clazz);
        if (method == null) {
            method = clazz.getMethod(methodName, methodParamTypes);
            mapOfMethods.put(clazz, method);
            // NB: it doesn't matter which method we return in the event of a race.
        }
        return method;
    }

}