// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
// https://developers.google.com/protocol-buffers/
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.jomofisher.cmakeserver;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.*;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility classes to convert protobuf messages to/from JSON format. The JSON
 * format follows Proto3 JSON specification and only proto3 features are
 * supported. Proto2 only features (e.g., extensions and unknown fields) will
 * be discarded in the conversion. That is, when converting proto2 messages
 * to JSON format, extensions and unknown fields will be treated as if they
 * do not exist. This applies to proto2 messages embedded in proto3 messages
 * as well.
 */
class JsonFormat {

    private JsonFormat() {
    }

    /**
     * Creates a {@link Printer} with default configurations.
     */
    public static Printer printer() {
        return new Printer(TypeRegistry.getEmptyTypeRegistry());
    }

    /**
     * A Printer converts protobuf message to JSON format.
     */
    public static class Printer {
        private final TypeRegistry registry;

        private Printer(
                TypeRegistry registry) {
            this.registry = registry;
        }

        /**
         * Converts a protobuf message to JSON format.
         *
         * @throws InvalidProtocolBufferException if the message contains Any types
         *                                        that can't be resolved.
         * @throws IOException                    if writing to the output fails.
         */
        public void appendTo(MessageOrBuilder message, Appendable output) throws IOException {
            // TODO(xiaofeng): Investigate the allocation overhead and optimize for
            // mobile.
            new PrinterImpl(
                    registry,
                    output)
                    .print(message);
        }

