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

@SuppressWarnings({"WeakerAccess", "unused"})
public class HandshakeMessage extends BaseMessage {
    public String cookie;
    public ProtocolVersion protocolVersion;
    public String sourceDirectory;
    public String buildDirectory;
    public String generator;

    public HandshakeMessage() {
        super("handshake");
    }
    
    public HandshakeMessage setProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
        return this;
    }
    
    public HandshakeMessage setCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public HandshakeMessage setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    public HandshakeMessage setBuildDirectory(String buildDirectory) {
        this.buildDirectory = buildDirectory;
        return this;
    }

    public HandshakeMessage setGenerator(String generator) {
        this.generator = generator;
        return this;
    }
}
