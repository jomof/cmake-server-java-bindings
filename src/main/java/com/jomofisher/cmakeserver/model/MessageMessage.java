package com.jomofisher.cmakeserver.model;

/**
 * Example,
 * <p>
 * {
 * "cookie":"",
 * "inReplyTo":"configure",
 * "message":"CMake Error: CMake was unable to find a build program corresponding to \"Ninja\".  CMAKE_MAKE_PROGRAM is not set.  You probably need to select a different build tool.",
 * "title":"Error",
 * "type":"message"
 * }
 */
@SuppressWarnings("unused")
public class MessageMessage extends BaseMessage {
    public String cookie;
    public String inReplyTo;
    public String message;
    public String title;
}