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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * CMake functionality bound to a particular CMake install path.
 */
public class CMake {
    final private File cmakeInstallPath;
    final private Map<String, String> cmakeProcessEnvironment;

    public CMake(File cmakeInstallPath) {
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
     * Get the environment that CMake will be (or already was) started with. If the process hasn't been started yet
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
}
