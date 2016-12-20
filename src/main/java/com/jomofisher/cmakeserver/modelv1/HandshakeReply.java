package com.jomofisher.cmakeserver.modelv1;

/**
 * Created by jomof on 12/20/2016.
 */
public class HandshakeReply {
    public String type;
    public String cookie;
    public String inReplyTo;
    HandshakeReply() {
        type = "handshake";
    }
}
