// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: messages.1.0.proto

package com.jomofisher.cmakeserver.model;

public interface FileGroupOrBuilder extends
    // @@protoc_insertion_point(interface_extends:FileGroup)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional string compileFlags = 1;</code>
   */
  java.lang.String getCompileFlags();
  /**
   * <code>optional string compileFlags = 1;</code>
   */
  com.google.protobuf.ByteString
      getCompileFlagsBytes();

  /**
   * <code>optional bool isGenerated = 2;</code>
   */
  boolean getIsGenerated();

  /**
   * <code>optional string language = 3;</code>
   */
  java.lang.String getLanguage();
  /**
   * <code>optional string language = 3;</code>
   */
  com.google.protobuf.ByteString
      getLanguageBytes();

  /**
   * <code>repeated string sources = 4;</code>
   */
  java.util.List<java.lang.String>
      getSourcesList();
  /**
   * <code>repeated string sources = 4;</code>
   */
  int getSourcesCount();
  /**
   * <code>repeated string sources = 4;</code>
   */
  java.lang.String getSources(int index);
  /**
   * <code>repeated string sources = 4;</code>
   */
  com.google.protobuf.ByteString
      getSourcesBytes(int index);

  /**
   * <code>repeated string defines = 5;</code>
   */
  java.util.List<java.lang.String>
      getDefinesList();
  /**
   * <code>repeated string defines = 5;</code>
   */
  int getDefinesCount();
  /**
   * <code>repeated string defines = 5;</code>
   */
  java.lang.String getDefines(int index);
  /**
   * <code>repeated string defines = 5;</code>
   */
  com.google.protobuf.ByteString
      getDefinesBytes(int index);

  /**
   * <code>repeated .IncludePath includePath = 6;</code>
   */
  java.util.List<com.jomofisher.cmakeserver.model.IncludePath> 
      getIncludePathList();
  /**
   * <code>repeated .IncludePath includePath = 6;</code>
   */
  com.jomofisher.cmakeserver.model.IncludePath getIncludePath(int index);
  /**
   * <code>repeated .IncludePath includePath = 6;</code>
   */
  int getIncludePathCount();
  /**
   * <code>repeated .IncludePath includePath = 6;</code>
   */
  java.util.List<? extends com.jomofisher.cmakeserver.model.IncludePathOrBuilder> 
      getIncludePathOrBuilderList();
  /**
   * <code>repeated .IncludePath includePath = 6;</code>
   */
  com.jomofisher.cmakeserver.model.IncludePathOrBuilder getIncludePathOrBuilder(
      int index);
}