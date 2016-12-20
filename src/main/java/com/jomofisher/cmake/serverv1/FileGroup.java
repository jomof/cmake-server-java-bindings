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

public class FileGroup {
    final public String compileFlags;
    final public Boolean isGenerated;
    final public String language;
    final public String sources[];
    final public String defines[];
    final public IncludePath includePath[];

    private FileGroup() {
        this.compileFlags = null;
        this.isGenerated = null;
        this.language = null;
        this.sources = null;
        this.defines = null;
        this.includePath = null;
    }
}
