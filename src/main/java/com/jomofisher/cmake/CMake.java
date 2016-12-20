package com.jomofisher.cmake;

import com.jomofisher.cmake.modelv1.ServerConnectionBuilder;

import java.io.File;
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

    public ServerConnectionBuilder newServerBuilderV1() {
        return new ServerConnectionBuilder(cmakeInstallPath, this.cmakeProcessEnvironment);
    }

    public ServerConnectionBuilder newServerBuilder() {
        return newServerBuilderV1();
    }

    public Map<String, String> environment() {
        return this.cmakeProcessEnvironment;
    }
}
