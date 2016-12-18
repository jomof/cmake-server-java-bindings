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
package com.jomofisher.cmakeserver.model;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HandshakeMessage extends BaseMessage {
    public String cookie;
    public ProtocolVersion protocolVersion;
    public String sourceDirectory;
    public String buildDirectory;
    public String generator;

    public HandshakeMessage() {
        this.type = "handshake";
    }

    @NotNull
    public HandshakeMessage setProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
        return this;
    }

    @NotNull
    public HandshakeMessage setCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    @NotNull
    public HandshakeMessage setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    @NotNull
    public HandshakeMessage setBuildDirectory(String buildDirectory) {
        this.buildDirectory = buildDirectory;
        return this;
    }

    @NotNull
    public HandshakeMessage setGenerator(String generator) {
        this.generator = generator;
        return this;
    }
}
