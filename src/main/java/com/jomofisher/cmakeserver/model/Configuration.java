// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: messages.1.0.proto

package com.jomofisher.cmakeserver.model;

/**
 * Protobuf type {@code Configuration}
 */
public  final class Configuration extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:Configuration)
    ConfigurationOrBuilder {
  // Use Configuration.newBuilder() to construct.
  private Configuration(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Configuration() {
    name_ = "";
    projects_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private Configuration(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            name_ = s;
            break;
          }
          case 18: {
            if (!((mutable_bitField0_ & 0x00000002) == 0x00000002)) {
              projects_ = new java.util.ArrayList<com.jomofisher.cmakeserver.model.Project>();
              mutable_bitField0_ |= 0x00000002;
            }
            projects_.add(
                input.readMessage(com.jomofisher.cmakeserver.model.Project.parser(), extensionRegistry));
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      if (((mutable_bitField0_ & 0x00000002) == 0x00000002)) {
        projects_ = java.util.Collections.unmodifiableList(projects_);
      }
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.jomofisher.cmakeserver.model.Messages10.internal_static_Configuration_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.jomofisher.cmakeserver.model.Messages10.internal_static_Configuration_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.jomofisher.cmakeserver.model.Configuration.class, com.jomofisher.cmakeserver.model.Configuration.Builder.class);
  }

  private int bitField0_;
  public static final int NAME_FIELD_NUMBER = 1;
  private volatile java.lang.Object name_;
  /**
   * <code>optional string name = 1;</code>
   */
  public java.lang.String getName() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      name_ = s;
      return s;
    }
  }
  /**
   * <code>optional string name = 1;</code>
   */
  public com.google.protobuf.ByteString
      getNameBytes() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      name_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int PROJECTS_FIELD_NUMBER = 2;
  private java.util.List<com.jomofisher.cmakeserver.model.Project> projects_;
  /**
   * <code>repeated .Project projects = 2;</code>
   */
  public java.util.List<com.jomofisher.cmakeserver.model.Project> getProjectsList() {
    return projects_;
  }
  /**
   * <code>repeated .Project projects = 2;</code>
   */
  public java.util.List<? extends com.jomofisher.cmakeserver.model.ProjectOrBuilder> 
      getProjectsOrBuilderList() {
    return projects_;
  }
  /**
   * <code>repeated .Project projects = 2;</code>
   */
  public int getProjectsCount() {
    return projects_.size();
  }
  /**
   * <code>repeated .Project projects = 2;</code>
   */
  public com.jomofisher.cmakeserver.model.Project getProjects(int index) {
    return projects_.get(index);
  }
  /**
   * <code>repeated .Project projects = 2;</code>
   */
  public com.jomofisher.cmakeserver.model.ProjectOrBuilder getProjectsOrBuilder(
      int index) {
    return projects_.get(index);
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, name_);
    }
    for (int i = 0; i < projects_.size(); i++) {
      output.writeMessage(2, projects_.get(i));
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, name_);
    }
    for (int i = 0; i < projects_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, projects_.get(i));
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.jomofisher.cmakeserver.model.Configuration)) {
      return super.equals(obj);
    }
    com.jomofisher.cmakeserver.model.Configuration other = (com.jomofisher.cmakeserver.model.Configuration) obj;

    boolean result = true;
    result = result && getName()
        .equals(other.getName());
    result = result && getProjectsList()
        .equals(other.getProjectsList());
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptorForType().hashCode();
    hash = (37 * hash) + NAME_FIELD_NUMBER;
    hash = (53 * hash) + getName().hashCode();
    if (getProjectsCount() > 0) {
      hash = (37 * hash) + PROJECTS_FIELD_NUMBER;
      hash = (53 * hash) + getProjectsList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.jomofisher.cmakeserver.model.Configuration parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.jomofisher.cmakeserver.model.Configuration parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.jomofisher.cmakeserver.model.Configuration parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.jomofisher.cmakeserver.model.Configuration parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.jomofisher.cmakeserver.model.Configuration parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.jomofisher.cmakeserver.model.Configuration parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.jomofisher.cmakeserver.model.Configuration parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.jomofisher.cmakeserver.model.Configuration parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.jomofisher.cmakeserver.model.Configuration parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.jomofisher.cmakeserver.model.Configuration parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.jomofisher.cmakeserver.model.Configuration prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code Configuration}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:Configuration)
      com.jomofisher.cmakeserver.model.ConfigurationOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.jomofisher.cmakeserver.model.Messages10.internal_static_Configuration_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.jomofisher.cmakeserver.model.Messages10.internal_static_Configuration_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.jomofisher.cmakeserver.model.Configuration.class, com.jomofisher.cmakeserver.model.Configuration.Builder.class);
    }

    // Construct using com.jomofisher.cmakeserver.model.Configuration.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
        getProjectsFieldBuilder();
      }
    }
    public Builder clear() {
      super.clear();
      name_ = "";

      if (projectsBuilder_ == null) {
        projects_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000002);
      } else {
        projectsBuilder_.clear();
      }
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.jomofisher.cmakeserver.model.Messages10.internal_static_Configuration_descriptor;
    }

    public com.jomofisher.cmakeserver.model.Configuration getDefaultInstanceForType() {
      return com.jomofisher.cmakeserver.model.Configuration.getDefaultInstance();
    }

    public com.jomofisher.cmakeserver.model.Configuration build() {
      com.jomofisher.cmakeserver.model.Configuration result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.jomofisher.cmakeserver.model.Configuration buildPartial() {
      com.jomofisher.cmakeserver.model.Configuration result = new com.jomofisher.cmakeserver.model.Configuration(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      result.name_ = name_;
      if (projectsBuilder_ == null) {
        if (((bitField0_ & 0x00000002) == 0x00000002)) {
          projects_ = java.util.Collections.unmodifiableList(projects_);
          bitField0_ = (bitField0_ & ~0x00000002);
        }
        result.projects_ = projects_;
      } else {
        result.projects_ = projectsBuilder_.build();
      }
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.jomofisher.cmakeserver.model.Configuration) {
        return mergeFrom((com.jomofisher.cmakeserver.model.Configuration)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.jomofisher.cmakeserver.model.Configuration other) {
      if (other == com.jomofisher.cmakeserver.model.Configuration.getDefaultInstance()) return this;
      if (!other.getName().isEmpty()) {
        name_ = other.name_;
        onChanged();
      }
      if (projectsBuilder_ == null) {
        if (!other.projects_.isEmpty()) {
          if (projects_.isEmpty()) {
            projects_ = other.projects_;
            bitField0_ = (bitField0_ & ~0x00000002);
          } else {
            ensureProjectsIsMutable();
            projects_.addAll(other.projects_);
          }
          onChanged();
        }
      } else {
        if (!other.projects_.isEmpty()) {
          if (projectsBuilder_.isEmpty()) {
            projectsBuilder_.dispose();
            projectsBuilder_ = null;
            projects_ = other.projects_;
            bitField0_ = (bitField0_ & ~0x00000002);
            projectsBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getProjectsFieldBuilder() : null;
          } else {
            projectsBuilder_.addAllMessages(other.projects_);
          }
        }
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.jomofisher.cmakeserver.model.Configuration parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.jomofisher.cmakeserver.model.Configuration) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.lang.Object name_ = "";
    /**
     * <code>optional string name = 1;</code>
     */
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        name_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string name = 1;</code>
     */
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string name = 1;</code>
     */
    public Builder setName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      name_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string name = 1;</code>
     */
    public Builder clearName() {
      
      name_ = getDefaultInstance().getName();
      onChanged();
      return this;
    }
    /**
     * <code>optional string name = 1;</code>
     */
    public Builder setNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      name_ = value;
      onChanged();
      return this;
    }

    private java.util.List<com.jomofisher.cmakeserver.model.Project> projects_ =
      java.util.Collections.emptyList();
    private void ensureProjectsIsMutable() {
      if (!((bitField0_ & 0x00000002) == 0x00000002)) {
        projects_ = new java.util.ArrayList<com.jomofisher.cmakeserver.model.Project>(projects_);
        bitField0_ |= 0x00000002;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.jomofisher.cmakeserver.model.Project, com.jomofisher.cmakeserver.model.Project.Builder, com.jomofisher.cmakeserver.model.ProjectOrBuilder> projectsBuilder_;

    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public java.util.List<com.jomofisher.cmakeserver.model.Project> getProjectsList() {
      if (projectsBuilder_ == null) {
        return java.util.Collections.unmodifiableList(projects_);
      } else {
        return projectsBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public int getProjectsCount() {
      if (projectsBuilder_ == null) {
        return projects_.size();
      } else {
        return projectsBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public com.jomofisher.cmakeserver.model.Project getProjects(int index) {
      if (projectsBuilder_ == null) {
        return projects_.get(index);
      } else {
        return projectsBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public Builder setProjects(
        int index, com.jomofisher.cmakeserver.model.Project value) {
      if (projectsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureProjectsIsMutable();
        projects_.set(index, value);
        onChanged();
      } else {
        projectsBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public Builder setProjects(
        int index, com.jomofisher.cmakeserver.model.Project.Builder builderForValue) {
      if (projectsBuilder_ == null) {
        ensureProjectsIsMutable();
        projects_.set(index, builderForValue.build());
        onChanged();
      } else {
        projectsBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public Builder addProjects(com.jomofisher.cmakeserver.model.Project value) {
      if (projectsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureProjectsIsMutable();
        projects_.add(value);
        onChanged();
      } else {
        projectsBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public Builder addProjects(
        int index, com.jomofisher.cmakeserver.model.Project value) {
      if (projectsBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureProjectsIsMutable();
        projects_.add(index, value);
        onChanged();
      } else {
        projectsBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public Builder addProjects(
        com.jomofisher.cmakeserver.model.Project.Builder builderForValue) {
      if (projectsBuilder_ == null) {
        ensureProjectsIsMutable();
        projects_.add(builderForValue.build());
        onChanged();
      } else {
        projectsBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public Builder addProjects(
        int index, com.jomofisher.cmakeserver.model.Project.Builder builderForValue) {
      if (projectsBuilder_ == null) {
        ensureProjectsIsMutable();
        projects_.add(index, builderForValue.build());
        onChanged();
      } else {
        projectsBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public Builder addAllProjects(
        java.lang.Iterable<? extends com.jomofisher.cmakeserver.model.Project> values) {
      if (projectsBuilder_ == null) {
        ensureProjectsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, projects_);
        onChanged();
      } else {
        projectsBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public Builder clearProjects() {
      if (projectsBuilder_ == null) {
        projects_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000002);
        onChanged();
      } else {
        projectsBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public Builder removeProjects(int index) {
      if (projectsBuilder_ == null) {
        ensureProjectsIsMutable();
        projects_.remove(index);
        onChanged();
      } else {
        projectsBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public com.jomofisher.cmakeserver.model.Project.Builder getProjectsBuilder(
        int index) {
      return getProjectsFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public com.jomofisher.cmakeserver.model.ProjectOrBuilder getProjectsOrBuilder(
        int index) {
      if (projectsBuilder_ == null) {
        return projects_.get(index);  } else {
        return projectsBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public java.util.List<? extends com.jomofisher.cmakeserver.model.ProjectOrBuilder> 
         getProjectsOrBuilderList() {
      if (projectsBuilder_ != null) {
        return projectsBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(projects_);
      }
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public com.jomofisher.cmakeserver.model.Project.Builder addProjectsBuilder() {
      return getProjectsFieldBuilder().addBuilder(
          com.jomofisher.cmakeserver.model.Project.getDefaultInstance());
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public com.jomofisher.cmakeserver.model.Project.Builder addProjectsBuilder(
        int index) {
      return getProjectsFieldBuilder().addBuilder(
          index, com.jomofisher.cmakeserver.model.Project.getDefaultInstance());
    }
    /**
     * <code>repeated .Project projects = 2;</code>
     */
    public java.util.List<com.jomofisher.cmakeserver.model.Project.Builder> 
         getProjectsBuilderList() {
      return getProjectsFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.jomofisher.cmakeserver.model.Project, com.jomofisher.cmakeserver.model.Project.Builder, com.jomofisher.cmakeserver.model.ProjectOrBuilder> 
        getProjectsFieldBuilder() {
      if (projectsBuilder_ == null) {
        projectsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            com.jomofisher.cmakeserver.model.Project, com.jomofisher.cmakeserver.model.Project.Builder, com.jomofisher.cmakeserver.model.ProjectOrBuilder>(
                projects_,
                ((bitField0_ & 0x00000002) == 0x00000002),
                getParentForChildren(),
                isClean());
        projects_ = null;
      }
      return projectsBuilder_;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:Configuration)
  }

  // @@protoc_insertion_point(class_scope:Configuration)
  private static final com.jomofisher.cmakeserver.model.Configuration DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.jomofisher.cmakeserver.model.Configuration();
  }

  public static com.jomofisher.cmakeserver.model.Configuration getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Configuration>
      PARSER = new com.google.protobuf.AbstractParser<Configuration>() {
    public Configuration parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new Configuration(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<Configuration> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Configuration> getParserForType() {
    return PARSER;
  }

  public com.jomofisher.cmakeserver.model.Configuration getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

