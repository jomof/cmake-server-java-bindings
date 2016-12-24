package com.jomofisher.tests.cmake.model;

import java.util.Map;

/**
 * Created by jomof on 12/24/2016.
 */
public class AndroidGradleBuild {
    public String buildFiles[];
    public String cleanCommands[];
    public String cppFileExtensions[];
    public Map<String, Library> libraries;
    public Map<String, Toolchain> toolchains;
}
