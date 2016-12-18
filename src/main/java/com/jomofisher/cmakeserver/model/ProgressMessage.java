package com.jomofisher.cmakeserver.model;

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
public class ProgressMessage extends BaseMessage {
    public String cookie;
    public String inReplyTo;
    public int progressCurrent;
    public int progressMaximum;
    public String progressMessage;
    public int progressMinimum;
}
