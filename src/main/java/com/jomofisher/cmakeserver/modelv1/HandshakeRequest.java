package com.jomofisher.cmakeserver.modelv1;

public class HandshakeRequest {
    final public String type;
    public String cookie;
    public ProtocolVersion protocolVersion;
    public String sourceDirectory;
    public String buildDirectory;
    public String generator;
    public HandshakeRequest() {
        type = "handshake";
    }
}
