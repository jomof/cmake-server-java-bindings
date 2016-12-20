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

public class Target {
    final public String artifacts[];
    final public String buildDirectory;
    final public FileGroup fileGroups[];
    final public String fullName;
    final public String linkLibraries;
    final public String linkerLanguage;
    final public String name;
    final public String sourceDirectory;
    final public String type;
    final public String linkFlags;
    final public String sysroot;

    private Target() {
        this.artifacts = null;
        this.buildDirectory = null;
        this.fileGroups = null;
        this.fullName = null;
        this.linkLibraries = null;
        this.linkerLanguage = null;
        this.name = null;
        this.type = null;
        this.sourceDirectory = null;
        this.linkFlags = null;
        this.sysroot = null;
    }
}
