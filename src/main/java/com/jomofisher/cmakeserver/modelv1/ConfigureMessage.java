package com.jomofisher.cmakeserver.modelv1;

/**
 * Created by jomof on 12/20/2016.
 */
public class ConfigureMessage {
    public String type;
    public String cacheArguments[];
    public ConfigureMessage() {
        type = "configure";
    }
}
