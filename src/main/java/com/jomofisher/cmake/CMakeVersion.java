package com.jomofisher.cmake;

/**
 * A CMake release version
 */
public class CMakeVersion {
    final public int major;
    final public int minor;
    final public int point;
    final public String tag;

    CMakeVersion(int major, int minor, int point, String tag) {
        this.major = major;
        this.minor = minor;
        this.point = point;
        this.tag = tag;
    }
}
