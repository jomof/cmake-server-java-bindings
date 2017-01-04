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

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jomofisher.cmake.CMake;
import com.jomofisher.cmake.database.Compilation;
import com.jomofisher.cmake.serverv1.*;
import com.jomofisher.literatehash.LiterateHash;
import com.jomofisher.tests.cmake.model.AndroidGradleBuild;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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
        return new File(basedir.toFile(), String.format("build-output-%s",
                LiterateHash.of(new Random().nextLong())));
    }

    private File getWorkspaceFolder() {
        return new File(".").getAbsoluteFile();
    }

    private File getAndroidStudioCMakeExecutable() {
        switch (detectedOS) {
            case Linux:
                return new File("./prebuilts/android-studio-cmake-3.6.3155560-linux-x86_64/bin/cmake");
            case Windows:
                return new File("./prebuilts/android-studio-cmake-3.6.3155560-windows-x86_64/bin/cmake.exe");
            case MacOS:
                return new File("./prebuilts/android-studio-cmake-3.6.3155560-darwin-x86_64/bin/cmake");
            default:
                throw new RuntimeException("OS not yet supported: " + detectedOS);
        }
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

    private CMake getCMake() {
        return new CMake(getCMakeInstallFolder());
    }

    private ServerConnectionBuilder getConnectionBuilder() {
        return getConnectionBuilder(getCMake());
    }

    private void setUpCmakeEnvironment(Map<String, String> environment) {
        // Add prebuilts ninja to the install path
        if (detectedOS == OSType.Windows) {
            String path = getNinjaInstallFolder() + ";" + environment.get("Path");
            // Put gcc on the path
            path = "c:\\tools\\msys64\\usr\\bin;" + path;
            environment.put("Path", path);
        } else {
            String path = getNinjaInstallFolder() + ":" + environment.get("PATH");
            environment.put("PATH", path);
        }
    }

    private ServerConnectionBuilder getConnectionBuilder(CMake cmake) {
        setUpCmakeEnvironment(cmake.environment());

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
                .setSignalReceiver(new SignalReceiver() {
                    @Override
                    public void receive(InteractiveSignal signal) {
                        System.err.printf("Signal: %s\n", signal.name);
                        assertThat(signal.cookie).isNotNull();
                        assertThat(signal.inReplyTo).isNotNull();
                        assertThat(signal.type).isNotNull();
                        assertThat(signal.type).isEqualTo("signal");
                    }
                })
                .setMessageReceiver(new MessageReceiver() {
                    @Override
                    public void receive(InteractiveMessage message) {
                        System.err.printf("Message: %s\n", message.message);
                        assertThat(message.cookie).isNotNull();
                        assertThat(message.inReplyTo).isNotNull();
                        assertThat(message.message).isNotNull();
                        assertThat(message.type).isNotNull();
                        assertThat(message.type).isEqualTo("message");
                    }
                })
                .setProgressReceiver(new ProgressReceiver() {
                    @Override
                    public void receive(InteractiveProgress progress) {
                        System.err.printf("Progress: %s of %s\n", progress.progressCurrent, progress.progressMaximum);
                        assertThat(progress.cookie).isNotNull();
                        assertThat(progress.inReplyTo).isNotNull();
                        assertThat(progress.progressMinimum).isNotNull();
                        assertThat(progress.progressCurrent).isNotNull();
                        assertThat(progress.progressMaximum).isNotNull();
                        assertThat(progress.progressMessage).isNotNull();
                        assertThat(progress.type).isNotNull();
                        assertThat(progress.type).isEqualTo("progress");
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
        message.sourceDirectory = new File(getSampleProjectsFolder(), "hello-world").getCanonicalFile()
                .getAbsolutePath().replace('\\', '/');
        message.buildDirectory = getTemporaryBuildOutputFolder().getCanonicalFile()
                .getAbsolutePath().replace('\\', '/');
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
    public void testCompilationDatabase() throws Exception {
        CMake cmake = new CMake(getCMakeInstallFolder());
        ServerConnection connection = getConnectionBuilder(cmake).create();
        HandshakeRequest handshake = getHelloWorldHandshake();
        connection.handshake(handshake);
        connection.configure("-DCMAKE_EXPORT_COMPILE_COMMANDS=1");
        connection.compute();
        Compilation[] database = cmake.getCompilationDatabase(new File(handshake.buildDirectory));
        assertThat(database).hasLength(1);
        assertThat(database[0].command).isNotNull();
        assertThat(database[0].directory).isNotNull();
        assertThat(database[0].file).isNotNull();
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
        HandshakeRequest handshakeRequest = getHelloWorldHandshake();
        HandshakeResult handshakeResult = connection.handshake(handshakeRequest);
        connection.configure();
        ComputeResult computeResult = connection.compute();
        CodeModel codemodelReply = connection.codemodel();
        recordUnique(codemodelReply, handshakeRequest.buildDirectory, handshakeRequest.sourceDirectory);
    }

    @Test
    public void testAndroidStudioCMakeExecutableExists() {
        if ("1".equals(System.getenv().get("NO_ANDROID_STUDIO_CMAKE_ON_THIS_OS"))) {
            return;
        }
        assertThat(getAndroidStudioCMakeExecutable().isFile())
                .named(getAndroidStudioCMakeExecutable().getAbsolutePath())
                .isTrue();
    }

    @Test
    public void testAndroidCodeModelAgainstAndroidStudio() throws Exception {
        if ("1".equals(System.getenv().get("NO_ANDROID_STUDIO_CMAKE_ON_THIS_OS"))) {
            return;
        }
        ServerConnection connection = getConnectionBuilder(getCMake()).create();
        HelloResult helloResult = connection.getConnectionHelloResult();

        HandshakeRequest handshakeRequest = getAndroidSharedLibHandshake();
        HandshakeResult handshakeResult = connection.handshake(handshakeRequest);
        String toolChain = new File("prebuilts/android-ndk-r13b/build/cmake/android.toolchain.cmake")
                .getAbsolutePath();
        ConfigureResult configureResult = connection.configure(
                "-DANDROID_ABI=arm64-v8a",
                "-DANDROID_NDK=prebuilts\\android-ndk-r13b",
                "-DCMAKE_BUILD_TYPE=Debug",
                "-DCMAKE_TOOLCHAIN_FILE=" + toolChain,
                "-DANDROID_NATIVE_API_LEVEL=21",
                "-DANDROID_TOOLCHAIN=gcc",
                "-DCMAKE_EXPORT_COMPILE_COMMANDS=1",
                "-DCMAKE_CXX_FLAGS=");

        ComputeResult computeResult = connection.compute();
        CodeModel codemodelReply = connection.codemodel();

        // Call Android Studio fork of CMake to get android_gradle.json
        String androidStudioBuildDirectory = getTemporaryBuildOutputFolder().getAbsolutePath().replace('\\', '/');
        assertThat(getAndroidStudioCMakeExecutable().isFile())
                .named(getAndroidStudioCMakeExecutable().getAbsolutePath())
                .isTrue();
        ProcessBuilder processBuilder = new ProcessBuilder(
                getAndroidStudioCMakeExecutable().getAbsolutePath(),
                "-H" + handshakeRequest.sourceDirectory,
                "-B" + androidStudioBuildDirectory,
                "-GAndroid Gradle - Ninja",
                "-DANDROID_ABI=arm64-v8a",
                "-DANDROID_NDK=prebuilts\\android-ndk-r13b",
                "-DCMAKE_BUILD_TYPE=Debug",
                "-DCMAKE_MAKE_PROGRAM=" + getNinjaInstallFolder().getAbsolutePath() + "/ninja",
                "-DCMAKE_TOOLCHAIN_FILE=" + toolChain,
                "-DANDROID_NATIVE_API_LEVEL=21",
                "-DCMAKE_EXPORT_COMPILE_COMMANDS=1",
                "-DANDROID_TOOLCHAIN=gcc",
                "-DCMAKE_CXX_FLAGS=");

        setUpCmakeEnvironment(processBuilder.environment());
        processBuilder.redirectErrorStream();
        Process process = processBuilder.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        System.err.println("android-studio-out: before");
        while ((line = in.readLine()) != null) {
            System.err.println("android-studio-out: " + line);
        }
        System.err.println("android-studio-out: after");
        assertThat(process.waitFor()).named("Android CMake process").isEqualTo(0);

        File androidGradleBuildJson = new File(androidStudioBuildDirectory, "android_gradle_build.json");
        assertThat(androidGradleBuildJson.isFile())
                .named(androidGradleBuildJson.getAbsolutePath())
                .isTrue();
        String androidGradleBuildText = new String(Files.readAllBytes(androidGradleBuildJson.toPath()));
        Gson gson = new GsonBuilder()
                .create();
        AndroidGradleBuild androidGradleBuild = gson.fromJson(androidGradleBuildText, AndroidGradleBuild.class);
        JsonUtils.checkForExtraFields(androidGradleBuildText, AndroidGradleBuild.class);
        recordUnique(codemodelReply, androidGradleBuild, handshakeRequest.buildDirectory, androidStudioBuildDirectory,
                handshakeRequest.sourceDirectory);

        // Translate codeModel into android studio model
        AndroidGradleBuild translated = AndroidGradleBuild.of(
                codemodelReply,
                getAndroidStudioCMakeExecutable().getAbsolutePath(),
                "arm64-v8a"


        );
    }

    @Test
    public void testAndroidStudioCMakeVersion() throws Exception {
        if ("1".equals(System.getenv().get("NO_ANDROID_STUDIO_CMAKE_ON_THIS_OS"))) {
            return;
        }
        CMake cmake = new CMake(getAndroidStudioCMakeExecutable().getParentFile());
        String version = cmake.getVersionString();
        assertThat(version).isEqualTo("3.6.1-rc2");
    }

    @Test
    public void testAndroidCodeModel() throws Exception {
        ServerConnection connection = getConnectionBuilder(getCMake()).create();
        HelloResult helloResult = connection.getConnectionHelloResult();

        assertThat(helloResult.type).isNotNull();
        assertThat(helloResult.supportedProtocolVersions).isNotNull();
        assertThat(helloResult.supportedProtocolVersions).isNotEmpty();
        assertThat(helloResult.supportedProtocolVersions[0].isExperimental).isNotNull();
        assertThat(helloResult.supportedProtocolVersions[0].major).isNotNull();
        assertThat(helloResult.supportedProtocolVersions[0].minor).isNotNull();

        HandshakeRequest handshakeRequest = getAndroidSharedLibHandshake();
        HandshakeResult handshakeResult = connection.handshake(handshakeRequest);

        assertThat(handshakeResult.cookie).isNotNull();
        assertThat(handshakeResult.inReplyTo).isNotNull();
        assertThat(handshakeResult.type).isNotNull();

        ConfigureResult configureResult = connection.configure(
                "-DANDROID_ABI=arm64-v8a",
                "-DANDROID_NDK=prebuilts\\android-ndk-r13b",
                "-DCMAKE_BUILD_TYPE=Debug",
                String.format("-DCMAKE_TOOLCHAIN_FILE=%s\\prebuilts\\android-ndk-r13b\\build\\cmake\\android.toolchain.cmake",
                        new File(".").getAbsolutePath()),
                "-DANDROID_NATIVE_API_LEVEL=21",
                "-DCMAKE_EXPORT_COMPILE_COMMANDS=1",
                "-DANDROID_TOOLCHAIN=gcc",
                "-DCMAKE_CXX_FLAGS=");

        assertThat(configureResult.cookie).isNotNull();
        assertThat(configureResult.inReplyTo).isNotNull();
        assertThat(configureResult.type).isNotNull();

        ComputeResult computeResult = connection.compute();

        assertThat(computeResult.cookie).isNotNull();
        assertThat(computeResult.inReplyTo).isNotNull();
        assertThat(computeResult.type).isNotNull();

        CodeModel codemodelReply = connection.codemodel();

        assertThat(codemodelReply.type).isNotNull();
        assertThat(codemodelReply.configurations).isNotNull();
        assertThat(codemodelReply.cookie).isNotNull();
        assertThat(codemodelReply.inReplyTo).isNotNull();
        assertThat(codemodelReply.configurations).isNotEmpty();
        assertThat(codemodelReply.configurations[0].name).isNotNull();
        assertThat(codemodelReply.configurations[0].projects).isNotNull();
        assertThat(codemodelReply.configurations[0].projects).isNotEmpty();
        assertThat(codemodelReply.configurations[0].projects[0].buildDirectory).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].name).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].sourceDirectory).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets).isNotEmpty();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].artifacts).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].buildDirectory).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].linkerLanguage).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fullName).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].sysroot).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].type).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].linkFlags).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].sourceDirectory).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].linkLibraries).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].name).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups).isNotEmpty();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups[0].compileFlags).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups[0].defines).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups[0].isGenerated).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups[0].language).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups[0].sources).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups[0].includePath).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups[0].includePath).isNotEmpty();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups[0].includePath[0].isSystem).isNotNull();
        assertThat(codemodelReply.configurations[0].projects[0].targets[0].fileGroups[0].includePath[0].path).isNotNull();

        GlobalSettings globalSettings = connection.globalSettings();
        assertThat(globalSettings).isNotNull();
        assertThat(globalSettings.type).isNotNull();
        assertThat(globalSettings.cookie).isNotNull();
        assertThat(globalSettings.inReplyTo).isNotNull();
        assertThat(globalSettings.buildDirectory).isNotNull();
        assertThat(globalSettings.checkSystemVars).isNotNull();
        assertThat(globalSettings.debugOutput).isNotNull();
        assertThat(globalSettings.extraGenerator).isNotNull();
        assertThat(globalSettings.generator).isNotNull();
        assertThat(globalSettings.sourceDirectory).isNotNull();
        assertThat(globalSettings.trace).isNotNull();
        assertThat(globalSettings.traceExpand).isNotNull();
        assertThat(globalSettings.warnUninitialized).isNotNull();
        assertThat(globalSettings.warnUnused).isNotNull();
        assertThat(globalSettings.warnUnusedCli).isNotNull();
        assertThat(globalSettings.capabilities).isNotNull();
        assertThat(globalSettings.capabilities.serverMode).isNotNull();
        assertThat(globalSettings.capabilities.version).isNotNull();
        assertThat(globalSettings.capabilities.generators).isNotNull();
        assertThat(globalSettings.capabilities.generators).isNotEmpty();
        assertThat(globalSettings.capabilities.generators[0].extraGenerators).isNotNull();
        assertThat(globalSettings.capabilities.generators[0].name).isNotNull();
        assertThat(globalSettings.capabilities.generators[0].platformSupport).isNotNull();
        assertThat(globalSettings.capabilities.generators[0].toolsetSupport).isNotNull();
        assertThat(globalSettings.capabilities.version.isDirty).isNotNull();
        assertThat(globalSettings.capabilities.version.major).isNotNull();
        assertThat(globalSettings.capabilities.version.minor).isNotNull();
        assertThat(globalSettings.capabilities.version.patch).isNotNull();
        assertThat(globalSettings.capabilities.version.string).isNotNull();
        assertThat(globalSettings.capabilities.version.suffix).isNotNull();

        recordUnique(codemodelReply, handshakeRequest.buildDirectory, handshakeRequest.sourceDirectory);
    }

    private String replaceWithSlashCombinations(String string, String search, String replace) {
        string = string.replace(search, replace);
        string = string.replace(search.replace("/", "\\\\"), replace);
        string = string.replace(search.replace("\\\\", "/"), replace);
        return string;
    }

    private CodeModel recordUnique(
            CodeModel codemodel,
            String buildDirectory,
            String sourceDirectory) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String string = gson.toJson(codemodel);
        string = replaceWithSlashCombinations(string, buildDirectory, "${buildDirectory}");
        string = replaceWithSlashCombinations(string, sourceDirectory, "${sourceDirectory}");
        File exampleMessages = new File("./example-messages");
        exampleMessages.mkdir();
        File outFile = new File(exampleMessages, String.format("codemodel-%s.json",
                LiterateHash.of(string)));
        com.google.common.io.Files.write(string, outFile, Charsets.UTF_8);
        return codemodel;
    }

    private CodeModel recordUnique(
            CodeModel codemodel,
            AndroidGradleBuild build,
            String buildDirectory1,
            String buildDirectory2,
            String sourceDirectory) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String string = gson.toJson(codemodel);
        string = replaceWithSlashCombinations(string, buildDirectory1, "${buildDirectory}");
        string = replaceWithSlashCombinations(string, sourceDirectory, "${sourceDirectory}");
        File exampleMessages = new File("./example-messages");
        exampleMessages.mkdir();

        String hash = LiterateHash.of(string);
        com.google.common.io.Files.write(string, new File(exampleMessages, String.format("codemodel-%s.json",
                hash)), Charsets.UTF_8);

        string = gson.toJson(build);
        string = replaceWithSlashCombinations(string, buildDirectory2, "${buildDirectory}");
        string = replaceWithSlashCombinations(string, sourceDirectory, "${sourceDirectory}");
        com.google.common.io.Files.write(string,
                new File(exampleMessages, String.format("android-studio-%s.json",
                        hash)), Charsets.UTF_8);
        return codemodel;
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
        //noinspection ConstantConditions
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
