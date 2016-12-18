package com.jomofisher.cmakeserver.model;

@SuppressWarnings("unused")
public class HandshakeReplyMessage extends BaseMessage {
    public String cookie;
    public String inReplyTo;
}
