// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: messages.1.0.proto

package com.jomofisher.cmakeserver.model;

public interface VersionOrBuilder extends
    // @@protoc_insertion_point(interface_extends:Version)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional bool isDirty = 1;</code>
   */
  boolean getIsDirty();

  /**
   * <code>optional int32 major = 2;</code>
   */
  int getMajor();

  /**
   * <code>optional int32 minor = 3;</code>
   */
  int getMinor();

  /**
   * <code>optional int32 patch = 4;</code>
   */
  int getPatch();

  /**
   * <code>optional string string = 5;</code>
   */
  java.lang.String getString();
  /**
   * <code>optional string string = 5;</code>
   */
  com.google.protobuf.ByteString
      getStringBytes();

  /**
   * <code>optional string suffix = 6;</code>
   */
  java.lang.String getSuffix();
  /**
   * <code>optional string suffix = 6;</code>
   */
  com.google.protobuf.ByteString
      getSuffixBytes();
}
