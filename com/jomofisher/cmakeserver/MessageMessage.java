package com.jomofisher.cmakeserver;

/**
 * Example,
 *
 * {
 *    "cookie":"",
 *    "inReplyTo":"configure",
 *    "message":"CMake Error: CMake was unable to find a build program corresponding to \"Ninja\".  CMAKE_MAKE_PROGRAM is not set.  You probably need to select a different build tool.",
 *    "title":"Error",
 *    "type":"message"
 *  }
 */
public class MessageMessage extends Message {
  public String cookie;
  public String inReplyTo;
  public String message;
  public String title;
}