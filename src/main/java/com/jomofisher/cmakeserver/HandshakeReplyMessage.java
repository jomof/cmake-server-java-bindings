package com.jomofisher.cmakeserver;

@SuppressWarnings("unused")
public class HandshakeReplyMessage extends Message {
  public String cookie;
  public String inReplyTo;
}
