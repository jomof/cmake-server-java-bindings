package com.jomofisher.cmakeserver;

@SuppressWarnings({"WeakerAccess", "unused"})
public class HandshakeMessage extends Message {
  public String cookie;
  public ProtocolVersion protocolVersion;
  public String sourceDirectory;
  public String buildDirectory;
  public String generator;
  public HandshakeMessage() {
    this.type = "handshake";
  }

  public HandshakeMessage setProtocolVersion(ProtocolVersion protocolVersion) {
    this.protocolVersion = protocolVersion;
    return this;
  }

  public HandshakeMessage setCookie(String cookie) {
    this.cookie = cookie;
    return this;
  }

  public HandshakeMessage setSourceDirectory(String sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
    return this;
  }

  public HandshakeMessage setBuildDirectory(String buildDirectory) {
    this.buildDirectory = buildDirectory;
    return this;
  }

  public HandshakeMessage setGenerator(String generator) {
    this.generator = generator;
    return this;
  }
}
