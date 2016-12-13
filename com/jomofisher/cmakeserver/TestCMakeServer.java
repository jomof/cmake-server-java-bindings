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
import static org.junit.Assert.assertEquals;

import java.io.File;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestCMakeServer {
  private File getCMakeInstallFolder() {
    if (System.getenv("BAZEL_CMAKE_INSTALL_FOLDER") == null) {
      throw new RuntimeException("You must pass BAZEL_CMAKE_INSTALL_FOLDER at bazel test command-line");
    }
    return new File(System.getenv("BAZEL_CMAKE_INSTALL_FOLDER"));
  }

  @Test
  public void testConnect() throws Exception {
    CMakeServer.connect(getCMakeInstallFolder());
  }
}
