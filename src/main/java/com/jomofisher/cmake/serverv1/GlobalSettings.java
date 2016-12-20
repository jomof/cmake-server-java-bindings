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

public class GlobalSettings {
    final public String type;
    final public String cookie;
    final public String inReplyTo;
    final public String buildDirectory;
    final public Boolean checkSystemVars;
    final public Boolean debugOutput;
    final public String extraGenerator;
    final public String generator;
    final public String sourceDirectory;
    final public Boolean trace;
    final public Boolean traceExpand;
    final public Boolean warnUninitialized;
    final public Boolean warnUnused;
    final public Boolean warnUnusedCli;
    final public Capabilities capabilities;

    private GlobalSettings() {
        this.type = null;
        this.cookie = null;
        this.inReplyTo = null;
        this.buildDirectory = null;
        this.checkSystemVars = null;
        this.debugOutput = null;
        this.extraGenerator = null;
        this.sourceDirectory = null;
        this.generator = null;
        this.trace = null;
        this.traceExpand = null;
        this.warnUninitialized = null;
        this.warnUnused = null;
        this.warnUnusedCli = null;
        this.capabilities = null;
    }
}
