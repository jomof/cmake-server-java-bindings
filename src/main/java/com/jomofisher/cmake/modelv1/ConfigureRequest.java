package com.jomofisher.cmake.modelv1;

public class ConfigureRequest {
    final public String type;
    public String cacheArguments[];
    public ConfigureRequest() {
        type = "configure";
    }
}
