package com.jomofisher.cmakeserver;

import java.util.List;

/**
 * Created by jomof on 12/15/16.
 */
public class GlobalSettingsReplyMessage extends Message {
  public String cookie;
  public String inReplyTo;
  public String buildDirectory;
  public boolean checkSystemVars;
  public boolean debugOutput;
  public String extraGenerator;
  public String generator;
  public String sourceDirectory;
  public boolean trace;
  public boolean traceExpand;
  public boolean warnUninitialized;
  public boolean warnUnused;
  public boolean warnUnusedCli;
  public Capabilities capabilities;
}
