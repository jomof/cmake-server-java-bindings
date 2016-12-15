/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jomofisher.cmakeserver;
import static com.jomofisher.cmakeserver.TestCMakeServer.OSType.Linux;
import static com.jomofisher.cmakeserver.TestCMakeServer.OSType.MacOS;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestCMakeServer {
    enum OSType {
      Windows, MacOS, Linux, Other
    };

    static OSType detectedOS;

    static {
      if (detectedOS == null) {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
          detectedOS = OSType.MacOS;
        } else if (OS.indexOf("win") >= 0) {
          detectedOS = OSType.Windows;
        } else if (OS.indexOf("nux") >= 0) {
          detectedOS = OSType.Linux;
        } else {
          detectedOS = OSType.Other;
        }
      }
    }

  private File getCMakeInstallFolder() {
    if (System.getenv("BAZEL_CMAKE_WORKSPACE_FOLDER") == null) {
      throw new RuntimeException("You must pass BAZEL_CMAKE_WORKSPACE_FOLDER at bazel test command-line");
    }

    File workspaceFolder = new File(System.getenv("BAZEL_CMAKE_WORKSPACE_FOLDER"));

    switch (detectedOS) {
      case Linux: return new File(workspaceFolder, "prebuilts/cmake-3.7.1-Linux-x86_64");
      case MacOS: return new File(workspaceFolder, "prebuilts/cmake-3.7.1-Darwin-x86_64/CMake.app/Contents");
      default:
        throw new RuntimeException("OS not yet supported: "+detectedOS);
    }
  }

  @Test
  public void testConnect() throws Exception {
    CMakeServerConnection connection = CMakeServer.connect(getCMakeInstallFolder());
  }

//  @Test
//  public void testHandshake() throws Exception {
//    CMakeServerConnection connection = CMakeServer.connect(getCMakeInstallFolder());
//    connection.handshake("my-cookie",
//        new File("/usr/local/google/home/jomof/projects/cmake-server-java-bindings/test-data/cmake-projects/hello-world"),
//        new File("."),
//        "Ninja");
//  }
}
