package com.jomofisher.cmake;

/**
 * A Cmake release version
 */
public class CmakeVersionX {
    final public String full;
    final public int major;
    final public int minor;
    final public int point;
    final public String tag;

    CmakeVersionX(String full, int major, int minor, int point, String tag) {
        this.full = full;
        this.major = major;
        this.minor = minor;
        this.point = point;
        this.tag = tag;
    }
}
