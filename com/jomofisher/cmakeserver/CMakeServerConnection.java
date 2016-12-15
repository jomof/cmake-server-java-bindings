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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CMakeServerConnection {
  private final File cmakeInstallPath;
  Process process;
  BufferedReader input;
  OutputStreamWriter output;
  String connectionMessage;

  CMakeServerConnection(File cmakeInstallPath) {
    this.cmakeInstallPath = cmakeInstallPath;
  }

  private void readExpected(String expect) throws IOException {
    String found = input.readLine();
    if (!found.equals(expect)) {
      throw new RuntimeException(String.format(
          "Expected '%s' from CMake server but got '%s'\n", expect, found));
    }
  }

  private String readMessage() throws IOException {
    readExpected("[== \"CMake Server\" ==[");
    String json = input.readLine();
    readExpected("]== \"CMake Server\" ==]");
    return json;
  }

  public void connect() throws IOException {
    if (!cmakeInstallPath.isDirectory()) {
      throw new RuntimeException(
          String.format("Expected CMake install path %s to be a folder",
              cmakeInstallPath.getAbsolutePath()));
    }

    if (System.getProperty("os.name").contains("Windows")) {
      process = new ProcessBuilder(String.format("%s\\bin\\cmake", cmakeInstallPath),
          "-E", "server", "--experimental", "--debug").start();
    } else {
      process = new ProcessBuilder(String.format("%s/bin/cmake", cmakeInstallPath),
          "-E", "server", "--experimental", "--debug").start();
    }

    input = new BufferedReader(new InputStreamReader(process.getInputStream()));
    output = new OutputStreamWriter(process.getOutputStream());

    // Read the 'hello' message from CMake server.
    readExpected("");
    connectionMessage = readMessage();
    Gson gson = new GsonBuilder().create();
    HelloMessage hello = gson.fromJson(connectionMessage, HelloMessage.class);
    if (!hello.type.equals("hello")) {
      throw new RuntimeException("Expected hello message from CMake server");
    }
    System.out.printf("\n<%s>\n", connectionMessage);
  }

  private void writeMessageToServer(String message) throws IOException {
    String bracketedMessage = "[== \"CMake Server\" ==[\n" +
        message +
        "\n]== \"CMake Server\" ==]\n";
    System.out.printf("%s\n", bracketedMessage);
    output.write(bracketedMessage);
  }

  public int handshake(String message) throws IOException {
    writeMessageToServer(message);
    String result = readMessage();
    throw new RuntimeException(result);
  }

  public int handshake(String cookie, File sourceDirectory, File buildDirectory, String generator)
      throws IOException {
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

