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
package com.jomofisher.cmake;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jomofisher.cmake.database.Compilation;
import com.jomofisher.cmake.serverv1.ServerConnectionBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Map;

/**
 * Cmake functionality bound to a particular Cmake install path.
 */
public class CmakeX {
    final private static String CMAKE_VERSION_LINE_PREFIX = "cmake version ";
    final private File cmakeInstallPath;
    final private Map<String, String> cmakeProcessEnvironment;

    public CmakeX(File cmakeInstallPath) {
        this.cmakeInstallPath = cmakeInstallPath;
        this.cmakeProcessEnvironment = new ProcessBuilder().environment();
    }

    /**
     * Create a server connection builder for V1 protocol.
     */
    public ServerConnectionBuilder newServerBuilderV1() {
        return new ServerConnectionBuilder(cmakeInstallPath, this.cmakeProcessEnvironment);
    }

    /**
     * Get a builder for a new server connection. Will get the "latest" protocol version that this library supports.
     * This may break existing code and force a recompile. For an immutable version choose, for example,
     * newServerBuilderV1.
     */
    public ServerConnectionBuilder newServerBuilder() {
        return newServerBuilderV1();
    }

    /**
     * Get the environment that Cmake will be (or already was) started with. If the process hasn't been started yet
     * the changes here will end up in the environment of the spawned process.
     */
    public Map<String, String> environment() {
        return this.cmakeProcessEnvironment;
    }

    /**
     * Get the compilation database if one was created. Compilation database is created when server connection is
     * configured with -DCMAKE_EXPORT_COMPILE_COMMANDS=1. The file is created during compute phase.
     */
    public Compilation[] getCompilationDatabase(File buildDirectory) throws IOException {
        File compileCommandsFile = new File(buildDirectory, "compile_commands.json");
        if (!compileCommandsFile.isFile()) {
            throw new RuntimeException(String.format("File %s not found", compileCommandsFile));
        }
        String text = new String(Files.readAllBytes(compileCommandsFile.toPath()));
        Gson gson = new GsonBuilder()
                .create();
        return gson.fromJson(text, Compilation[].class);
    }

    /**
     * Get the current Cmake version as a string like "3.6.0-rc2"
     */
    public String getVersionString() throws IOException {
        File cmakeExecutable = new File(cmakeInstallPath, "cmake");
        ProcessBuilder processBuilder = new ProcessBuilder(cmakeExecutable.getAbsolutePath(), "--version");
        processBuilder.redirectErrorStream();
        Process process = processBuilder.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = in.readLine();
        if (!line.startsWith(CMAKE_VERSION_LINE_PREFIX)) {
            throw new RuntimeException("Did not recognize stdout line as a Cmake version: " + line);
        }
        return line.substring(CMAKE_VERSION_LINE_PREFIX.length());
    }

    /**
     * Get the current Cmake version as a structure
     */
    public CmakeVersionX getVersion() throws IOException {
        String string = getVersionString();
        String[] parts = string.split("\\.");
        if (parts[2].contains("-")) {
            // There is a tag, as in 3.6.0-rc2
            String[] subparts = parts[2].split("-");
            return new CmakeVersionX(
                    string,
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(subparts[0]),
                    subparts[1]);
        }

        // There's no tag
        return new CmakeVersionX(
                string,
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                "");
    }
}
