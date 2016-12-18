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
package com.jomofisher.cmakeserver;

import com.jomofisher.cmakeserver.model.HelloMessage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CMakeServerConnectionBuilder {
    final private File cmakeInstallPath;
    final private Map<String, String> cmakeProcessEnvironment;
    private boolean allowExtraMessageFields = true;
    private ProgressReceiver progressReceiver = null;
    private DiagnosticReceiver diagnosticReceiver = null;

    public CMakeServerConnectionBuilder(File cmakeInstallPath) {
        this.cmakeInstallPath = cmakeInstallPath;
        this.cmakeProcessEnvironment = new ProcessBuilder().environment();
    }

    /**
     * Start the server and return a connection to interact with it.
     *
     * @return A connection to the CMake server that can be used to interact with.
     * @throws IOException if there was a problem spawning the process.
     */
    public CMakeServerConnection create() throws IOException {
        CMakeServerConnection connection = new CMakeServerConnection(this);
        HelloMessage reply = connection.connect();
        return connection;
    }

    public File getCmakeInstallPath() {
        return cmakeInstallPath;
    }

    public boolean getAllowExtraMessageFields() {
        return allowExtraMessageFields;
    }

    public CMakeServerConnectionBuilder setAllowExtraMessageFields(boolean allowExtraMessageFields) {
        this.allowExtraMessageFields = allowExtraMessageFields;
        return this;
    }

    public ProgressReceiver getProgressReceiver() {
        return progressReceiver;
    }

    public CMakeServerConnectionBuilder setProgressReceiver(ProgressReceiver progressReceiver) {
        this.progressReceiver = progressReceiver;
        return this;
    }

    public DiagnosticReceiver getDiagnosticReceiver() {
        return diagnosticReceiver;
    }

    public CMakeServerConnectionBuilder setDiagnosticReceiver(DiagnosticReceiver diagnosticReceiver) {
        this.diagnosticReceiver = diagnosticReceiver;
        return this;
    }

    public Map<String, String> environment() {
        return this.cmakeProcessEnvironment;
    }
}
