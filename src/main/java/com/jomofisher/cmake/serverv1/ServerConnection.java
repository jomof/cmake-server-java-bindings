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
package com.jomofisher.cmake.serverv1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ServerConnection {
    private final ServerConnectionBuilder builder;
    private BufferedReader input;
    private BufferedWriter output;
    private HelloResult helloResult;

    ServerConnection(ServerConnectionBuilder builder) {
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
        diagnostic("%s\n", message);
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

    private <T> T decodeResponse(Class<T> clazz) throws IOException {
        Gson gson = new GsonBuilder()
                .create();
        String message = readMessage();
        String messageType = gson.fromJson(message, TypeOfMessage.class).type;

        // Process interactive messages.
        while (messageType.equals("message") || messageType.equals("progress")) {
            switch (messageType) {
                case "message":
                    if (builder.getMessageReceiver() != null) {
                        builder.getMessageReceiver().receive(gson.fromJson(message, InteractiveMessage.class));
                    }
                    break;
                case "progress":
                    if (builder.getProgressReceiver() != null) {
                        builder.getProgressReceiver().receive(gson.fromJson(message, InteractiveProgress.class));
                        break;
                    }
            }
            message = readMessage();
            messageType = gson.fromJson(message, TypeOfMessage.class).type;
        }

        // Process the final message.
        switch (messageType) {
            case "hello":
            case "reply":
                if (builder.getDeserializationMonitor() != null) {
                    builder.getDeserializationMonitor().receive(message, clazz);
                }
                return gson.fromJson(message, clazz);
            default:
                throw new RuntimeException(message);
        }
    }

    public HelloResult getConnectionHelloResult() {
        return this.helloResult;
    }

    public void connect() throws IOException {
        ProcessBuilder processBuilder;
        if (System.getProperty("os.name").contains("Windows")) {
            processBuilder = new ProcessBuilder(String.format("%s\\cmake",
                    this.builder.getCmakeInstallPath()),
                    "-E", "server", "--experimental", "--debug");
        } else {
            processBuilder = new ProcessBuilder(String.format("%s/cmake",
                    this.builder.getCmakeInstallPath()),
                    "-E", "server", "--experimental", "--debug");
        }

        processBuilder.environment().putAll(builder.environment());
        Process process = processBuilder.start();

        input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        helloResult = decodeResponse(HelloResult.class);
    }

    public HandshakeResult handshake(HandshakeRequest message) throws IOException {
        writeMessage(new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(message));
        return decodeResponse(HandshakeResult.class);
    }

    public GlobalSettings globalSettings() throws IOException {
        writeMessage("{\"type\":\"globalSettings\"}");
        return decodeResponse(GlobalSettings.class);
    }

    public ConfigureResult configure(String... cacheArguments) throws IOException {
        ConfigureRequest message = new ConfigureRequest();

        // Insert a blank element to work around a bug in CMake 3.7.1 where the first element is ignored.
        message.cacheArguments = new String[cacheArguments.length + 1];
        message.cacheArguments[0] = "";
        System.arraycopy(cacheArguments, 0, message.cacheArguments, 1, cacheArguments.length);

        writeMessage(new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(message));
        return decodeResponse(ConfigureResult.class);
    }

    public ComputeResult compute() throws IOException {
        writeMessage("{\"type\":\"compute\"}");
        return decodeResponse(ComputeResult.class);
    }

    public CodeModel codemodel() throws IOException {
        writeMessage("{\"type\":\"codemodel\"}");
        return decodeResponse(CodeModel.class);
    }
}

