package com.jomofisher.cmakeserver.modelv1;

/**
 * Created by jomof on 12/20/2016.
 */
public class FileGroup {
    public String compileFlags;
    public boolean isGenerated;
    public String language;
    public String sources[];
    public String defines[];
    public IncludePath includePath[];
}
