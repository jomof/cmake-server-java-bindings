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

import static com.google.common.truth.Truth.assertThat;

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
        String cmakeName = System.getenv().get("TARGET_CMAKE_NAME");
        if (cmakeName == null) {
            switch (detectedOS) {
                case Linux:
                    cmakeName = "cmake-3.7.1-Linux-x86_64";
                    break;
                case Windows:
                    cmakeName = "cmake-3.7.1-Windows-x86_64";
                    break;
                case MacOS:
                    cmakeName = "cmake-3.7.1-Darwin-x86_64";
                    break;
                default:
                    throw new RuntimeException("OS not yet supported: " + detectedOS);
            }
        }

        File workspaceFolder = getWorkspaceFolder();
        switch (detectedOS) {
            case MacOS:
                return new File(workspaceFolder, String.format("prebuilts/%s/CMake.app/Contents/bin", cmakeName));
            default:
                return new File(workspaceFolder, String.format("prebuilts/%s/bin", cmakeName));
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
        return getConnectionBuilder(getCMakeInstallFolder());
    }
    private CMakeServerConnectionBuilder getConnectionBuilder(File cmakeInstallFolder) {
        CMakeServerConnectionBuilder builder = new CMakeServerConnectionBuilder(cmakeInstallFolder)
                //.setAllowExtraMessageFields(false)
                .setDiagnosticReceiver(new DiagnosticReceiver() {
                    @Override
                    public void receive(String diagnosticMessage) {
                        System.err.printf(diagnosticMessage);
                    }
                })
                .setProgressReceiver(new ProgressReceiver() {
                    @Override
                    public void receiveMessage(MessageReply message) {
                        System.err.printf("Message: %s\n", message.getMessage());
                    }

                    @Override
                    public void receiveProgress(ProgressReply progress) {
                        System.err.printf("Progress: %s of %s\n", progress.getProgressCurrent(),
                                       progress.getProgressMaximum());
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
        return HandshakeMessage.newBuilder()
                .setCookie("my-cookie")
                .setGenerator("Ninja")
                .setSourceDirectory(new File(getSampleProjectsFolder(), "hello-world")
                        .getAbsolutePath().replace('\\', '/'))
                .setBuildDirectory(getTemporaryBuildOutputFolder().getAbsolutePath().replace('\\', '/'))
                .setProtocolVersion(ProtocolVersion.newBuilder()
                        .setMajor(1))
                .build();
    }

    private HandshakeMessage getAndroidSharedLibHandshake() throws IOException {
        return HandshakeMessage.newBuilder()
                .setCookie("my-cookie")
                .setGenerator("Ninja")
                .setSourceDirectory(new File(getSampleProjectsFolder(), "android-shared-lib")
                        .getAbsolutePath().replace('\\', '/'))
                .setBuildDirectory(getTemporaryBuildOutputFolder().getAbsolutePath().replace('\\', '/'))
                .setProtocolVersion(ProtocolVersion.newBuilder()
                        .setMajor(1))
                .build();
    }

    @Test
    public void testConnect() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
    }

    @Test
    public void testHandshake() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReply handshakeReply = connection.handshake(getHelloWorldHandshake());
    }

    @Test
    public void testConfigure() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReply handshakeReply = connection.handshake(getHelloWorldHandshake());
        connection.configure();
    }

    @Test
    public void testCompute() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReply handshakeReply = connection.handshake(getHelloWorldHandshake());
        connection.configure();
        ComputeReply computeReply = connection.compute();
    }

    @Test
    public void testCodeModel() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReply handshakeReply = connection.handshake(getHelloWorldHandshake());
        connection.configure();
        ComputeReply computeReply = connection.compute();
        CodeModelReply codemodelReply = connection.codemodel();
    }

    @Test
    public void testAndroidCodeModel() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder(getCMakeInstallFolder()).create();
        HandshakeReply handshakeReply = connection.handshake(getAndroidSharedLibHandshake());
        connection.configure(
                "-DANDROID_ABI=arm64-v8a",
                "-DANDROID_NDK=prebuilts\\android-ndk-r13b",
                "-DCMAKE_BUILD_TYPE=Debug",
                String.format("-DCMAKE_TOOLCHAIN_FILE=%s\\prebuilts\\android-ndk-r13b\\build\\cmake\\android.toolchain.cmake",
                        new File(".").getAbsolutePath()),
                "-DANDROID_NATIVE_API_LEVEL=21",
                "-DANDROID_TOOLCHAIN=gcc",
                "-DCMAKE_CXX_FLAGS=");
        ComputeReply computeReply = connection.compute();
        CodeModelReply codemodelReply = connection.codemodel();
    }

    @Test
    public void testGlobalSettings() throws Exception {
        CMakeServerConnection connection = getConnectionBuilder().create();
        HandshakeReply handshakeReply = connection.handshake(getHelloWorldHandshake());
        GlobalSettingsReply globalSettingsReply = connection.globalSettings();
        assertThat(globalSettingsReply.getGenerator()).isEqualTo("Ninja");
    }

    @Test
    public void testExample() throws Exception {
        if (false) { // Just make sure it compiles
            // Usage example
            CMakeServerConnection connection =
                    new CMakeServerConnectionBuilder(getCMakeInstallFolder())
                            .create();
            connection.handshake(HandshakeMessage.newBuilder()
                    .setCookie("my-cookie")
                    .setGenerator("Ninja")
                    .setSourceDirectory("./hello-world")
                    .setBuildDirectory("./hello-world-output")
                    .setProtocolVersion(ProtocolVersion.newBuilder()
                            .setMajor(1)
                            .setMinor(0))
                    .build());
            connection.configure();
            connection.compute();
            CodeModelReply codemodelReply = connection.codemodel();
        }
    }

    enum OSType {
        Windows, MacOS, Linux, Other
    }
}
