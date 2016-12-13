package com.jomofisher.cmakeserver;

import static java.lang.System.getenv;

import java.io.File;

public class CMakeServer {
  public static void connect(File cmakeInstallPath) {
    for(String key :System.getenv().keySet()) {
      System.out.printf("%s = %s\n", key, System.getenv().get(key));
    }
    cmakeInstallPath = cmakeInstallPath.getAbsoluteFile();
    if (!cmakeInstallPath.isDirectory()) {
      throw new RuntimeException(
          String.format("Expected CMake install path %s to be a folder", cmakeInstallPath));
    }
  }
}
