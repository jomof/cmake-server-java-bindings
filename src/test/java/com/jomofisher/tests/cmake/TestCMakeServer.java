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
package com.jomofisher.tests.cmake;

import com.jomofisher.cmake.CMake;
import com.jomofisher.cmake.serverv1.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;

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

    private ServerConnectionBuilder getConnectionBuilder() {
        return getConnectionBuilder(getCMakeInstallFolder());
    }

    private ServerConnectionBuilder getConnectionBuilder(File cmakeInstallFolder) {
        CMake cmake = new CMake(cmakeInstallFolder);
        // Add prebuilts ninja to the install path
        if (detectedOS == OSType.Windows) {
            String path = getNinjaInstallFolder() + ";" + cmake.environment().get("Path");
            // Put gcc on the path
            path = "c:\\tools\\msys64\\usr\\bin;" + path;
            cmake.environment().put("Path", path);
        } else {
            String path = getNinjaInstallFolder() + ":" + cmake.environment().get("PATH");
            cmake.environment().put("PATH", path);
        }

        return cmake.newServerBuilder()
                .setDeserializationMonitor(new DeserializationMonitor() {
                    @Override
                    public <T> void receive(String message, Class<T> clazz) {
                        JsonUtils.checkForExtraFields(message, clazz);
                    }
                })
                .setDiagnosticReceiver(new DiagnosticReceiver() {
                    @Override
                    public void receive(String diagnosticMessage) {
                        System.err.printf(diagnosticMessage);
                    }
                })
                .setMessageReceiver(new MessageReceiver() {
                    @Override
                    public void receive(InteractiveMessage message) {
                        System.err.printf("Message: %s\n", message.message);
                    }
                })
                .setProgressReceiver(new ProgressReceiver() {
                    @Override
                    public void receive(InteractiveProgress progress) {
                        System.err.printf("Progress: %s of %s\n", progress.progressCurrent, progress.progressMaximum);
                    }
                });
    }

    private File getSampleProjectsFolder() {
        File workspaceFolder = getWorkspaceFolder();
        return new File(workspaceFolder, "test-data/cmake-projects/");
    }

    private HandshakeRequest getHelloWorldHandshake() throws IOException {
        HandshakeRequest message = new HandshakeRequest();
        message.cookie = "my-cookie";
        message.generator = "Ninja";
        message.sourceDirectory = new File(getSampleProjectsFolder(), "hello-world")
                .getAbsolutePath().replace('\\', '/');
        message.buildDirectory = getTemporaryBuildOutputFolder().getAbsolutePath().replace('\\', '/');
        ProtocolVersion version = new ProtocolVersion();
        version.major = 1;
        message.protocolVersion = version;
        return message;
    }

    private HandshakeRequest getAndroidSharedLibHandshake() throws IOException {
        HandshakeRequest message = new HandshakeRequest();
        message.cookie = "my-cookie";
        message.generator = "Ninja";
        message.sourceDirectory = new File(getSampleProjectsFolder(), "android-shared-lib")
                .getAbsolutePath().replace('\\', '/');
        message.buildDirectory = getTemporaryBuildOutputFolder().getAbsolutePath().replace('\\', '/');
        ProtocolVersion version = new ProtocolVersion();
        version.major = 1;
        message.protocolVersion = version;
        return message;
    }

    @Test
    public void testConnect() throws Exception {
        ServerConnection connection = getConnectionBuilder().create();
    }

    @Test
    public void testHandshake() throws Exception {
        ServerConnection connection = getConnectionBuilder().create();
        HandshakeResult handshakeResult = connection.handshake(getHelloWorldHandshake());
    }

    @Test
    public void testConfigure() throws Exception {
        ServerConnection connection = getConnectionBuilder().create();
        HandshakeResult handshakeResult = connection.handshake(getHelloWorldHandshake());
        connection.configure();
    }

    @Test
    public void testCompute() throws Exception {
        ServerConnection connection = getConnectionBuilder().create();
        HandshakeResult handshakeResult = connection.handshake(getHelloWorldHandshake());
        connection.configure();
        ComputeResult computeResult = connection.compute();
    }

    @Test
    public void testCodeModel() throws Exception {
        ServerConnection connection = getConnectionBuilder().create();
        HandshakeResult handshakeResult = connection.handshake(getHelloWorldHandshake());
        connection.configure();
        ComputeResult computeResult = connection.compute();
        CodeModel codemodelReply = connection.codemodel();
    }

    @Test
    public void testAndroidCodeModel() throws Exception {
        ServerConnection connection = getConnectionBuilder(getCMakeInstallFolder()).create();
        HandshakeResult handshakeResult = connection.handshake(getAndroidSharedLibHandshake());
        connection.configure(
                "-DANDROID_ABI=arm64-v8a",
                "-DANDROID_NDK=prebuilts\\android-ndk-r13b",
                "-DCMAKE_BUILD_TYPE=Debug",
                String.format("-DCMAKE_TOOLCHAIN_FILE=%s\\prebuilts\\android-ndk-r13b\\build\\cmake\\android.toolchain.cmake",
                        new File(".").getAbsolutePath()),
                "-DANDROID_NATIVE_API_LEVEL=21",
                "-DANDROID_TOOLCHAIN=gcc",
                "-DCMAKE_CXX_FLAGS=");
        ComputeResult computeResult = connection.compute();
        CodeModel codemodelReply = connection.codemodel();
        GlobalSettings globalSettings = connection.globalSettings();
    }

    @Test
    public void testGlobalSettings() throws Exception {
        ServerConnection connection = getConnectionBuilder().create();
        HandshakeResult handshakeResult = connection.handshake(getHelloWorldHandshake());
        GlobalSettings globalSettings = connection.globalSettings();
        assertThat(globalSettings.generator).isEqualTo("Ninja");
    }

    @Test
    public void testExample() throws Exception {
        if (false) { // Just make sure it compiles
            // Usage example
            ServerConnection connection = new CMake(getCMakeInstallFolder())
                    .newServerBuilder()
                    .create();
            HandshakeRequest message = new HandshakeRequest();
            message.cookie = "my-cookie";
            message.generator = "Ninja";
            message.sourceDirectory = "./hello-world";
            message.buildDirectory = "./hello-world-output";
            ProtocolVersion version = new ProtocolVersion();
            version.major = 1;
            message.protocolVersion = version;
            ConfigureResult configureResult = connection.configure();
            ComputeResult computeResult = connection.compute();
            CodeModel codemodel = connection.codemodel();
        }
    }

    enum OSType {
        Windows, MacOS, Linux, Other
    }
}
