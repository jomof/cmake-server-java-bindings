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
import static com.jomofisher.cmakeserver.JsonUtils.checkForExtraFields;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CMakeServerConnection {
  CMakeServerConnectionBuilder builder;
  Process process;
  BufferedReader input;
  BufferedWriter output;

  CMakeServerConnection(CMakeServerConnectionBuilder builder) {
    this.builder = builder;
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

  private <T extends Message> T decodeResponse(Class<T> clazz) throws IOException {
    Gson gson = new GsonBuilder()
        .create();
    String message = readMessage();
    Message messageType = gson.fromJson(message, Message.class);

    // Process interactive messages.
    while (messageType.type.equals("message") || messageType.type.equals("progress")) {
      if (builder.getProgressReceiver() != null) {
        switch (messageType.type) {
          case "message":
            builder.getProgressReceiver().receive(gson.fromJson(message, MessageMessage.class));
            break;
          case "progress":
            builder.getProgressReceiver().receive(gson.fromJson(message, ProgressMessage.class));
            break;
        }
      }
      message = readMessage();
      messageType = gson.fromJson(message, Message.class);
    }

    // Process the final message.
    switch (messageType.type) {
      case "hello":
      case "reply":
        if (!builder.getAllowExtraMessageFields()) {
          checkForExtraFields(message, clazz);
        }
        return gson.fromJson(message, clazz);
      default:
        throw new RuntimeException(message);
    }
  }

  public HelloMessage connect() throws IOException {
    ProcessBuilder processBuilder;
    if (System.getProperty("os.name").contains("Windows")) {
      processBuilder = new ProcessBuilder(String.format("%s\\bin\\cmake",
          this.builder.getCmakeInstallPath()),
          "-E", "server", "--experimental", "--debug");
    } else {
      processBuilder = new ProcessBuilder(String.format("%s/bin/cmake",
          this.builder.getCmakeInstallPath()),
          "-E", "server", "--experimental", "--debug");
    }

    processBuilder.environment().putAll(builder.environment());
    process = processBuilder.start();

    input = new BufferedReader(new InputStreamReader(process.getInputStream()));
    output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

    return decodeResponse(HelloMessage.class);
  }

  public HandshakeReplyMessage handshake(String message) throws IOException {
    writeMessage(message);
    return decodeResponse(HandshakeReplyMessage.class);
  }

  public HandshakeReplyMessage handshake(String cookie, File sourceDirectory,
      File buildDirectory, String generator) throws IOException {
    if (!new File(sourceDirectory, "CMakeLists.txt").exists()) {
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
        sourceDirectory.getAbsolutePath(),
        buildDirectory.getAbsolutePath(),
        generator));
  }

  public HandshakeReplyMessage handshake(String cookie, File sourceDirectory,
      File buildDirectory) throws IOException {
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
            + "\"buildDirectory\":\"%s\"}",
        cookie,
        sourceDirectory.getAbsolutePath(),
        buildDirectory.getAbsolutePath()));
  }

  public GlobalSettingsReplyMessage globalSettings() throws IOException {
    writeMessage("{\"type\":\"globalSettings\"}");
    return decodeResponse(GlobalSettingsReplyMessage.class);
  }

  public ConfigureReplyMessage configure (String... cacheArguments) throws IOException {
    StringBuilder cacheArgumentBuilder = new StringBuilder();
    for (int i = 0; i < cacheArguments.length; ++i) {
      if (i != 0) {
        cacheArgumentBuilder.append(", ");
      }
      cacheArgumentBuilder.append(String.format("\"%s\"", cacheArguments[i]));
    }
    writeMessage("{\"type\":\"configure\", \"cacheArguments\":["
        + cacheArgumentBuilder.toString() + "]}");
    return decodeResponse(ConfigureReplyMessage.class);
  }

  public ComputeReplyMessage compute() throws IOException {
    writeMessage("{\"type\":\"compute\"}");
    return decodeResponse(ComputeReplyMessage.class);
  }
}