        /**
         * Converts a protobuf message to JSON format. Throws exceptions if there
         * are unknown Any types in the message.
         */
        public String print(MessageOrBuilder message) throws InvalidProtocolBufferException {
            try {
                StringBuilder builder = new StringBuilder();
                appendTo(message, builder);
                return builder.toString();
            } catch (InvalidProtocolBufferException e) {
                throw e;
            } catch (IOException e) {
                // Unexpected IOException.
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * A TypeRegistry is used to resolve Any messages in the JSON conversion.
     * You must provide a TypeRegistry containing all message types used in
     * Any message fields, or the JSON conversion will fail because data
     * in Any message fields is unrecognizable. You don't need to supply a
     * TypeRegistry if you don't use Any message fields.
     */
    public static class TypeRegistry {
        private static class EmptyTypeRegistryHolder {
            private static final TypeRegistry EMPTY =
                    new TypeRegistry(Collections.emptyMap());
        }

        public static TypeRegistry getEmptyTypeRegistry() {
            return EmptyTypeRegistryHolder.EMPTY;
        }

        /**
         * Find a type by its full name. Returns null if it cannot be found in
         * this {@link TypeRegistry}.
         */
        public Descriptor find(String name) {
            return types.get(name);
        }

        private final Map<String, Descriptor> types;

        private TypeRegistry(Map<String, Descriptor> types) {
            this.types = types;
        }

    }

    /**
     * An interface for json formatting that can be used in
     * combination with the omittingInsignificantWhitespace() method
     */
    interface TextGenerator {
        void indent();

        void outdent();

        void print(final CharSequence text) throws IOException;
    }

    /**
     * A TextGenerator adds indentation when writing formatted text.
     */
    private static final class PrettyTextGenerator implements TextGenerator {
        private final Appendable output;
        private final StringBuilder indent = new StringBuilder();
        private boolean atStartOfLine = true;

        private PrettyTextGenerator(final Appendable output) {
            this.output = output;
        }

        /**
         * Indent text by two spaces.  After calling Indent(), two spaces will be
         * inserted at the beginning of each line of text.  Indent() may be called
         * multiple times to produce deeper indents.
         */
        public void indent() {
            indent.append("  ");
        }

        /**
         * Reduces the current indent level by two spaces, or crashes if the indent
         * level is zero.
         */
        public void outdent() {
            final int length = indent.length();
            if (length < 2) {
                throw new IllegalArgumentException(" Outdent() without matching Indent().");
            }
            indent.delete(length - 2, length);
        }

        /**
         * Print text to the output stream.
         */
        public void print(final CharSequence text) throws IOException {
            final int size = text.length();
            int pos = 0;

            for (int i = 0; i < size; i++) {
                if (text.charAt(i) == '\n') {
                    write(text.subSequence(pos, i + 1));
                    pos = i + 1;
                    atStartOfLine = true;
                }
            }
            write(text.subSequence(pos, size));
        }

        private void write(final CharSequence data) throws IOException {
            if (data.length() == 0) {
                return;
            }
            if (atStartOfLine) {
                atStartOfLine = false;
                output.append(indent);
            }
            output.append(data);
        }
    }

    /**
     * A Printer converts protobuf messages to JSON format.
     */
    private static final class PrinterImpl {
        private final TypeRegistry registry;
        private final TextGenerator generator;
        // We use Gson to help handle string escapes.
        private final Gson gson;
        private final CharSequence blankOrSpace;
        private final CharSequence blankOrNewLine;

        private static class GsonHolder {
            private static final Gson DEFAULT_GSON = new GsonBuilder().disableHtmlEscaping().create();
        }

        PrinterImpl(
                TypeRegistry registry,
                Appendable jsonOutput) {
            this.registry = registry;
            this.gson = GsonHolder.DEFAULT_GSON;
            // json format related properties, determined by printerType
            this.generator = new PrettyTextGenerator(jsonOutput);
            this.blankOrSpace = " ";
            this.blankOrNewLine = "\n";
        }

        void print(MessageOrBuilder message) throws IOException {
            WellKnownTypePrinter specialPrinter =
                    wellKnownTypePrinters.get(message.getDescriptorForType().getFullName());
            if (specialPrinter != null) {
                specialPrinter.print(this, message);
                return;
            }
            print(message, null);
        }

        private interface WellKnownTypePrinter {
            void print(PrinterImpl printer, MessageOrBuilder message) throws IOException;
        }

        private static final Map<String, WellKnownTypePrinter> wellKnownTypePrinters =
                buildWellKnownTypePrinters();

         private static Map<String, WellKnownTypePrinter> buildWellKnownTypePrinters() {
      Map<String, WellKnownTypePrinter> printers = new HashMap<String, WellKnownTypePrinter>();
      // Special-case Any.
      printers.put(
          Any.getDescriptor().getFullName(),
          new WellKnownTypePrinter() {
            @Override
            public void print(PrinterImpl printer, MessageOrBuilder message) throws IOException {
              printer.printAny(message);
            }
          });
      // Special-case wrapper types.
      WellKnownTypePrinter wrappersPrinter =
          new WellKnownTypePrinter() {
            @Override
            public void print(PrinterImpl printer, MessageOrBuilder message) throws IOException {
              printer.printWrapper(message);
            }
          };
      printers.put(BoolValue.getDescriptor().getFullName(), wrappersPrinter);
      printers.put(Int32Value.getDescriptor().getFullName(), wrappersPrinter);
      printers.put(UInt32Value.getDescriptor().getFullName(), wrappersPrinter);
      printers.put(Int64Value.getDescriptor().getFullName(), wrappersPrinter);
      printers.put(UInt64Value.getDescriptor().getFullName(), wrappersPrinter);
      printers.put(StringValue.getDescriptor().getFullName(), wrappersPrinter);
      printers.put(BytesValue.getDescriptor().getFullName(), wrappersPrinter);
      printers.put(FloatValue.getDescriptor().getFullName(), wrappersPrinter);
      printers.put(DoubleValue.getDescriptor().getFullName(), wrappersPrinter);
      // Special-case Struct.
      printers.put(
          Struct.getDescriptor().getFullName(),
          new WellKnownTypePrinter() {
            @Override
            public void print(PrinterImpl printer, MessageOrBuilder message) throws IOException {
              printer.printStruct(message);
            }
          });
      // Special-case Value.
      printers.put(
          Value.getDescriptor().getFullName(),
          new WellKnownTypePrinter() {
            @Override
            public void print(PrinterImpl printer, MessageOrBuilder message) throws IOException {
              printer.printValue(message);
            }
          });
      // Special-case ListValue.
      printers.put(
          ListValue.getDescriptor().getFullName(),
          new WellKnownTypePrinter() {
            @Override
            public void print(PrinterImpl printer, MessageOrBuilder message) throws IOException {
              printer.printListValue(message);
            }
          });
      return printers;
    }

        /**
         * Prints google.protobuf.Any
         */
        private void printAny(MessageOrBuilder message) throws IOException {
            if (Any.getDefaultInstance().equals(message)) {
                generator.print("{}");
                return;
            }
            Descriptor descriptor = message.getDescriptorForType();
            FieldDescriptor typeUrlField = descriptor.findFieldByName("type_url");
            FieldDescriptor valueField = descriptor.findFieldByName("value");
            // Validates type of the message. Note that we can't just cast the message
            // to com.google.protobuf.Any because it might be a DynamicMessage.
            if (typeUrlField == null
                    || valueField == null
                    || typeUrlField.getType() != FieldDescriptor.Type.STRING
                    || valueField.getType() != FieldDescriptor.Type.BYTES) {
                throw new InvalidProtocolBufferException("Invalid Any type.");
            }
            String typeUrl = (String) message.getField(typeUrlField);
            String typeName = getTypeName(typeUrl);
            Descriptor type = registry.find(typeName);
            if (type == null) {
                throw new InvalidProtocolBufferException("Cannot find type for url: " + typeUrl);
            }
            ByteString content = (ByteString) message.getField(valueField);
            Message contentMessage =
                    DynamicMessage.getDefaultInstance(type).getParserForType().parseFrom(content);
            WellKnownTypePrinter printer = wellKnownTypePrinters.get(typeName);
            if (printer != null) {
                // If the type is one of the well-known types, we use a special
                // formatting.
                generator.print("{" + blankOrNewLine);
                generator.indent();
                generator.print("\"@type\":" + blankOrSpace + gson.toJson(typeUrl) + "," + blankOrNewLine);
                generator.print("\"value\":" + blankOrSpace);
                printer.print(this, contentMessage);
                generator.print(blankOrNewLine);
                generator.outdent();
                generator.print("}");
            } else {
                // Print the content message instead (with a "@type" field added).
                print(contentMessage, typeUrl);
            }
        }

        /**
         * Prints wrapper types (e.g., google.protobuf.Int32Value)
         */
        private void printWrapper(MessageOrBuilder message) throws IOException {
            Descriptor descriptor = message.getDescriptorForType();
            FieldDescriptor valueField = descriptor.findFieldByName("value");
            if (valueField == null) {
                throw new InvalidProtocolBufferException("Invalid Wrapper type.");
            }
            // When formatting wrapper types, we just print its value field instead of
            // the whole message.
            printSingleFieldValue(valueField, message.getField(valueField));
        }

        /**
         * Prints google.protobuf.Struct
         */
        private void printStruct(MessageOrBuilder message) throws IOException {
            Descriptor descriptor = message.getDescriptorForType();
            FieldDescriptor field = descriptor.findFieldByName("fields");
            if (field == null) {
                throw new InvalidProtocolBufferException("Invalid Struct type.");
            }
            // Struct is formatted as a map object.
            printMapFieldValue(field, message.getField(field));
        }

        /**
         * Prints google.protobuf.Value
         */
        private void printValue(MessageOrBuilder message) throws IOException {
            // For a Value message, only the value of the field is formatted.
            Map<FieldDescriptor, Object> fields = message.getAllFields();
            if (fields.isEmpty()) {
                // No value set.
                generator.print("null");
                return;
            }
            // A Value message can only have at most one field set (it only contains
            // an oneof).
            if (fields.size() != 1) {
                throw new InvalidProtocolBufferException("Invalid Value type.");
            }
            for (Map.Entry<FieldDescriptor, Object> entry : fields.entrySet()) {
                printSingleFieldValue(entry.getKey(), entry.getValue());
            }
        }

        /**
         * Prints google.protobuf.ListValue
         */
        private void printListValue(MessageOrBuilder message) throws IOException {
            Descriptor descriptor = message.getDescriptorForType();
            FieldDescriptor field = descriptor.findFieldByName("values");
            if (field == null) {
                throw new InvalidProtocolBufferException("Invalid ListValue type.");
            }
            printRepeatedFieldValue(field, message.getField(field));
        }

        /**
         * Prints a regular message with an optional type URL.
         */
        private void print(MessageOrBuilder message, String typeUrl) throws IOException {
            generator.print("{" + blankOrNewLine);
            generator.indent();

            boolean printedField = false;
            if (typeUrl != null) {
                generator.print("\"@type\":" + blankOrSpace + gson.toJson(typeUrl));
                printedField = true;
            }
            Map<FieldDescriptor, Object> fieldsToPrint;
            fieldsToPrint = message.getAllFields();
            for (Map.Entry<FieldDescriptor, Object> field : fieldsToPrint.entrySet()) {
                if (printedField) {
                    // Add line-endings for the previous field.
                    generator.print("," + blankOrNewLine);
                } else {
                    printedField = true;
                }
                printField(field.getKey(), field.getValue());
            }

            // Add line-endings for the last field.
            if (printedField) {
                generator.print(blankOrNewLine);
            }
            generator.outdent();
            generator.print("}");
        }

        private void printField(FieldDescriptor field, Object value) throws IOException {
            generator.print("\"" + field.getJsonName() + "\":" + blankOrSpace);
            if (field.isMapField()) {
                printMapFieldValue(field, value);
            } else if (field.isRepeated()) {
                printRepeatedFieldValue(field, value);
            } else {
                printSingleFieldValue(field, value);
            }
        }

        @SuppressWarnings("rawtypes")
        private void printRepeatedFieldValue(FieldDescriptor field, Object value) throws IOException {
            generator.print("[");
            boolean printedElement = false;
            for (Object element : (List) value) {
                if (printedElement) {
                    generator.print("," + blankOrSpace);
                } else {
                    printedElement = true;
                }
                printSingleFieldValue(field, element);
            }
            generator.print("]");
        }

        @SuppressWarnings("rawtypes")
        private void printMapFieldValue(FieldDescriptor field, Object value) throws IOException {
            Descriptor type = field.getMessageType();
            FieldDescriptor keyField = type.findFieldByName("key");
            FieldDescriptor valueField = type.findFieldByName("value");
            if (keyField == null || valueField == null) {
                throw new InvalidProtocolBufferException("Invalid map field.");
            }
            generator.print("{" + blankOrNewLine);
            generator.indent();
            boolean printedElement = false;
            for (Object element : (List) value) {
                Message entry = (Message) element;
                Object entryKey = entry.getField(keyField);
                Object entryValue = entry.getField(valueField);
                if (printedElement) {
                    generator.print("," + blankOrNewLine);
                } else {
                    printedElement = true;
                }
                // Key fields are always double-quoted.
                printSingleFieldValue(keyField, entryKey, true);
                generator.print(":" + blankOrSpace);
                printSingleFieldValue(valueField, entryValue);
            }
            if (printedElement) {
                generator.print(blankOrNewLine);
            }
            generator.outdent();
            generator.print("}");
        }

        private void printSingleFieldValue(FieldDescriptor field, Object value) throws IOException {
            printSingleFieldValue(field, value, false);
        }

        /**
         * Prints a field's value in JSON format.
         *
         * @param alwaysWithQuotes whether to always add double-quotes to primitive
         *                         types.
         */
        private void printSingleFieldValue(
                final FieldDescriptor field, final Object value, boolean alwaysWithQuotes)
                throws IOException {
            switch (field.getType()) {
                case INT32:
                case SINT32:
                case SFIXED32:
                    if (alwaysWithQuotes) {
                        generator.print("\"");
                    }
                    generator.print(value.toString());
                    if (alwaysWithQuotes) {
                        generator.print("\"");
                    }
                    break;

                case INT64:
                case SINT64:
                case SFIXED64:
                    generator.print("\"" + value.toString() + "\"");
                    break;

                case BOOL:
                    if (alwaysWithQuotes) {
                        generator.print("\"");
                    }
                    if ((Boolean) value) {
                        generator.print("true");
                    } else {
                        generator.print("false");
                    }
                    if (alwaysWithQuotes) {
                        generator.print("\"");
                    }
                    break;

                case FLOAT:
                    Float floatValue = (Float) value;
                    if (floatValue.isNaN()) {
                        generator.print("\"NaN\"");
                    } else if (floatValue.isInfinite()) {
                        if (floatValue < 0) {
                            generator.print("\"-Infinity\"");
                        } else {
                            generator.print("\"Infinity\"");
                        }
                    } else {
                        if (alwaysWithQuotes) {
                            generator.print("\"");
                        }
                        generator.print(floatValue.toString());
                        if (alwaysWithQuotes) {
                            generator.print("\"");
                        }
                    }
                    break;

                case DOUBLE:
                    Double doubleValue = (Double) value;
                    if (doubleValue.isNaN()) {
                        generator.print("\"NaN\"");
                    } else if (doubleValue.isInfinite()) {
                        if (doubleValue < 0) {
                            generator.print("\"-Infinity\"");
                        } else {
                            generator.print("\"Infinity\"");
                        }
                    } else {
                        if (alwaysWithQuotes) {
                            generator.print("\"");
                        }
                        generator.print(doubleValue.toString());
                        if (alwaysWithQuotes) {
                            generator.print("\"");
                        }
                    }
                    break;

                case UINT32:
                case FIXED32:
                    if (alwaysWithQuotes) {
                        generator.print("\"");
                    }
                    generator.print(unsignedToString((Integer) value));
                    if (alwaysWithQuotes) {
                        generator.print("\"");
                    }
                    break;

                case UINT64:
                case FIXED64:
                    generator.print("\"" + unsignedToString((Long) value) + "\"");
                    break;

                case STRING:
                    generator.print(gson.toJson(value));
                    break;

                case BYTES:
                    generator.print("\"");
                    generator.print(BaseEncoding.base64().encode(((ByteString) value).toByteArray()));
                    generator.print("\"");
                    break;

                case ENUM:
                    // Special-case google.protobuf.NullValue (it's an Enum).
                    if (field.getEnumType().getFullName().equals("google.protobuf.NullValue")) {
                        // No matter what value it contains, we always print it as "null".
                        if (alwaysWithQuotes) {
                            generator.print("\"");
                        }
                        generator.print("null");
                        if (alwaysWithQuotes) {
                            generator.print("\"");
                        }
                    } else {
                        if (((EnumValueDescriptor) value).getIndex() == -1) {
                            generator.print(String.valueOf(((EnumValueDescriptor) value).getNumber()));
                        } else {
                            generator.print("\"" + ((EnumValueDescriptor) value).getName() + "\"");
                        }
                    }
                    break;

                case MESSAGE:
                case GROUP:
                    print((Message) value);
                    break;
            }
        }
    }

    /**
     * Convert an unsigned 32-bit integer to a string.
     */
    private static String unsignedToString(final int value) {
        if (value >= 0) {
            return Integer.toString(value);
        } else {
            return Long.toString(value & 0x00000000FFFFFFFFL);
        }
    }

    /**
     * Convert an unsigned 64-bit integer to a string.
     */
    private static String unsignedToString(final long value) {
        if (value >= 0) {
            return Long.toString(value);
        } else {
            // Pull off the most-significant bit so that BigInteger doesn't think
            // the number is negative, then set it again using setBit().
            return BigInteger.valueOf(value & Long.MAX_VALUE).setBit(Long.SIZE - 1).toString();
        }
    }

    private static String getTypeName(String typeUrl) throws InvalidProtocolBufferException {
        String[] parts = typeUrl.split("/");
        if (parts.length == 1) {
            throw new InvalidProtocolBufferException("Invalid type url found: " + typeUrl);
        }
        return parts[parts.length - 1];
    }

}