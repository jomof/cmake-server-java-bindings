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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jomofisher.cmakeserver.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static com.jomofisher.cmakeserver.JsonUtils.checkForExtraFields;

@SuppressWarnings("unused")
public class CMakeServerConnection {
    private final CMakeServerConnectionBuilder builder;
    private BufferedReader input;
    private BufferedWriter output;

    CMakeServerConnection(CMakeServerConnectionBuilder builder) {
        this.builder = builder;
    }

    private void diagnostic(String format, Object... args) {
        if (builder.getDiagnosticReceiver() != null) {
            builder.getDiagnosticReceiver().receive(String.format(format, args));
        }
    }

    private String readLine() throws IOException {
        diagnostic("Reading: ");
        String line = input.readLine();
        diagnostic(line + "\n");
        return line;
    }

    private void writeLine(String message) throws IOException {
        diagnostic("Writing: %s\n", message);
        output.write(message);
        output.newLine();
    }

    private void readExpected(String expect) throws IOException {
        String found = readLine();
        if (found.equals(expect)) {
            return;
        }
        // Skip a blank line if there is one.
        if (found.length() == 0) {
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

    private <T extends BaseMessage> T decodeResponse(Class<T> clazz) throws IOException {
        Gson gson = new GsonBuilder()
                .create();
        String message = readMessage();
        BaseMessage messageType = gson.fromJson(message, BaseMessage.class);

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
            messageType = gson.fromJson(message, BaseMessage.class);
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
        Process process = processBuilder.start();

        input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        return decodeResponse(HelloMessage.class);
    }

    private HandshakeReplyMessage handshake(String message) throws IOException {
        writeMessage(message);
        return decodeResponse(HandshakeReplyMessage.class);
    }

    private void checkFolderExists(String path) {
        if (path == null) {
            throw new RuntimeException("Path was null");
        }
        File directory = new File(path);
        if (!directory.isDirectory()) {
            File parent = directory;
            while (parent != null && !parent.isDirectory()) {
                parent = parent.getParentFile();
            }
            if (parent == null) {
                throw new RuntimeException(String.format(
                        "Folder %s didn't exist and neither did any parent folders", directory));
            }
            throw new RuntimeException(String.format(
                    "Folder %s didn't exist. Nearest existing parent folder is %s",
                    directory, parent));
        }
    }

    public HandshakeReplyMessage handshake(HandshakeMessage message) throws IOException {
        checkFolderExists(message.buildDirectory);
        checkFolderExists(message.sourceDirectory);
        writeMessage(new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(message));
        return decodeResponse(HandshakeReplyMessage.class);
    }

    public GlobalSettingsReplyMessage globalSettings() throws IOException {
        writeMessage("{\"type\":\"globalSettings\"}");
        return decodeResponse(GlobalSettingsReplyMessage.class);
    }

    public ConfigureReplyMessage configure(String... cacheArguments) throws IOException {
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

    public CodeModelReplyMessage codemodel() throws IOException {
        writeMessage("{\"type\":\"codemodel\"}");
        return decodeResponse(CodeModelReplyMessage.class);
    }
}

