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
