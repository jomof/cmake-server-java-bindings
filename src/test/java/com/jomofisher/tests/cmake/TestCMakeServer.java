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
import com.jomofisher.cmake.serverv1.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Locale;
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

    private static int modAppend(String[] keys, StringBuilder hash, int remainingHash) {
        int keySize = keys.length;
        hash.append(keys[remainingHash % keySize]);
        return remainingHash / keySize;
    }

    private static String literateHash(Object object) {
        String adjective[] = new String[]{
                "Deteriorating",
                "Walking", "Running", "Sprinting", "Dashing", "Crashing", "Spirited", "Spiritual", "Ancient",
                "Melting", "Boiling", "Evaporating", "Freezing", "Sublimating", "Triple", "Double", "Quadruple",
                "Quintuple", "Octo", "Former", "Latter", "Formal", "Decorated", "Hypnotic", "Drunken", "Realizing",
                "Gaseous", "Grassy", "Sandy", "Rocky", "Gigantic", "Hilarious", "Hard", "Soft", "Curious", "Rigid",
                "Adorable", "Clean", "Elegant", "Turquoise", "Blue", "Green", "Famous", "Gifted", "Thankful",
                "Brave", "Calm", "Mysterious", "Round", "Square", "Massive", "Faint", "Melodic", "Tall", "Ancient",
                "Brief", "Swift", "Whispering", "Salty", "Weak", "Inspirational", "Cellar", "Better", "Best", "Early",
                "Important", "Wild", "Swamp", "Arboreal", "Jungle", "Molten", "Frozen", "Aromatic", "Zippy", "Zesty",
                "Comfortable", "Cozy", "Abstract", "Surreal", "Impressionist", "Cubist", "Fauvist", "Dadaist", "Pop",
                "Nouveau", "Aesthetic", "Real", "Concrete", "Conceptual", "Deconstructed", "Digital", "Fantastic",
                "Figurative", "Folk", "Future", "Geometric", "Gothic", "Typographic", "Lyrical", "Magical", "Maximum",
                "Minimum", "Modern", "Naive", "Primitive", "Naive", "Objective", "Precision", "Psychedelic",
                "Regional", "Romantic", "Rococo", "Resonating", "Space", "Symbolic", "Street", "Supreme",
                "Penultimate", "Ultimate", "Underground", "Baroque"
        };
        String noun[] = new String[]{
                "Dog", "Cat", "Mountain", "Ocean", "Submarine", "Salamander", "Tree", "Forest", "Rock", "Earth",
                "Novel", "Hunter", "Teacher", "Fire", "Tower", "Lamp", "Flame", "Theory", "Love", "People", "History",
                "World", "Map", "Family", "Door", "Window", "Music", "Bird", "Fact", "Area", "Language", "Rhythm",
                "Bird", "Worm", "Time", "Year", "Hand", "Night", "Day", "Story", "Chemistry", "Painting", "Cigarette",
                "Scene", "Mood", "Expression", "Foundation", "Grandfather", "Hope", "Selection", "Wine", "Passion",
                "Happiness", "Republic", "Engine", "Hotel", "Motorcycle", "Leader", "Cousin", "Flute", "Piano",
                "Beetle", "Spider", "Lion", "Snake", "Fish", "Shark", "Whale", "Ship", "Capsule", "Travel", "Train",
                "Range", "Locus", "Pentagon", "Polygon", "Sphere", "Pyramid", "Tide", "Wheel", "Tire", "Engine",
                "Seat", "Rose", "Tulip", "Azalea", "Chest", "Blouse", "Tiger", "Cube", "Scorpion", "Fox", "Sandwich",
                "Taco", "Poem", "Novel", "Candle", "Sketch", "Painting", "Vine", "Ceramic", "Bowl", "Plate", "Spoon",
                "Fork", "Hammock", "Cable", "Wire", "Diamond", "Ruby", "Emerald", "Comet", "Planet", "Star", "Moon",
                "Rocket", "Sled", "Table", "Couch", "Bed", "Room", "Basket", "Box", "Envelope", "Robe", "Silk",
                "Terrier", "Shepherd", "Sheep", "Cow", "Corgi", "Hill", "Lake", "Pond", "Mountain", "Plateau",
                "Grocer", "Horse", "Bicycle", "Helmet", "Turtle", "Moose", "Mine", "Cave", "Core", "Snow", "Angel",
                "Cactus", "Elephant", "Hippo", "Zippo", "Cigar", "Eagle", "Hawk", "Raptor", "Dinosaur", "Vegetable",
                "Carrot", "Celery", "Gerbil", "Rabbit", "Pig", "Mouse", "Paintbrush", "Easel", "Shoe", "Sock",
                "Wall", "Roof", "Drawer", "Straw", "Wick", "Gasoline", "Kerosene", "Blanket", "Pillow", "Musician",
                "Artist", "Poet", "Chiclet", "Physics", "Chemistry", "Anatomy", "Astrobiology", "Biochemistry",
                "Biogeography", "Biophysics", "Neuroscience", "Biotechnology", "Botany", "Cryobiology",
                "Ecology", "Ethnobiology", "Gerontology", "Immunology", "Limnology", "Microbiology", "Neuroscience",
                "Paleontology", "Parasitology", "Physiology", "Radiobiology", "Sociobiology", "Toxicology", "Zoology",
                "Carnation", "Lily", "Thistle", "Orchid", "Sunflower", "Snapdragon", "Lavender", "Holly", "Peony",
                "Marigold", "Lilac", "Ginger", "Aster", "Bloom", "Bell", "Corn", "Wheat", "Hound", "Akita", "Malamute",
                "Spaniel", "Azawakh", "Barbet", "Basenji", "Collie", "Malinois", "Tervuren", "Picard", "Frise",
                "Bolognese", "Boxer", "Briard", "Griffon", "Chihuahua", "Dachshund", "Dalmatian", "Pinscher",
                "Setter", "Pointer", "Pyrenees", "Harrier", "Havanese", "Keeshond", "Kuvasz", "Labradoodle",
                "Mutt", "Otterhound", "Papillon", "Pug", "Puli", "Ridgeback", "Rottweiler", "Whippet", "Yorkipoo"
        };
        String verb[] = new String[]{
                "Runs", "Walks", "Speaks", "Waits", "Learns", "Opens", "Closes", "Calls", "Asks", "Becomes", "Helps",
                "Plays", "Moves", "Lives", "Writes", "Stands", "Meets", "Continues", "Changes", "Creates", "Speaks",
                "Grows", "Remembers", "Sends", "Builds", "Reaches", "Raises", "Hopes", "Supports", "Catches",
                "Delights", "Entrances", "Improves", "Captures", "Befriends", "Elevates", "Erupts", "Raises", "Lowers",
                "Extends", "Heightens", "Keeps", "Organizes", "Predicts", "Embiggens", "Waltzes", "Tunes"
        };
        StringBuilder hash = new StringBuilder();
        int remainingHash = object.hashCode();
        if (remainingHash < 0) {
            remainingHash *= -1;
            remainingHash = modAppend(noun, hash, remainingHash);
            remainingHash = modAppend(verb, hash, remainingHash);
        }
        while (remainingHash > 1) {
            remainingHash = modAppend(adjective, hash, remainingHash);
            if (remainingHash > 0) {
                remainingHash = modAppend(noun, hash, remainingHash);
            }
            if (remainingHash > 0) {
                remainingHash = modAppend(verb, hash, remainingHash);
            }
        }
        return hash.toString();
    }

    private File getTemporaryBuildOutputFolder() throws IOException {
        Path basedir = FileSystems.getDefault().getPath("test-output").toAbsolutePath();
        basedir.toFile().mkdirs();
        return new File(basedir.toFile(), String.format("build-output-%s",
                literateHash(new Random().nextLong())));
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
        HandshakeRequest handshakeRequest = getHelloWorldHandshake();
        HandshakeResult handshakeResult = connection.handshake(handshakeRequest);
        connection.configure();
        ComputeResult computeResult = connection.compute();
        CodeModel codemodelReply = connection.codemodel();
        recordUnique(codemodelReply, handshakeRequest.buildDirectory);
    }

    @Test
    public void testAndroidCodeModel() throws Exception {
        ServerConnection connection = getConnectionBuilder(getCMakeInstallFolder()).create();
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

        recordUnique(codemodelReply, handshakeRequest.buildDirectory);
    }

    private CodeModel recordUnique(CodeModel codemodel, String buildDirectory) throws IOException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String string = gson.toJson(codemodel);
        string = string.replace(buildDirectory, "${buildDirectory}");
        File exampleMessages = new File("./example-messages");
        exampleMessages.mkdir();
        File outFile = new File(exampleMessages, String.format("codemodel-%s.json", literateHash(string)));
        com.google.common.io.Files.write(string, outFile, Charsets.UTF_8);
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
