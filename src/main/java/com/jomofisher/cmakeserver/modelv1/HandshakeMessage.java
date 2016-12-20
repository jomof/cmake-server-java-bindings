package com.jomofisher.cmakeserver.modelv1;

public class HandshakeMessage {
    public String type;
    public String cookie;
    public ProtocolVersion protocolVersion;
    public String sourceDirectory;
    public String buildDirectory;
    public String generator;
    public HandshakeMessage() {
        type = "handshake";
    }
}
