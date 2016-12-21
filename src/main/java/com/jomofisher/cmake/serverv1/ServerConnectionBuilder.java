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

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ServerConnectionBuilder {
    final private File cmakeInstallPath;
    final private Map<String, String> cmakeProcessEnvironment;
    private ProgressReceiver progressReceiver = null;
    private MessageReceiver messageReceiver = null;
    private SignalReceiver signalReceiver = null;
    private DiagnosticReceiver diagnosticReceiver = null;
    private DeserializationMonitor deserializationMonitor = null;

    public ServerConnectionBuilder(File cmakeInstallPath, Map<String, String> cmakeProcessEnvironment) {
        this.cmakeInstallPath = cmakeInstallPath;
        this.cmakeProcessEnvironment = cmakeProcessEnvironment;
    }

    /**
     * Start the server and return a connection to it.
     *
     * @return A connection to the CMake server that can be used to interact with.
     * @throws IOException if there was a problem spawning the process.
     */
    public ServerConnection create() throws IOException {
        ServerConnection connection = new ServerConnection(this);
        connection.connect();
        return connection;
    }

    File getCmakeInstallPath() {
        return cmakeInstallPath;
    }

    DeserializationMonitor getDeserializationMonitor() {
        return deserializationMonitor;
    }

    public ServerConnectionBuilder setDeserializationMonitor(DeserializationMonitor deserializationMonitor) {
        this.deserializationMonitor = deserializationMonitor;
        return this;
    }

    ProgressReceiver getProgressReceiver() {
        return progressReceiver;
    }

    public ServerConnectionBuilder setProgressReceiver(ProgressReceiver progressReceiver) {
        this.progressReceiver = progressReceiver;
        return this;
    }

    SignalReceiver getSignalReceiver() {
        return signalReceiver;
    }

    public ServerConnectionBuilder setSignalReceiver(SignalReceiver signalReceiver) {
        this.signalReceiver = signalReceiver;
        return this;
    }

    MessageReceiver getMessageReceiver() {
return messageReceiver;
    }

    public ServerConnectionBuilder setMessageReceiver(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
        return this;
    }

    DiagnosticReceiver getDiagnosticReceiver() {
        return diagnosticReceiver;
    }

    public ServerConnectionBuilder setDiagnosticReceiver(DiagnosticReceiver diagnosticReceiver) {
        this.diagnosticReceiver = diagnosticReceiver;
        return this;
    }

    Map<String, String> environment() {
        return this.cmakeProcessEnvironment;
    }
}
