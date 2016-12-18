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

import com.jomofisher.cmakeserver.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

@RunWith(JUnit4.class)
public class TestCMakeServer {
    private static OSType detectedOS;

    static {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
                detectedOS = OSType.MacOS;
            } else if (OS.indexOf("win") >= 0) {
                detectedOS = OSType.Windows;
            } else if (OS.indexOf("nux") >= 0) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
    }

    private File getTemporaryBuildOutputFolder() throws IOException {
        Path basedir = FileSystems.getDefault().getPath("test-output").toAbsolutePath();
        basedir.toFile().mkdirs();
        return Files.createTempDirectory(basedir, "cmake-bindings-test").toFile();
    }

    private File getWorkspaceFolder() {
        return new File(".").getAbsoluteFile();
    }

    private File getCMakeInstallFolder() {
        File workspaceFolder = getWorkspaceFolder();
        switch (detectedOS) {
            case Linux:
                return new File(workspaceFolder, "prebuilts/cmake-3.7.1-Linux-x86_64");
            case Windows:
                return new File(workspaceFolder, "prebuilts/cmake-3.7.1-Windows-x86_64");
            case MacOS:
                return new File(workspaceFolder, "prebuilts/cmake-3.7.1-Darwin-x86_64/CMake.app/Contents");
            default:
                throw new RuntimeException("OS not yet supported: " + detectedOS);
        }
    }

    private File getNinjaInstallFolder() {
        File workspaceFolder = getWorkspaceFolder();
        switch (detectedOS) {
            case Linux:
                return new File(workspaceFolder, "prebuilts/ninja-1.7.2-Linux");
            case Windows:
                return new File(workspaceFolder, "prebuilts/ninja-1.7.2-Windows");
            case MacOS:
                return new File(workspaceFolder, "prebuilts/ninja-1.7.2-Darwin");
            default:
                throw new RuntimeException("OS not yet supported: " + detectedOS);
        }
    }

    private CMakeServerConnectionBuilder getConnectionBuilder() {
        CMakeServerConnectionBuilder builder = new CMakeServerConnectionBuilder(getCMakeInstallFolder())
                .setAllowExtraMessageFields(false)
                .setDiagnosticReceiver(new DiagnosticReceiver() {
                    @Override
                    public void receive(String diagnosticMessage) {
                        System.err.printf(diagnosticMessage);
                    }
                })
                .setProgressReceiver(new ProgressReceiver() {
                    @Override
                    public void receive(BaseMessage message) {
                        switch (message.type) {
                            case "message":
                                MessageMessage messageMessage = (MessageMessage) message;
                                System.err.printf("Message: %s\n", messageMessage.message);
                                break;
                            case "progress":
                                ProgressMessage progressMessage = (ProgressMessage) message;
                                System.err.printf("Progress: %s of %s\n", progressMessage.progressCurrent,
                                        progressMessage.progressMaximum);
                                break;
                            default:
                                throw new RuntimeException("Unexpected message type: " + message.type);
                        }
                    }
                });

        // Add prebuilts ninja to the install path
        if (detectedOS == OSType.Windows) {
            String path = getNinjaInstallFolder() + ";" + builder.environment().get("Path");
            // Put gcc on the path
            path = "c:\\tools\\msys64\\usr\\bin;" + path;
            builder.environment().put("Path", path);
        } else {
            String path = getNinjaInstallFolder() + ":" + builder.environment().get("PATH");
            builder.environment().put("PATH", path);
        }

        return builder;
    }

    private File getSampleProjectsFolder() {
        File workspaceFolder = getWorkspaceFolder();
        return new File(workspaceFolder, "test-data/cmake-projects/");
    }

    private HandshakeMessage getHelloWorldHandshake() throws IOException {
        return new HandshakeMessage()
                .setCookie("my-cookie")
                .setGenerator("Ninja")
                .setSourceDirectory(new File(getSampleProjectsFolder(), "hello-world").getAbsolutePath().replace('\\', '/'))
                .setBuildDirectory(getTemporaryBuildOutputFolder().getAbsolutePath().replace('\\','/'))
                .setProtocolVersion(new ProtocolVersion()
                        .setMajor(1));
    }

    @Test
    public void testConnect() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
    }

    @Test
    public void testHandshake() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReplyMessage handshakeReply = connection.handshake(getHelloWorldHandshake());
    }

    @Test
    public void testConfigure() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReplyMessage handshakeReply = connection.handshake(getHelloWorldHandshake());
        ConfigureReplyMessage configureReply = connection.configure();
    }

    @Test
    public void testCompute() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReplyMessage handshakeReply = connection.handshake(getHelloWorldHandshake());
        ConfigureReplyMessage configureReply = connection.configure();
        ComputeReplyMessage computeReply = connection.compute();
    }

    @Test
    public void testCodeModel() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReplyMessage handshakeReply = connection.handshake(getHelloWorldHandshake());
        ConfigureReplyMessage configureReply = connection.configure();
        ComputeReplyMessage computeReply = connection.compute();
        CodeModelReplyMessage codemodelReply = connection.codemodel();
    }

    @Test
    public void testGlobalSettings() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReplyMessage handshakeReply = connection.handshake(getHelloWorldHandshake());
        GlobalSettingsReplyMessage globalSettingsReply = connection.globalSettings();
    }

    @Test
    public void testExample() throws Exception {
        if (false) { // Just make sure it compiles
            // Usage example
            CMakeServerConnection connection =
                    new CMakeServerConnectionBuilder(getCMakeInstallFolder())
                            .create();
            connection.handshake(new HandshakeMessage()
                    .setCookie("my-cookie")
                    .setGenerator("Ninja")
                    .setSourceDirectory("./hello-world")
                    .setBuildDirectory("./hello-world-output")
                    .setProtocolVersion(new ProtocolVersion()
                            .setMajor(1)
                            .setMinor(0)));
            connection.configure();
            connection.compute();
            CodeModelReplyMessage codemodelReply = connection.codemodel();
        }
    }

    enum OSType {
        Windows, MacOS, Linux, Other
    }
}
