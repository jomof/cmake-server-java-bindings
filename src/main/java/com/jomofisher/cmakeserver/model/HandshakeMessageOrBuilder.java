// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: messages.1.0.proto

package com.jomofisher.cmakeserver.model;

public interface HandshakeMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:HandshakeMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional string type = 1;</code>
   */
  java.lang.String getType();
  /**
   * <code>optional string type = 1;</code>
   */
  com.google.protobuf.ByteString
      getTypeBytes();

  /**
   * <code>optional string cookie = 2;</code>
   */
  java.lang.String getCookie();
  /**
   * <code>optional string cookie = 2;</code>
   */
  com.google.protobuf.ByteString
      getCookieBytes();

  /**
   * <code>optional .ProtocolVersion protocolVersion = 3;</code>
   */
  boolean hasProtocolVersion();
  /**
   * <code>optional .ProtocolVersion protocolVersion = 3;</code>
   */
  com.jomofisher.cmakeserver.model.ProtocolVersion getProtocolVersion();
  /**
   * <code>optional .ProtocolVersion protocolVersion = 3;</code>
   */
  com.jomofisher.cmakeserver.model.ProtocolVersionOrBuilder getProtocolVersionOrBuilder();

  /**
   * <code>optional string sourceDirectory = 4;</code>
   */
  java.lang.String getSourceDirectory();
  /**
   * <code>optional string sourceDirectory = 4;</code>
   */
  com.google.protobuf.ByteString
      getSourceDirectoryBytes();

  /**
   * <code>optional string buildDirectory = 5;</code>
   */
  java.lang.String getBuildDirectory();
  /**
   * <code>optional string buildDirectory = 5;</code>
   */
  com.google.protobuf.ByteString
      getBuildDirectoryBytes();

  /**
   * <code>optional string generator = 6;</code>
   */
  java.lang.String getGenerator();
  /**
   * <code>optional string generator = 6;</code>
   */
  com.google.protobuf.ByteString
      getGeneratorBytes();
}
