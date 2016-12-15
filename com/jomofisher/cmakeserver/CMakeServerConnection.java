/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jomofisher.cmakeserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class CMakeServerConnection {
  private final File cmakeInstallPath;
  private final boolean allowExtraMessageFields;
  Process process;
  BufferedReader input;
  BufferedWriter output;

  CMakeServerConnection(File cmakeInstallPath) {
    this.cmakeInstallPath = cmakeInstallPath;
    this.allowExtraMessageFields = true;
  }

  CMakeServerConnection(File cmakeInstallPath, boolean allowExtraMessageFields) {
    this.cmakeInstallPath = cmakeInstallPath;
    this.allowExtraMessageFields = allowExtraMessageFields;
  }

  private String readLine() throws IOException {
    System.err.printf("Reading: ");
    String line = input.readLine();
    System.err.printf(line + "\n");
    return line;
  }

  private void writeLine(String message) throws IOException {
    System.err.printf("Writing: %s\n", message);
    output.write(message);
    output.newLine();
  }

  private void readExpected(String expect) throws IOException {
    String found = readLine();
    if (found.equals(expect)) {
      return;
    }
    // Skip a blank line if there is one.
    if (found.length()==0) {
      readExpected(expect);
      return;
    }
    throw new RuntimeException(String.format(
        "Expected '%s' from CMake server but got '%s' (%s)\n", expect, found, found.length()));
  }

  private String readMessage() throws IOException {
    readExpected("[== \"CMake Server\" ==[");
    String json = readLine();
    readExpected("]== \"CMake Server\" ==]");
    return json;
  }

  private void writeMessage(String message) throws IOException {
    writeLine("[== \"CMake Server\" ==[");
    writeLine(message);
    writeLine("]== \"CMake Server\" ==]");
    output.flush();
  }

  private static Set<String> getFieldNames(Class clazz) {
    System.err.printf("Adding fields for %s\n", clazz);
    Set<String> fields = new HashSet<>();
    for (Field field : clazz.getFields()) {
      System.err.printf("Adding %s\n", field.getName());
      fields.add(field.getName());
    }
    Class parent = clazz.getSuperclass();
    if (parent != null) {
      fields.addAll(getFieldNames(parent));
    }
    return fields;
  }

  private static <T extends Message> void checkForExtraFields(String message, Class<T> clazz) {
    JsonObject root = new JsonParser().parse(message).getAsJsonObject();
    Set<String> fieldNames = getFieldNames(clazz);
    for (Entry<String, JsonElement> element : root.entrySet()) {
      if (!fieldNames.contains(element.getKey())) {
        throw new RuntimeException(String.format("Did not find field %s in class %s",
            element.getKey(), clazz.getSimpleName()));
      }
    }
  }

  private <T extends Message> T decodeMessage(String message, Class<T> clazz) {
    if (!allowExtraMessageFields) {
      checkForExtraFields(message, clazz);
    }
    Gson gson = new GsonBuilder()
        .create();
    Message messageType = gson.fromJson(message, Message.class);
    switch (messageType.type) {
      case "hello":
      case "reply":
        return gson.fromJson(message, clazz);
      default:
        throw new RuntimeException(message);
    }
  }

  public HelloMessage connect() throws IOException {
    if (System.getProperty("os.name").contains("Windows")) {
      process = new ProcessBuilder(String.format("%s\\bin\\cmake", cmakeInstallPath),
          "-E", "server", "--experimental", "--debug").start();
    } else {
      process = new ProcessBuilder(String.format("%s/bin/cmake", cmakeInstallPath),
          "-E", "server", "--experimental", "--debug").start();
    }

    input = new BufferedReader(new InputStreamReader(process.getInputStream()));
    output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

    return decodeMessage(readMessage(), HelloMessage.class);
  }

  public HandshakeReplyMessage handshake(String message) throws IOException {
    writeMessage(message);
    return decodeMessage(readMessage(), HandshakeReplyMessage.class);
  }

  public HandshakeReplyMessage handshake(String cookie, File sourceDirectory,
      File buildDirectory, String generator) throws IOException {
    if (!sourceDirectory.isDirectory()) {
      throw new RuntimeException(String.format(
          "Expected sourceDirectory %s to exist", sourceDirectory));
    }
    if (!new File(sourceDirectory, "CMakeLists.txt").isFile()) {
      throw new RuntimeException(String.format(
          "Expected sourceDirectory %s to contain CMakeLists.txt", sourceDirectory));
    }
    return handshake(String.format(
        "{\"cookie\":\"%s\", "
            + "\"type\":\"handshake\", "
            + "\"protocolVersion\":{\"major\":1}, "
            + "\"sourceDirectory\":\"%s\", "
            + "\"buildDirectory\":\"%s\", "
            + "\"generator\":\"%s\"}",
        cookie,
        sourceDirectory,
        buildDirectory,
        generator));
  }
}

