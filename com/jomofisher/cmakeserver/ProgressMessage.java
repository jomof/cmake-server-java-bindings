package com.jomofisher.cmakeserver;

/**
 * Example,
 *
 * {
 *  "cookie":"",
 *  "inReplyTo":"configure",
 *  "progressCurrent":33,
 *  "progressMaximum":1000,
 *  "progressMessage":"Configuring",
 *  "progressMinimum":0,
 *  "type":"progress"
 }
 *
 */
public class ProgressMessage extends Message {
  public String cookie;
  public String inReplyTo;
  public int progressCurrent;
  public int progressMaximum;
  public String progressMessage;
  public int progressMinimum;
}
