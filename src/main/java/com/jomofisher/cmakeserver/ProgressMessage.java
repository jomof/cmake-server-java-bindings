package com.jomofisher.cmakeserver;

/**
 * Example,
 * <p>
 * {
 * "cookie":"",
 * "inReplyTo":"configure",
 * "progressCurrent":33,
 * "progressMaximum":1000,
 * "progressMessage":"Configuring",
 * "progressMinimum":0,
 * "type":"progress"
 * }
 */
@SuppressWarnings("unused")
class ProgressMessage extends Message {
  public String cookie;
  public String inReplyTo;
  public int progressCurrent;
  public int progressMaximum;
  public String progressMessage;
  public int progressMinimum;
}
