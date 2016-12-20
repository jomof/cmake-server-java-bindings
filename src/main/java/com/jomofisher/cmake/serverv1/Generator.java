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

public class Generator {
    final public String name;
    final public Boolean platformSupport;
    final public Boolean toolsetSupport;
    final public String extraGenerators[];

    private Generator() {
        this.name = null;
        this.platformSupport = null;
        this.toolsetSupport = null;
        this.extraGenerators = null;
    }
}
