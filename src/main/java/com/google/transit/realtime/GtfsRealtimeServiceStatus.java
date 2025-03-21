package com.google.transit.realtime;

public final class GtfsRealtimeServiceStatus {
  private GtfsRealtimeServiceStatus() {
  }

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {
    registry.add(com.google.transit.realtime.GtfsRealtimeServiceStatus.mercuryFeedHeader);
    registry.add(com.google.transit.realtime.GtfsRealtimeServiceStatus.mercuryAlert);
    registry.add(com.google.transit.realtime.GtfsRealtimeServiceStatus.mercuryEntitySelector);
  }

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public interface MercuryFeedHeaderOrBuilder extends com.google.protobuf.MessageOrBuilder {
    /**
     * <code>required string mercury_version = 1;</code>
     *
     * <pre>
     * Version of the Mercury extensions
     * The current version is 1.0
     * </pre>
     */
    boolean hasMercuryVersion();

    /**
     * <code>required string mercury_version = 1;</code>
     *
     * <pre>
     * Version of the Mercury extensions
     * The current version is 1.0
     * </pre>
     */
    java.lang.String getMercuryVersion();

    /**
     * <code>required string mercury_version = 1;</code>
     *
     * <pre>
     * Version of the Mercury extensions
     * The current version is 1.0
     * </pre>
     */
    com.google.protobuf.ByteString getMercuryVersionBytes();
  }

  public static final class MercuryFeedHeader extends com.google.protobuf.GeneratedMessageV3 implements MercuryFeedHeaderOrBuilder {
    private MercuryFeedHeader(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }

    private static final long serialVersionUID = 0L;

    private MercuryFeedHeader() {
      mercuryVersion_ = "";
    }

    @java.lang.Override @SuppressWarnings(value = { "unused" }) protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
      return new MercuryFeedHeader();
    }

    private static final com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader DEFAULT_INSTANCE;

    @java.lang.Override public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private MercuryFeedHeader(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
            done = true;
            break;
            case 10:
            {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              mercuryVersion_ = bs;
              break;
            }
            default:
            {
              if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }

    @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryFeedHeader_descriptor;
    }

    @java.lang.Override protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryFeedHeader_fieldAccessorTable.ensureFieldAccessorsInitialized(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader.Builder.class);
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<MercuryFeedHeader> PARSER = new com.google.protobuf.AbstractParser<MercuryFeedHeader>() {
      @java.lang.Override public MercuryFeedHeader parsePartialFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
        return new MercuryFeedHeader(input, extensionRegistry);
      }
    };

    private int bitField0_;

    @java.lang.Override public com.google.protobuf.Parser<MercuryFeedHeader> getParserForType() {
      return PARSER;
    }

    public static final int MERCURY_VERSION_FIELD_NUMBER = 1;

    private volatile java.lang.Object mercuryVersion_;

    /**
     * <code>required string mercury_version = 1;</code>
     *
     * <pre>
     * Version of the Mercury extensions
     * The current version is 1.0
     * </pre>
     */
    public boolean hasMercuryVersion() {
      return ((bitField0_ & 0x00000001) != 0);
    }

    /**
     * <code>required string mercury_version = 1;</code>
     *
     * <pre>
     * Version of the Mercury extensions
     * The current version is 1.0
     * </pre>
     */
    public java.lang.String getMercuryVersion() {
      java.lang.Object ref = mercuryVersion_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          mercuryVersion_ = s;
        }
        return s;
      }
    }

    /**
     * <code>required string mercury_version = 1;</code>
     *
     * <pre>
     * Version of the Mercury extensions
     * The current version is 1.0
     * </pre>
     */
    public com.google.protobuf.ByteString getMercuryVersionBytes() {
      java.lang.Object ref = mercuryVersion_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        mercuryVersion_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;

    @java.lang.Override public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) {
        return true;
      }
      if (isInitialized == 0) {
        return false;
      }
      if (!hasMercuryVersion()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, mercuryVersion_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) {
        return size;
      }
      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, mercuryVersion_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader)) {
        return super.equals(obj);
      }
      com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader other = (com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader) obj;
      if (hasMercuryVersion() != other.hasMercuryVersion()) {
        return false;
      }
      if (hasMercuryVersion()) {
        if (!getMercuryVersion().equals(other.getMercuryVersion())) {
          return false;
        }
      }
      if (!unknownFields.equals(other.unknownFields)) {
        return false;
      }
      return true;
    }

    @java.lang.Override public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasMercuryVersion()) {
        hash = (37 * hash) + MERCURY_VERSION_FIELD_NUMBER;
        hash = (53 * hash) + getMercuryVersion().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(java.io.InputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseDelimitedFrom(java.io.InputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseDelimitedFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(com.google.protobuf.CodedInputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parseFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }

    @java.lang.Override public Builder newBuilderForType() {
      return newBuilder();
    }

    public static Builder newBuilder(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }

    @java.lang.Override public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }

    public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeaderOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryFeedHeader_descriptor;
      }

      @java.lang.Override protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryFeedHeader_fieldAccessorTable.ensureFieldAccessorsInitialized(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader.Builder.class);
      }

      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
        }
      }

      @java.lang.Override public Builder clear() {
        super.clear();
        mercuryVersion_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override public Builder clone() {
        return super.clone();
      }

      @java.lang.Override public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryFeedHeader_descriptor;
      }

      @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader getDefaultInstanceForType() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader.getDefaultInstance();
      }

      @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader build() {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader buildPartial() {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader result = new com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.mercuryVersion_ = mercuryVersion_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
        return super.setField(field, value);
      }

      @java.lang.Override public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }

      @java.lang.Override public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }

      @java.lang.Override public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }

      @java.lang.Override public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }

      @java.lang.Override public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader) {
          return mergeFrom((com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader) other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader other) {
        if (other == com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader.getDefaultInstance()) {
          return this;
        }
        if (other.hasMercuryVersion()) {
          bitField0_ |= 0x00000001;
          mercuryVersion_ = other.mercuryVersion_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override public final boolean isInitialized() {
        if (!hasMercuryVersion()) {
          return false;
        }
        return true;
      }

      @java.lang.Override public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int bitField0_;

      private java.lang.Object mercuryVersion_ = "";

      /**
       * <code>required string mercury_version = 1;</code>
       *
       * <pre>
       * Version of the Mercury extensions
       * The current version is 1.0
       * </pre>
       */
      public boolean hasMercuryVersion() {
        return ((bitField0_ & 0x00000001) != 0);
      }

      /**
       * <code>required string mercury_version = 1;</code>
       *
       * <pre>
       * Version of the Mercury extensions
       * The current version is 1.0
       * </pre>
       */
      public java.lang.String getMercuryVersion() {
        java.lang.Object ref = mercuryVersion_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            mercuryVersion_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }

      /**
       * <code>required string mercury_version = 1;</code>
       *
       * <pre>
       * Version of the Mercury extensions
       * The current version is 1.0
       * </pre>
       */
      public com.google.protobuf.ByteString getMercuryVersionBytes() {
        java.lang.Object ref = mercuryVersion_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
          mercuryVersion_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }

      /**
       * <code>required string mercury_version = 1;</code>
       *
       * <pre>
       * Version of the Mercury extensions
       * The current version is 1.0
       * </pre>
       */
      public Builder setMercuryVersion(java.lang.String value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        mercuryVersion_ = value;
        onChanged();
        return this;
      }

      /**
       * <code>required string mercury_version = 1;</code>
       *
       * <pre>
       * Version of the Mercury extensions
       * The current version is 1.0
       * </pre>
       */
      public Builder clearMercuryVersion() {
        bitField0_ = (bitField0_ & ~0x00000001);
        mercuryVersion_ = getDefaultInstance().getMercuryVersion();
        onChanged();
        return this;
      }

      /**
       * <code>required string mercury_version = 1;</code>
       *
       * <pre>
       * Version of the Mercury extensions
       * The current version is 1.0
       * </pre>
       */
      public Builder setMercuryVersionBytes(com.google.protobuf.ByteString value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        mercuryVersion_ = value;
        onChanged();
        return this;
      }

      @java.lang.Override public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }
    }

    static {
      DEFAULT_INSTANCE = new com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader();
    }

    public static com.google.protobuf.Parser<MercuryFeedHeader> parser() {
      return PARSER;
    }
  }

  public interface MercuryStationAlternativeOrBuilder extends com.google.protobuf.MessageOrBuilder {
    /**
     * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
     */
    boolean hasAffectedEntity();

    /**
     * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
     */
    com.google.transit.realtime.GtfsRealtime.EntitySelector getAffectedEntity();

    /**
     * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
     */
    com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder getAffectedEntityOrBuilder();

    /**
     * <code>required .transit_realtime.TranslatedString notes = 2;</code>
     */
    boolean hasNotes();

    /**
     * <code>required .transit_realtime.TranslatedString notes = 2;</code>
     */
    com.google.transit.realtime.GtfsRealtime.TranslatedString getNotes();

    /**
     * <code>required .transit_realtime.TranslatedString notes = 2;</code>
     */
    com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getNotesOrBuilder();
  }

  public static final class MercuryStationAlternative extends com.google.protobuf.GeneratedMessage implements MercuryStationAlternativeOrBuilder {
    private MercuryStationAlternative(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }

    private MercuryStationAlternative(boolean noInit) {
      this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }

    private static final MercuryStationAlternative defaultInstance;

    public static MercuryStationAlternative getDefaultInstance() {
      return defaultInstance;
    }

    public MercuryStationAlternative getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;

    @java.lang.Override public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }

    private MercuryStationAlternative(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
            done = true;
            break;
            default:
            {
              if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10:
            {
              com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder subBuilder = null;
              if (((bitField0_ & 0x00000001) == 0x00000001)) {
                subBuilder = affectedEntity_.toBuilder();
              }
              affectedEntity_ = input.readMessage(com.google.transit.realtime.GtfsRealtime.EntitySelector.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(affectedEntity_);
                affectedEntity_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000001;
              break;
            }
            case 18:
            {
              com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder subBuilder = null;
              if (((bitField0_ & 0x00000002) == 0x00000002)) {
                subBuilder = notes_.toBuilder();
              }
              notes_ = input.readMessage(com.google.transit.realtime.GtfsRealtime.TranslatedString.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(notes_);
                notes_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000002;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }

    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryStationAlternative_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
      return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryStationAlternative_fieldAccessorTable.ensureFieldAccessorsInitialized(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder.class);
    }

    public static com.google.protobuf.Parser<MercuryStationAlternative> PARSER = new com.google.protobuf.AbstractParser<MercuryStationAlternative>() {
      public MercuryStationAlternative parsePartialFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
        return new MercuryStationAlternative(input, extensionRegistry);
      }
    };

    @java.lang.Override public com.google.protobuf.Parser<MercuryStationAlternative> getParserForType() {
      return PARSER;
    }

    private int bitField0_;

    public static final int AFFECTED_ENTITY_FIELD_NUMBER = 1;

    private com.google.transit.realtime.GtfsRealtime.EntitySelector affectedEntity_;

    /**
     * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
     */
    public boolean hasAffectedEntity() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }

    /**
     * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.EntitySelector getAffectedEntity() {
      return affectedEntity_;
    }

    /**
     * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder getAffectedEntityOrBuilder() {
      return affectedEntity_;
    }

    public static final int NOTES_FIELD_NUMBER = 2;

    private com.google.transit.realtime.GtfsRealtime.TranslatedString notes_;

    /**
     * <code>required .transit_realtime.TranslatedString notes = 2;</code>
     */
    public boolean hasNotes() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }

    /**
     * <code>required .transit_realtime.TranslatedString notes = 2;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.TranslatedString getNotes() {
      return notes_;
    }

    /**
     * <code>required .transit_realtime.TranslatedString notes = 2;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getNotesOrBuilder() {
      return notes_;
    }

    private void initFields() {
      affectedEntity_ = com.google.transit.realtime.GtfsRealtime.EntitySelector.getDefaultInstance();
      notes_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
    }

    private byte memoizedIsInitialized = -1;

    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) {
        return true;
      }
      if (isInitialized == 0) {
        return false;
      }
      if (!hasAffectedEntity()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasNotes()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!getAffectedEntity().isInitialized()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!getNotes().isInitialized()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeMessage(1, affectedEntity_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeMessage(2, notes_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;

    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) {
        return size;
      }
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, affectedEntity_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream.computeMessageSize(2, notes_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;

    @java.lang.Override protected java.lang.Object writeReplace() throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseFrom(com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseFrom(com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseFrom(byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseFrom(byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseFrom(java.io.InputStream input) throws java.io.IOException {
      return PARSER.parseFrom(input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseDelimitedFrom(java.io.InputStream input) throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseDelimitedFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseFrom(com.google.protobuf.CodedInputStream input) throws java.io.IOException {
      return PARSER.parseFrom(input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parseFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return Builder.create();
    }

    public Builder newBuilderForType() {
      return newBuilder();
    }

    public static Builder newBuilder(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative prototype) {
      return newBuilder().mergeFrom(prototype);
    }

    public Builder toBuilder() {
      return newBuilder(this);
    }

    @java.lang.Override protected Builder newBuilderForType(com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }

    public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder> implements com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryStationAlternative_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryStationAlternative_fieldAccessorTable.ensureFieldAccessorsInitialized(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder.class);
      }

      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
          getAffectedEntityFieldBuilder();
          getNotesFieldBuilder();
        }
      }

      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        if (affectedEntityBuilder_ == null) {
          affectedEntity_ = com.google.transit.realtime.GtfsRealtime.EntitySelector.getDefaultInstance();
        } else {
          affectedEntityBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        if (notesBuilder_ == null) {
          notes_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
        } else {
          notesBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryStationAlternative_descriptor;
      }

      public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative getDefaultInstanceForType() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.getDefaultInstance();
      }

      public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative build() {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative buildPartial() {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative result = new com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        if (affectedEntityBuilder_ == null) {
          result.affectedEntity_ = affectedEntity_;
        } else {
          result.affectedEntity_ = affectedEntityBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        if (notesBuilder_ == null) {
          result.notes_ = notes_;
        } else {
          result.notes_ = notesBuilder_.build();
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative) {
          return mergeFrom((com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative) other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative other) {
        if (other == com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.getDefaultInstance()) {
          return this;
        }
        if (other.hasAffectedEntity()) {
          mergeAffectedEntity(other.getAffectedEntity());
        }
        if (other.hasNotes()) {
          mergeNotes(other.getNotes());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasAffectedEntity()) {
          return false;
        }
        if (!hasNotes()) {
          return false;
        }
        if (!getAffectedEntity().isInitialized()) {
          return false;
        }
        if (!getNotes().isInitialized()) {
          return false;
        }
        return true;
      }

      public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int bitField0_;

      private com.google.transit.realtime.GtfsRealtime.EntitySelector affectedEntity_ = com.google.transit.realtime.GtfsRealtime.EntitySelector.getDefaultInstance();

      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.EntitySelector, com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder, com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder> affectedEntityBuilder_;

      /**
       * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
       */
      public boolean hasAffectedEntity() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }

      /**
       * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.EntitySelector getAffectedEntity() {
        if (affectedEntityBuilder_ == null) {
          return affectedEntity_;
        } else {
          return affectedEntityBuilder_.getMessage();
        }
      }

      /**
       * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
       */
      public Builder setAffectedEntity(com.google.transit.realtime.GtfsRealtime.EntitySelector value) {
        if (affectedEntityBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          affectedEntity_ = value;
          onChanged();
        } else {
          affectedEntityBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }

      /**
       * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
       */
      public Builder setAffectedEntity(com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder builderForValue) {
        if (affectedEntityBuilder_ == null) {
          affectedEntity_ = builderForValue.build();
          onChanged();
        } else {
          affectedEntityBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000001;
        return this;
      }

      /**
       * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
       */
      public Builder mergeAffectedEntity(com.google.transit.realtime.GtfsRealtime.EntitySelector value) {
        if (affectedEntityBuilder_ == null) {
          if (((bitField0_ & 0x00000001) == 0x00000001) && affectedEntity_ != com.google.transit.realtime.GtfsRealtime.EntitySelector.getDefaultInstance()) {
            affectedEntity_ = com.google.transit.realtime.GtfsRealtime.EntitySelector.newBuilder(affectedEntity_).mergeFrom(value).buildPartial();
          } else {
            affectedEntity_ = value;
          }
          onChanged();
        } else {
          affectedEntityBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }

      /**
       * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
       */
      public Builder clearAffectedEntity() {
        if (affectedEntityBuilder_ == null) {
          affectedEntity_ = com.google.transit.realtime.GtfsRealtime.EntitySelector.getDefaultInstance();
          onChanged();
        } else {
          affectedEntityBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      /**
       * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder getAffectedEntityBuilder() {
        bitField0_ |= 0x00000001;
        onChanged();
        return getAffectedEntityFieldBuilder().getBuilder();
      }

      /**
       * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder getAffectedEntityOrBuilder() {
        if (affectedEntityBuilder_ != null) {
          return affectedEntityBuilder_.getMessageOrBuilder();
        } else {
          return affectedEntity_;
        }
      }

      /**
       * <code>required .transit_realtime.EntitySelector affected_entity = 1;</code>
       */
      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.EntitySelector, com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder, com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder> getAffectedEntityFieldBuilder() {
        if (affectedEntityBuilder_ == null) {
          affectedEntityBuilder_ = new com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.EntitySelector, com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder, com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder>(getAffectedEntity(), getParentForChildren(), isClean());
          affectedEntity_ = null;
        }
        return affectedEntityBuilder_;
      }

      private com.google.transit.realtime.GtfsRealtime.TranslatedString notes_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();

      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder> notesBuilder_;

      /**
       * <code>required .transit_realtime.TranslatedString notes = 2;</code>
       */
      public boolean hasNotes() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }

      /**
       * <code>required .transit_realtime.TranslatedString notes = 2;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedString getNotes() {
        if (notesBuilder_ == null) {
          return notes_;
        } else {
          return notesBuilder_.getMessage();
        }
      }

      /**
       * <code>required .transit_realtime.TranslatedString notes = 2;</code>
       */
      public Builder setNotes(com.google.transit.realtime.GtfsRealtime.TranslatedString value) {
        if (notesBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          notes_ = value;
          onChanged();
        } else {
          notesBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }

      /**
       * <code>required .transit_realtime.TranslatedString notes = 2;</code>
       */
      public Builder setNotes(com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder builderForValue) {
        if (notesBuilder_ == null) {
          notes_ = builderForValue.build();
          onChanged();
        } else {
          notesBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000002;
        return this;
      }

      /**
       * <code>required .transit_realtime.TranslatedString notes = 2;</code>
       */
      public Builder mergeNotes(com.google.transit.realtime.GtfsRealtime.TranslatedString value) {
        if (notesBuilder_ == null) {
          if (((bitField0_ & 0x00000002) == 0x00000002) && notes_ != com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance()) {
            notes_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.newBuilder(notes_).mergeFrom(value).buildPartial();
          } else {
            notes_ = value;
          }
          onChanged();
        } else {
          notesBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000002;
        return this;
      }

      /**
       * <code>required .transit_realtime.TranslatedString notes = 2;</code>
       */
      public Builder clearNotes() {
        if (notesBuilder_ == null) {
          notes_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
          onChanged();
        } else {
          notesBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      /**
       * <code>required .transit_realtime.TranslatedString notes = 2;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder getNotesBuilder() {
        bitField0_ |= 0x00000002;
        onChanged();
        return getNotesFieldBuilder().getBuilder();
      }

      /**
       * <code>required .transit_realtime.TranslatedString notes = 2;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getNotesOrBuilder() {
        if (notesBuilder_ != null) {
          return notesBuilder_.getMessageOrBuilder();
        } else {
          return notes_;
        }
      }

      /**
       * <code>required .transit_realtime.TranslatedString notes = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder> getNotesFieldBuilder() {
        if (notesBuilder_ == null) {
          notesBuilder_ = new com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder>(getNotes(), getParentForChildren(), isClean());
          notes_ = null;
        }
        return notesBuilder_;
      }
    }

    static {
      defaultInstance = new MercuryStationAlternative(true);
      defaultInstance.initFields();
    }
  }

  public interface MercuryAlertOrBuilder extends com.google.protobuf.MessageOrBuilder {
    /**
     * <code>required uint64 created_at = 1;</code>
     */
    boolean hasCreatedAt();

    /**
     * <code>required uint64 created_at = 1;</code>
     */
    long getCreatedAt();

    /**
     * <code>required uint64 updated_at = 2;</code>
     */
    boolean hasUpdatedAt();

    /**
     * <code>required uint64 updated_at = 2;</code>
     */
    long getUpdatedAt();

    /**
     * <code>required string alert_type = 3;</code>
     */
    boolean hasAlertType();

    /**
     * <code>required string alert_type = 3;</code>
     */
    java.lang.String getAlertType();

    /**
     * <code>required string alert_type = 3;</code>
     */
    com.google.protobuf.ByteString getAlertTypeBytes();

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    java.util.List<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative> getStationAlternativeList();

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative getStationAlternative(int index);

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    int getStationAlternativeCount();

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    java.util.List<? extends com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder> getStationAlternativeOrBuilderList();

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder getStationAlternativeOrBuilder(int index);

    /**
     * <code>repeated string service_plan_number = 5;</code>
     */
    com.google.protobuf.ProtocolStringList getServicePlanNumberList();

    /**
     * <code>repeated string service_plan_number = 5;</code>
     */
    int getServicePlanNumberCount();

    /**
     * <code>repeated string service_plan_number = 5;</code>
     */
    java.lang.String getServicePlanNumber(int index);

    /**
     * <code>repeated string service_plan_number = 5;</code>
     */
    com.google.protobuf.ByteString getServicePlanNumberBytes(int index);

    /**
     * <code>repeated string general_order_number = 6;</code>
     */
    com.google.protobuf.ProtocolStringList getGeneralOrderNumberList();

    /**
     * <code>repeated string general_order_number = 6;</code>
     */
    int getGeneralOrderNumberCount();

    /**
     * <code>repeated string general_order_number = 6;</code>
     */
    java.lang.String getGeneralOrderNumber(int index);

    /**
     * <code>repeated string general_order_number = 6;</code>
     */
    com.google.protobuf.ByteString getGeneralOrderNumberBytes(int index);

    /**
     * <code>optional uint64 display_before_active = 7;</code>
     *
     * <pre>
     * A time interval, in seconds, indicating how long before each active period
     * consumers should display this alert. A value of 3600 here, for example,
     * suggests that this alert should be displayed 1 hour before each active
     * period. Consumers may choose to ignore this recommendation and display (or
     * not display) alerts based on their own logic if they so choose.
     * </pre>
     */
    boolean hasDisplayBeforeActive();

    /**
     * <code>optional uint64 display_before_active = 7;</code>
     *
     * <pre>
     * A time interval, in seconds, indicating how long before each active period
     * consumers should display this alert. A value of 3600 here, for example,
     * suggests that this alert should be displayed 1 hour before each active
     * period. Consumers may choose to ignore this recommendation and display (or
     * not display) alerts based on their own logic if they so choose.
     * </pre>
     */
    long getDisplayBeforeActive();

    /**
     * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
     *
     * <pre>
     * A human-friendly string that summarizes all active periods for this Alert,
     * i.e. "Sundays in May from 10:45pm to midnight"
     * </pre>
     */
    boolean hasHumanReadableActivePeriod();

    /**
     * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
     *
     * <pre>
     * A human-friendly string that summarizes all active periods for this Alert,
     * i.e. "Sundays in May from 10:45pm to midnight"
     * </pre>
     */
    com.google.transit.realtime.GtfsRealtime.TranslatedString getHumanReadableActivePeriod();

    /**
     * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
     *
     * <pre>
     * A human-friendly string that summarizes all active periods for this Alert,
     * i.e. "Sundays in May from 10:45pm to midnight"
     * </pre>
     */
    com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getHumanReadableActivePeriodOrBuilder();

    /**
     * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
     */
    boolean hasAdditionalInformation();

    /**
     * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
     */
    com.google.transit.realtime.GtfsRealtime.TranslatedString getAdditionalInformation();

    /**
     * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
     */
    com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getAdditionalInformationOrBuilder();

    /**
     * <code>optional uint64 directionality = 10;</code>
     */
    boolean hasDirectionality();

    /**
     * <code>optional uint64 directionality = 10;</code>
     */
    long getDirectionality();

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    java.util.List<com.google.transit.realtime.GtfsRealtime.EntitySelector> getAffectedStationsList();

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    com.google.transit.realtime.GtfsRealtime.EntitySelector getAffectedStations(int index);

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    int getAffectedStationsCount();

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    java.util.List<? extends com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder> getAffectedStationsOrBuilderList();

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder getAffectedStationsOrBuilder(int index);

    /**
     * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
     */
    boolean hasScreensSummary();

    /**
     * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
     */
    com.google.transit.realtime.GtfsRealtime.TranslatedString getScreensSummary();

    /**
     * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
     */
    com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getScreensSummaryOrBuilder();
  }

  public static final class MercuryAlert extends com.google.protobuf.GeneratedMessageV3 implements MercuryAlertOrBuilder {
    private MercuryAlert(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }

    private static final long serialVersionUID = 0L;

    private MercuryAlert() {
      alertType_ = "";
    }

    @java.lang.Override @SuppressWarnings(value = { "unused" }) protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
      return new MercuryAlert();
    }

    private static final com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert DEFAULT_INSTANCE;

    @java.lang.Override public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private MercuryAlert(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
            done = true;
            break;
            case 8:
            {
              bitField0_ |= 0x00000001;
              createdAt_ = input.readUInt64();
              break;
            }
            case 16:
            {
              bitField0_ |= 0x00000002;
              updatedAt_ = input.readUInt64();
              break;
            }
            case 26:
            {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000004;
              alertType_ = bs;
              break;
            }

<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
            case 34:
=======
            default:
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java

            {
              if (!((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
                stationAlternative_ = new java.util.ArrayList<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative>();
                mutable_bitField0_ |= 0x00000008;
              }
              stationAlternative_.add(input.readMessage(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.PARSER, extensionRegistry));
              break;
            }
            case 42:
            {
              com.google.protobuf.ByteString bs = input.readBytes();
              if (!((mutable_bitField0_ & 0x00000010) == 0x00000010)) {
                servicePlanNumber_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000010;
              }
              servicePlanNumber_.add(bs);
              break;
            }
            case 50:
            {
              com.google.protobuf.ByteString bs = input.readBytes();
              if (!((mutable_bitField0_ & 0x00000020) == 0x00000020)) {
                generalOrderNumber_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000020;
              }
              generalOrderNumber_.add(bs);
              break;
            }
            case 56:
            {
              bitField0_ |= 0x00000008;
              displayBeforeActive_ = input.readUInt64();
              break;
            }
            case 66:
            {
              com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder subBuilder = null;
              if (((bitField0_ & 0x00000010) == 0x00000010)) {
                subBuilder = humanReadableActivePeriod_.toBuilder();
              }
              humanReadableActivePeriod_ = input.readMessage(com.google.transit.realtime.GtfsRealtime.TranslatedString.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(humanReadableActivePeriod_);
                humanReadableActivePeriod_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000010;
              break;
            }
            case 74:
            {
              com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder subBuilder = null;
              if (((bitField0_ & 0x00000020) == 0x00000020)) {
                subBuilder = additionalInformation_.toBuilder();
              }
              additionalInformation_ = input.readMessage(com.google.transit.realtime.GtfsRealtime.TranslatedString.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(additionalInformation_);
                additionalInformation_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000020;
              break;
            }
            case 80:
            {
              bitField0_ |= 0x00000040;
              directionality_ = input.readUInt64();
              break;
            }
            case 90:
            {
              if (
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
              !((mutable_bitField0_ & 0x00000400) == 0x00000400)
=======
              !parseUnknownField(input, unknownFields, extensionRegistry, tag)
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
              ) {

<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
                affectedStations_ = new java.util.ArrayList<com.google.transit.realtime.GtfsRealtime.EntitySelector>()
=======
                done = true
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
                ;
                mutable_bitField0_ |= 0x00000400;
              }
              affectedStations_.add(input.readMessage(com.google.transit.realtime.GtfsRealtime.EntitySelector.PARSER, extensionRegistry));
              break;
            }
            case 98:
            {
              com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder subBuilder = null;
              if (((bitField0_ & 0x00000080) == 0x00000080)) {
                subBuilder = screensSummary_.toBuilder();
              }
              screensSummary_ = input.readMessage(com.google.transit.realtime.GtfsRealtime.TranslatedString.PARSER, extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(screensSummary_);
                screensSummary_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000080;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000008) == 0x00000008)) {
          stationAlternative_ = java.util.Collections.unmodifiableList(stationAlternative_);
        }
        if (((mutable_bitField0_ & 0x00000010) == 0x00000010)) {
          servicePlanNumber_ = servicePlanNumber_.getUnmodifiableView();
        }
        if (((mutable_bitField0_ & 0x00000020) == 0x00000020)) {
          generalOrderNumber_ = generalOrderNumber_.getUnmodifiableView();
        }
        if (((mutable_bitField0_ & 0x00000400) == 0x00000400)) {
          affectedStations_ = java.util.Collections.unmodifiableList(affectedStations_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }

    @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryAlert_descriptor;
    }

    @java.lang.Override protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryAlert_fieldAccessorTable.ensureFieldAccessorsInitialized(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.Builder.class);
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<MercuryAlert> PARSER = new com.google.protobuf.AbstractParser<MercuryAlert>() {
      @java.lang.Override public MercuryAlert parsePartialFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
        return new MercuryAlert(input, extensionRegistry);
      }
    };

    private int bitField0_;

    @java.lang.Override public com.google.protobuf.Parser<MercuryAlert> getParserForType() {
      return PARSER;
    }

    public static final int CREATED_AT_FIELD_NUMBER = 1;

    private long createdAt_;

    /**
     * <code>required uint64 created_at = 1;</code>
     */
    public boolean hasCreatedAt() {
      return ((bitField0_ & 0x00000001) != 0);
    }

    /**
     * <code>required uint64 created_at = 1;</code>
     */
    public long getCreatedAt() {
      return createdAt_;
    }

    public static final int UPDATED_AT_FIELD_NUMBER = 2;

    private long updatedAt_;

    /**
     * <code>required uint64 updated_at = 2;</code>
     */
    public boolean hasUpdatedAt() {
      return ((bitField0_ & 0x00000002) != 0);
    }

    /**
     * <code>required uint64 updated_at = 2;</code>
     */
    public long getUpdatedAt() {
      return updatedAt_;
    }

    public static final int ALERT_TYPE_FIELD_NUMBER = 3;

    private volatile java.lang.Object alertType_;

    /**
     * <code>required string alert_type = 3;</code>
     */
    public boolean hasAlertType() {
      return ((bitField0_ & 0x00000004) != 0);
    }

    /**
     * <code>required string alert_type = 3;</code>
     */
    public java.lang.String getAlertType() {
      java.lang.Object ref = alertType_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          alertType_ = s;
        }
        return s;
      }
    }

    /**
     * <code>required string alert_type = 3;</code>
     */
    public com.google.protobuf.ByteString getAlertTypeBytes() {
      java.lang.Object ref = alertType_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        alertType_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int STATION_ALTERNATIVE_FIELD_NUMBER = 4;

    private java.util.List<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative> stationAlternative_;

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    public java.util.List<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative> getStationAlternativeList() {
      return stationAlternative_;
    }

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    public java.util.List<? extends com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder> getStationAlternativeOrBuilderList() {
      return stationAlternative_;
    }

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    public int getStationAlternativeCount() {
      return stationAlternative_.size();
    }

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative getStationAlternative(int index) {
      return stationAlternative_.get(index);
    }

    /**
     * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
     */
    public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder getStationAlternativeOrBuilder(int index) {
      return stationAlternative_.get(index);
    }

    public static final int SERVICE_PLAN_NUMBER_FIELD_NUMBER = 5;

    private com.google.protobuf.LazyStringList servicePlanNumber_;

    /**
     * <code>repeated string service_plan_number = 5;</code>
     */
    public com.google.protobuf.ProtocolStringList getServicePlanNumberList() {
      return servicePlanNumber_;
    }

    /**
     * <code>repeated string service_plan_number = 5;</code>
     */
    public int getServicePlanNumberCount() {
      return servicePlanNumber_.size();
    }

    /**
     * <code>repeated string service_plan_number = 5;</code>
     */
    public java.lang.String getServicePlanNumber(int index) {
      return servicePlanNumber_.get(index);
    }

    /**
     * <code>repeated string service_plan_number = 5;</code>
     */
    public com.google.protobuf.ByteString getServicePlanNumberBytes(int index) {
      return servicePlanNumber_.getByteString(index);
    }

    public static final int GENERAL_ORDER_NUMBER_FIELD_NUMBER = 6;

    private com.google.protobuf.LazyStringList generalOrderNumber_;

    /**
     * <code>repeated string general_order_number = 6;</code>
     */
    public com.google.protobuf.ProtocolStringList getGeneralOrderNumberList() {
      return generalOrderNumber_;
    }

    /**
     * <code>repeated string general_order_number = 6;</code>
     */
    public int getGeneralOrderNumberCount() {
      return generalOrderNumber_.size();
    }

    /**
     * <code>repeated string general_order_number = 6;</code>
     */
    public java.lang.String getGeneralOrderNumber(int index) {
      return generalOrderNumber_.get(index);
    }

    /**
     * <code>repeated string general_order_number = 6;</code>
     */
    public com.google.protobuf.ByteString getGeneralOrderNumberBytes(int index) {
      return generalOrderNumber_.getByteString(index);
    }

    public static final int DISPLAY_BEFORE_ACTIVE_FIELD_NUMBER = 7;

    private long displayBeforeActive_;

    /**
     * <code>optional uint64 display_before_active = 7;</code>
     *
     * <pre>
     * A time interval, in seconds, indicating how long before each active period
     * consumers should display this alert. A value of 3600 here, for example,
     * suggests that this alert should be displayed 1 hour before each active
     * period. Consumers may choose to ignore this recommendation and display (or
     * not display) alerts based on their own logic if they so choose.
     * </pre>
     */
    public boolean hasDisplayBeforeActive() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }

    /**
     * <code>optional uint64 display_before_active = 7;</code>
     *
     * <pre>
     * A time interval, in seconds, indicating how long before each active period
     * consumers should display this alert. A value of 3600 here, for example,
     * suggests that this alert should be displayed 1 hour before each active
     * period. Consumers may choose to ignore this recommendation and display (or
     * not display) alerts based on their own logic if they so choose.
     * </pre>
     */
    public long getDisplayBeforeActive() {
      return displayBeforeActive_;
    }

    public static final int HUMAN_READABLE_ACTIVE_PERIOD_FIELD_NUMBER = 8;

    private com.google.transit.realtime.GtfsRealtime.TranslatedString humanReadableActivePeriod_;

    /**
     * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
     *
     * <pre>
     * A human-friendly string that summarizes all active periods for this Alert,
     * i.e. "Sundays in May from 10:45pm to midnight"
     * </pre>
     */
    public boolean hasHumanReadableActivePeriod() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }

    /**
     * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
     *
     * <pre>
     * A human-friendly string that summarizes all active periods for this Alert,
     * i.e. "Sundays in May from 10:45pm to midnight"
     * </pre>
     */
    public com.google.transit.realtime.GtfsRealtime.TranslatedString getHumanReadableActivePeriod() {
      return humanReadableActivePeriod_;
    }

    /**
     * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
     *
     * <pre>
     * A human-friendly string that summarizes all active periods for this Alert,
     * i.e. "Sundays in May from 10:45pm to midnight"
     * </pre>
     */
    public com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getHumanReadableActivePeriodOrBuilder() {
      return humanReadableActivePeriod_;
    }

    public static final int ADDITIONAL_INFORMATION_FIELD_NUMBER = 9;

    private com.google.transit.realtime.GtfsRealtime.TranslatedString additionalInformation_;

    /**
     * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
     */
    public boolean hasAdditionalInformation() {
      return ((bitField0_ & 0x00000020) == 0x00000020);
    }

    /**
     * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.TranslatedString getAdditionalInformation() {
      return additionalInformation_;
    }

    /**
     * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getAdditionalInformationOrBuilder() {
      return additionalInformation_;
    }

    public static final int DIRECTIONALITY_FIELD_NUMBER = 10;

    private long directionality_;

    /**
     * <code>optional uint64 directionality = 10;</code>
     */
    public boolean hasDirectionality() {
      return ((bitField0_ & 0x00000040) == 0x00000040);
    }

    /**
     * <code>optional uint64 directionality = 10;</code>
     */
    public long getDirectionality() {
      return directionality_;
    }

    public static final int AFFECTED_STATIONS_FIELD_NUMBER = 11;

    private java.util.List<com.google.transit.realtime.GtfsRealtime.EntitySelector> affectedStations_;

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    public java.util.List<com.google.transit.realtime.GtfsRealtime.EntitySelector> getAffectedStationsList() {
      return affectedStations_;
    }

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    public java.util.List<? extends com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder> getAffectedStationsOrBuilderList() {
      return affectedStations_;
    }

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    public int getAffectedStationsCount() {
      return affectedStations_.size();
    }

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.EntitySelector getAffectedStations(int index) {
      return affectedStations_.get(index);
    }

    /**
     * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder getAffectedStationsOrBuilder(int index) {
      return affectedStations_.get(index);
    }

    public static final int SCREENS_SUMMARY_FIELD_NUMBER = 12;

    private com.google.transit.realtime.GtfsRealtime.TranslatedString screensSummary_;

    /**
     * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
     */
    public boolean hasScreensSummary() {
      return ((bitField0_ & 0x00000080) == 0x00000080);
    }

    /**
     * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.TranslatedString getScreensSummary() {
      return screensSummary_;
    }

    /**
     * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
     */
    public com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getScreensSummaryOrBuilder() {
      return screensSummary_;
    }


<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    private void initFields() {
      createdAt_ = 0L;
      updatedAt_ = 0L;
      alertType_ = "";
      stationAlternative_ = java.util.Collections.emptyList();
      servicePlanNumber_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      generalOrderNumber_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      displayBeforeActive_ = 0L;
      humanReadableActivePeriod_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
      additionalInformation_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
      directionality_ = 0L;
      affectedStations_ = java.util.Collections.emptyList();
      screensSummary_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.


    private byte memoizedIsInitialized = -1;

    @java.lang.Override public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) {
        return true;
      }
      if (isInitialized == 0) {
        return false;
      }
      if (!hasCreatedAt()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasUpdatedAt()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasAlertType()) {
        memoizedIsInitialized = 0;
        return false;
      }
      for (int i = 0; i < getStationAlternativeCount(); i++) {
        if (!getStationAlternative(i).isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      if (hasHumanReadableActivePeriod()) {
        if (!getHumanReadableActivePeriod().isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      if (hasAdditionalInformation()) {
        if (!getAdditionalInformation().isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      for (int i = 0; i < getAffectedStationsCount(); i++) {
        if (!getAffectedStations(i).isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      if (hasScreensSummary()) {
        if (!getScreensSummary().isInitialized()) {
          memoizedIsInitialized = 0;
          return false;
        }
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeUInt64(1, createdAt_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeUInt64(2, updatedAt_);
      }
      for (int i = 0; i < stationAlternative_.size(); i++) {
        output.writeMessage(4, stationAlternative_.get(i));
      }
      for (int i = 0; i < servicePlanNumber_.size(); i++) {
        output.writeBytes(5, servicePlanNumber_.getByteString(i));
      }
      for (int i = 0; i < generalOrderNumber_.size(); i++) {
        output.writeBytes(6, generalOrderNumber_.getByteString(i));
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeUInt64(7, displayBeforeActive_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        output.writeMessage(8, humanReadableActivePeriod_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        output.writeMessage(9, additionalInformation_);
      }
      if (((bitField0_ & 0x00000040) == 0x00000040)) {
        output.writeUInt64(10, directionality_);
      }
      for (int i = 0; i < affectedStations_.size(); i++) {
        output.writeMessage(11, affectedStations_.get(i));
      }
      if (
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
      ((bitField0_ & 0x00000080) == 0x00000080)
=======
      ((bitField0_ & 0x00000004) != 0)
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
      ) {

<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
        output.writeMessage(12, screensSummary_)
=======
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, alertType_)
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
        ;
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) {
        return size;
      }
      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream.computeUInt64Size(1, createdAt_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream.computeUInt64Size(2, updatedAt_);
      }
      for (int i = 0; i < stationAlternative_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream.computeMessageSize(4, stationAlternative_.get(i));
      }
      {
        int dataSize = 0;
        for (int i = 0; i < servicePlanNumber_.size(); i++) {
          dataSize += com.google.protobuf.CodedOutputStream.computeBytesSizeNoTag(servicePlanNumber_.getByteString(i));
        }
        size += dataSize;
        size += 1 * getServicePlanNumberList().size();
      }
      {
        int dataSize = 0;
        for (int i = 0; i < generalOrderNumber_.size(); i++) {
          dataSize += com.google.protobuf.CodedOutputStream.computeBytesSizeNoTag(generalOrderNumber_.getByteString(i));
        }
        size += dataSize;
        size += 1 * getGeneralOrderNumberList().size();
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream.computeUInt64Size(7, displayBeforeActive_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        size += com.google.protobuf.CodedOutputStream.computeMessageSize(8, humanReadableActivePeriod_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        size += com.google.protobuf.CodedOutputStream.computeMessageSize(9, additionalInformation_);
      }
      if (((bitField0_ & 0x00000040) == 0x00000040)) {
        size += com.google.protobuf.CodedOutputStream.computeUInt64Size(10, directionality_);
      }
      for (int i = 0; i < affectedStations_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream.computeMessageSize(11, affectedStations_.get(i));
      }
      if (
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
      ((bitField0_ & 0x00000080) == 0x00000080)
=======
      ((bitField0_ & 0x00000004) != 0)
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
      ) {
        size += 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
        com.google.protobuf.CodedOutputStream
=======
        com.google.protobuf.GeneratedMessageV3
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
        .
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
        computeMessageSize(12, screensSummary_)
=======
        computeStringSize(3, alertType_)
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
        ;
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert)) {
        return super.equals(obj);
      }
      com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert other = (com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert) obj;
      if (hasCreatedAt() != other.hasCreatedAt()) {
        return false;
      }
      if (hasCreatedAt()) {
        if (getCreatedAt() != other.getCreatedAt()) {
          return false;
        }
      }
      if (hasUpdatedAt() != other.hasUpdatedAt()) {
        return false;
      }
      if (hasUpdatedAt()) {
        if (getUpdatedAt() != other.getUpdatedAt()) {
          return false;
        }
      }
      if (hasAlertType() != other.hasAlertType()) {
        return false;
      }
      if (hasAlertType()) {
        if (!getAlertType().equals(other.getAlertType())) {
          return false;
        }
      }
      if (!unknownFields.equals(other.unknownFields)) {
        return false;
      }
      return true;
    }

    @java.lang.Override public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasCreatedAt()) {
        hash = (37 * hash) + CREATED_AT_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getCreatedAt());
      }
      if (hasUpdatedAt()) {
        hash = (37 * hash) + UPDATED_AT_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(getUpdatedAt());
      }
      if (hasAlertType()) {
        hash = (37 * hash) + ALERT_TYPE_FIELD_NUMBER;
        hash = (53 * hash) + getAlertType().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(java.io.InputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseDelimitedFrom(java.io.InputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseDelimitedFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(com.google.protobuf.CodedInputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parseFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }

    @java.lang.Override public Builder newBuilderForType() {
      return newBuilder();
    }

    public static Builder newBuilder(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }

    @java.lang.Override public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }

    public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlertOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryAlert_descriptor;
      }

      @java.lang.Override protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryAlert_fieldAccessorTable.ensureFieldAccessorsInitialized(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.Builder.class);
      }

      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
          getStationAlternativeFieldBuilder();
          getHumanReadableActivePeriodFieldBuilder();
          getAdditionalInformationFieldBuilder();
          getAffectedStationsFieldBuilder();
          getScreensSummaryFieldBuilder();
        }
      }

      @java.lang.Override public Builder clear() {
        super.clear();
        createdAt_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000001);
        updatedAt_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        alertType_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        if (stationAlternativeBuilder_ == null) {
          stationAlternative_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000008);
        } else {
          stationAlternativeBuilder_.clear();
        }
        servicePlanNumber_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000010);
        generalOrderNumber_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000020);
        displayBeforeActive_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000040);
        if (humanReadableActivePeriodBuilder_ == null) {
          humanReadableActivePeriod_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
        } else {
          humanReadableActivePeriodBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000080);
        if (additionalInformationBuilder_ == null) {
          additionalInformation_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
        } else {
          additionalInformationBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000100);
        directionality_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000200);
        if (affectedStationsBuilder_ == null) {
          affectedStations_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000400);
        } else {
          affectedStationsBuilder_.clear();
        }
        if (screensSummaryBuilder_ == null) {
          screensSummary_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
        } else {
          screensSummaryBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000800);
        return this;
      }

      @java.lang.Override public Builder clone() {
        return super.clone();
      }

      @java.lang.Override public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryAlert_descriptor;
      }

      @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert getDefaultInstanceForType() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.getDefaultInstance();
      }

      @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert build() {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert buildPartial() {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert result = new com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.createdAt_ = createdAt_;
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.updatedAt_ = updatedAt_;
          to_bitField0_ |= 0x00000002;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          to_bitField0_ |= 0x00000004;
        }
        result.alertType_ = alertType_;
        if (stationAlternativeBuilder_ == null) {
          if (((bitField0_ & 0x00000008) == 0x00000008)) {
            stationAlternative_ = java.util.Collections.unmodifiableList(stationAlternative_);
            bitField0_ = (bitField0_ & ~0x00000008);
          }
          result.stationAlternative_ = stationAlternative_;
        } else {
          result.stationAlternative_ = stationAlternativeBuilder_.build();
        }
        if (((bitField0_ & 0x00000010) == 0x00000010)) {
          servicePlanNumber_ = servicePlanNumber_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000010);
        }
        result.servicePlanNumber_ = servicePlanNumber_;
        if (((bitField0_ & 0x00000020) == 0x00000020)) {
          generalOrderNumber_ = generalOrderNumber_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000020);
        }
        result.generalOrderNumber_ = generalOrderNumber_;
        if (((from_bitField0_ & 0x00000040) == 0x00000040)) {
          to_bitField0_ |= 0x00000008;
        }
        result.displayBeforeActive_ = displayBeforeActive_;
        if (((from_bitField0_ & 0x00000080) == 0x00000080)) {
          to_bitField0_ |= 0x00000010;
        }
        if (humanReadableActivePeriodBuilder_ == null) {
          result.humanReadableActivePeriod_ = humanReadableActivePeriod_;
        } else {
          result.humanReadableActivePeriod_ = humanReadableActivePeriodBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000100) == 0x00000100)) {
          to_bitField0_ |= 0x00000020;
        }
        if (additionalInformationBuilder_ == null) {
          result.additionalInformation_ = additionalInformation_;
        } else {
          result.additionalInformation_ = additionalInformationBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000200) == 0x00000200)) {
          to_bitField0_ |= 0x00000040;
        }
        result.directionality_ = directionality_;
        if (affectedStationsBuilder_ == null) {
          if (((bitField0_ & 0x00000400) == 0x00000400)) {
            affectedStations_ = java.util.Collections.unmodifiableList(affectedStations_);
            bitField0_ = (bitField0_ & ~0x00000400);
          }
          result.affectedStations_ = affectedStations_;
        } else {
          result.affectedStations_ = affectedStationsBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000800) == 0x00000800)) {
          to_bitField0_ |= 0x00000080;
        }
        if (screensSummaryBuilder_ == null) {
          result.screensSummary_ = screensSummary_;
        } else {
          result.screensSummary_ = screensSummaryBuilder_.build();
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
        return super.setField(field, value);
      }

      @java.lang.Override public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }

      @java.lang.Override public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }

      @java.lang.Override public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }

      @java.lang.Override public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }

      @java.lang.Override public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert) {
          return mergeFrom((com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert) other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert other) {
        if (other == com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.getDefaultInstance()) {
          return this;
        }
        if (other.hasCreatedAt()) {
          setCreatedAt(other.getCreatedAt());
        }
        if (other.hasUpdatedAt()) {
          setUpdatedAt(other.getUpdatedAt());
        }
        if (other.hasAlertType()) {
          bitField0_ |= 0x00000004;
          alertType_ = other.alertType_;
          onChanged();
        }
        if (stationAlternativeBuilder_ == null) {
          if (!other.stationAlternative_.isEmpty()) {
            if (stationAlternative_.isEmpty()) {
              stationAlternative_ = other.stationAlternative_;
              bitField0_ = (bitField0_ & ~0x00000008);
            } else {
              ensureStationAlternativeIsMutable();
              stationAlternative_.addAll(other.stationAlternative_);
            }
            onChanged();
          }
        } else {
          if (!other.stationAlternative_.isEmpty()) {
            if (stationAlternativeBuilder_.isEmpty()) {
              stationAlternativeBuilder_.dispose();
              stationAlternativeBuilder_ = null;
              stationAlternative_ = other.stationAlternative_;
              bitField0_ = (bitField0_ & ~0x00000008);
              stationAlternativeBuilder_ = com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders ? getStationAlternativeFieldBuilder() : null;
            } else {
              stationAlternativeBuilder_.addAllMessages(other.stationAlternative_);
            }
          }
        }
        if (!other.servicePlanNumber_.isEmpty()) {
          if (servicePlanNumber_.isEmpty()) {
            servicePlanNumber_ = other.servicePlanNumber_;
            bitField0_ = (bitField0_ & ~0x00000010);
          } else {
            ensureServicePlanNumberIsMutable();
            servicePlanNumber_.addAll(other.servicePlanNumber_);
          }
          onChanged();
        }
        if (!other.generalOrderNumber_.isEmpty()) {
          if (generalOrderNumber_.isEmpty()) {
            generalOrderNumber_ = other.generalOrderNumber_;
            bitField0_ = (bitField0_ & ~0x00000020);
          } else {
            ensureGeneralOrderNumberIsMutable();
            generalOrderNumber_.addAll(other.generalOrderNumber_);
          }
          onChanged();
        }
        if (other.hasDisplayBeforeActive()) {
          setDisplayBeforeActive(other.getDisplayBeforeActive());
        }
        if (other.hasHumanReadableActivePeriod()) {
          mergeHumanReadableActivePeriod(other.getHumanReadableActivePeriod());
        }
        if (other.hasAdditionalInformation()) {
          mergeAdditionalInformation(other.getAdditionalInformation());
        }
        if (other.hasDirectionality()) {
          setDirectionality(other.getDirectionality());
        }
        if (affectedStationsBuilder_ == null) {
          if (!other.affectedStations_.isEmpty()) {
            if (affectedStations_.isEmpty()) {
              affectedStations_ = other.affectedStations_;
              bitField0_ = (bitField0_ & ~0x00000400);
            } else {
              ensureAffectedStationsIsMutable();
              affectedStations_.addAll(other.affectedStations_);
            }
            onChanged();
          }
        } else {
          if (!other.affectedStations_.isEmpty()) {
            if (affectedStationsBuilder_.isEmpty()) {
              affectedStationsBuilder_.dispose();
              affectedStationsBuilder_ = null;
              affectedStations_ = other.affectedStations_;
              bitField0_ = (bitField0_ & ~0x00000400);
              affectedStationsBuilder_ = com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders ? getAffectedStationsFieldBuilder() : null;
            } else {
              affectedStationsBuilder_.addAllMessages(other.affectedStations_);
            }
          }
        }
        if (other.hasScreensSummary()) {
          mergeScreensSummary(other.getScreensSummary());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override public final boolean isInitialized() {
        if (!hasCreatedAt()) {
          return false;
        }
        if (!hasUpdatedAt()) {
          return false;
        }
        if (!hasAlertType()) {
          return false;
        }
        for (int i = 0; i < getStationAlternativeCount(); i++) {
          if (!getStationAlternative(i).isInitialized()) {
            return false;
          }
        }
        if (hasHumanReadableActivePeriod()) {
          if (!getHumanReadableActivePeriod().isInitialized()) {
            return false;
          }
        }
        if (hasAdditionalInformation()) {
          if (!getAdditionalInformation().isInitialized()) {
            return false;
          }
        }
        for (int i = 0; i < getAffectedStationsCount(); i++) {
          if (!getAffectedStations(i).isInitialized()) {
            return false;
          }
        }
        if (hasScreensSummary()) {
          if (!getScreensSummary().isInitialized()) {
            return false;
          }
        }
        return true;
      }

      @java.lang.Override public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int bitField0_;

      private long createdAt_;

      /**
       * <code>required uint64 created_at = 1;</code>
       */
      public boolean hasCreatedAt() {
        return ((bitField0_ & 0x00000001) != 0);
      }

      /**
       * <code>required uint64 created_at = 1;</code>
       */
      public long getCreatedAt() {
        return createdAt_;
      }

      /**
       * <code>required uint64 created_at = 1;</code>
       */
      public Builder setCreatedAt(long value) {
        bitField0_ |= 0x00000001;
        createdAt_ = value;
        onChanged();
        return this;
      }

      /**
       * <code>required uint64 created_at = 1;</code>
       */
      public Builder clearCreatedAt() {
        bitField0_ = (bitField0_ & ~0x00000001);
        createdAt_ = 0L;
        onChanged();
        return this;
      }

      private long updatedAt_;

      /**
       * <code>required uint64 updated_at = 2;</code>
       */
      public boolean hasUpdatedAt() {
        return ((bitField0_ & 0x00000002) != 0);
      }

      /**
       * <code>required uint64 updated_at = 2;</code>
       */
      public long getUpdatedAt() {
        return updatedAt_;
      }

      /**
       * <code>required uint64 updated_at = 2;</code>
       */
      public Builder setUpdatedAt(long value) {
        bitField0_ |= 0x00000002;
        updatedAt_ = value;
        onChanged();
        return this;
      }

      /**
       * <code>required uint64 updated_at = 2;</code>
       */
      public Builder clearUpdatedAt() {
        bitField0_ = (bitField0_ & ~0x00000002);
        updatedAt_ = 0L;
        onChanged();
        return this;
      }

      private java.lang.Object alertType_ = "";

      /**
       * <code>required string alert_type = 3;</code>
       */
      public boolean hasAlertType() {
        return ((bitField0_ & 0x00000004) != 0);
      }

      /**
       * <code>required string alert_type = 3;</code>
       */
      public java.lang.String getAlertType() {
        java.lang.Object ref = alertType_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            alertType_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }

      /**
       * <code>required string alert_type = 3;</code>
       */
      public com.google.protobuf.ByteString getAlertTypeBytes() {
        java.lang.Object ref = alertType_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
          alertType_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }

      /**
       * <code>required string alert_type = 3;</code>
       */
      public Builder setAlertType(java.lang.String value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000004;
        alertType_ = value;
        onChanged();
        return this;
      }

      /**
       * <code>required string alert_type = 3;</code>
       */
      public Builder clearAlertType() {
        bitField0_ = (bitField0_ & ~0x00000004);
        alertType_ = getDefaultInstance().getAlertType();
        onChanged();
        return this;
      }

      /**
       * <code>required string alert_type = 3;</code>
       */
      public Builder setAlertTypeBytes(com.google.protobuf.ByteString value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000004;
        alertType_ = value;
        onChanged();
        return this;
      }

      private java.util.List<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative> stationAlternative_ = java.util.Collections.emptyList();

      @java.lang.Override public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      private void ensureStationAlternativeIsMutable() {
        if (!((bitField0_ & 0x00000008) == 0x00000008)) {
          stationAlternative_ = new java.util.ArrayList<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative>(stationAlternative_);
          bitField0_ |= 0x00000008;
        }
      }

      @java.lang.Override public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }

      private com.google.protobuf.RepeatedFieldBuilder<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder> stationAlternativeBuilder_;

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public java.util.List<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative> getStationAlternativeList() {
        if (stationAlternativeBuilder_ == null) {
          return java.util.Collections.unmodifiableList(stationAlternative_);
        } else {
          return stationAlternativeBuilder_.getMessageList();
        }
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public int getStationAlternativeCount() {
        if (stationAlternativeBuilder_ == null) {
          return stationAlternative_.size();
        } else {
          return stationAlternativeBuilder_.getCount();
        }
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative getStationAlternative(int index) {
        if (stationAlternativeBuilder_ == null) {
          return stationAlternative_.get(index);
        } else {
          return stationAlternativeBuilder_.getMessage(index);
        }
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public Builder setStationAlternative(int index, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative value) {
        if (stationAlternativeBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureStationAlternativeIsMutable();
          stationAlternative_.set(index, value);
          onChanged();
        } else {
          stationAlternativeBuilder_.setMessage(index, value);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public Builder setStationAlternative(int index, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder builderForValue) {
        if (stationAlternativeBuilder_ == null) {
          ensureStationAlternativeIsMutable();
          stationAlternative_.set(index, builderForValue.build());
          onChanged();
        } else {
          stationAlternativeBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public Builder addStationAlternative(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative value) {
        if (stationAlternativeBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureStationAlternativeIsMutable();
          stationAlternative_.add(value);
          onChanged();
        } else {
          stationAlternativeBuilder_.addMessage(value);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public Builder addStationAlternative(int index, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative value) {
        if (stationAlternativeBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureStationAlternativeIsMutable();
          stationAlternative_.add(index, value);
          onChanged();
        } else {
          stationAlternativeBuilder_.addMessage(index, value);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public Builder addStationAlternative(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder builderForValue) {
        if (stationAlternativeBuilder_ == null) {
          ensureStationAlternativeIsMutable();
          stationAlternative_.add(builderForValue.build());
          onChanged();
        } else {
          stationAlternativeBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public Builder addStationAlternative(int index, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder builderForValue) {
        if (stationAlternativeBuilder_ == null) {
          ensureStationAlternativeIsMutable();
          stationAlternative_.add(index, builderForValue.build());
          onChanged();
        } else {
          stationAlternativeBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public Builder addAllStationAlternative(java.lang.Iterable<? extends com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative> values) {
        if (stationAlternativeBuilder_ == null) {
          ensureStationAlternativeIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(values, stationAlternative_);
          onChanged();
        } else {
          stationAlternativeBuilder_.addAllMessages(values);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public Builder clearStationAlternative() {
        if (stationAlternativeBuilder_ == null) {
          stationAlternative_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000008);
          onChanged();
        } else {
          stationAlternativeBuilder_.clear();
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public Builder removeStationAlternative(int index) {
        if (stationAlternativeBuilder_ == null) {
          ensureStationAlternativeIsMutable();
          stationAlternative_.remove(index);
          onChanged();
        } else {
          stationAlternativeBuilder_.remove(index);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder getStationAlternativeBuilder(int index) {
        return getStationAlternativeFieldBuilder().getBuilder(index);
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder getStationAlternativeOrBuilder(int index) {
        if (stationAlternativeBuilder_ == null) {
          return stationAlternative_.get(index);
        } else {
          return stationAlternativeBuilder_.getMessageOrBuilder(index);
        }
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public java.util.List<? extends com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder> getStationAlternativeOrBuilderList() {
        if (stationAlternativeBuilder_ != null) {
          return stationAlternativeBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(stationAlternative_);
        }
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder addStationAlternativeBuilder() {
        return getStationAlternativeFieldBuilder().addBuilder(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.getDefaultInstance());
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder addStationAlternativeBuilder(int index) {
        return getStationAlternativeFieldBuilder().addBuilder(index, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.getDefaultInstance());
      }

      /**
       * <code>repeated .transit_realtime.MercuryStationAlternative station_alternative = 4;</code>
       */
      public java.util.List<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder> getStationAlternativeBuilderList() {
        return getStationAlternativeFieldBuilder().getBuilderList();
      }

      private com.google.protobuf.RepeatedFieldBuilder<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder> getStationAlternativeFieldBuilder() {
        if (stationAlternativeBuilder_ == null) {
          stationAlternativeBuilder_ = new com.google.protobuf.RepeatedFieldBuilder<com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternative.Builder, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryStationAlternativeOrBuilder>(stationAlternative_, ((bitField0_ & 0x00000008) == 0x00000008), getParentForChildren(), isClean());
          stationAlternative_ = null;
        }
        return stationAlternativeBuilder_;
      }

      private com.google.protobuf.LazyStringList servicePlanNumber_ = com.google.protobuf.LazyStringArrayList.EMPTY;

      private void ensureServicePlanNumberIsMutable() {
        if (!((bitField0_ & 0x00000010) == 0x00000010)) {
          servicePlanNumber_ = new com.google.protobuf.LazyStringArrayList(servicePlanNumber_);
          bitField0_ |= 0x00000010;
        }
      }

      /**
       * <code>repeated string service_plan_number = 5;</code>
       */
      public com.google.protobuf.ProtocolStringList getServicePlanNumberList() {
        return servicePlanNumber_.getUnmodifiableView();
      }

      /**
       * <code>repeated string service_plan_number = 5;</code>
       */
      public int getServicePlanNumberCount() {
        return servicePlanNumber_.size();
      }

      /**
       * <code>repeated string service_plan_number = 5;</code>
       */
      public java.lang.String getServicePlanNumber(int index) {
        return servicePlanNumber_.get(index);
      }

      /**
       * <code>repeated string service_plan_number = 5;</code>
       */
      public com.google.protobuf.ByteString getServicePlanNumberBytes(int index) {
        return servicePlanNumber_.getByteString(index);
      }

      /**
       * <code>repeated string service_plan_number = 5;</code>
       */
      public Builder setServicePlanNumber(int index, java.lang.String value) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureServicePlanNumberIsMutable();
        servicePlanNumber_.set(index, value);
        onChanged();
        return this;
      }

      /**
       * <code>repeated string service_plan_number = 5;</code>
       */
      public Builder addServicePlanNumber(java.lang.String value) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureServicePlanNumberIsMutable();
        servicePlanNumber_.add(value);
        onChanged();
        return this;
      }

      /**
       * <code>repeated string service_plan_number = 5;</code>
       */
      public Builder addAllServicePlanNumber(java.lang.Iterable<java.lang.String> values) {
        ensureServicePlanNumberIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(values, servicePlanNumber_);
        onChanged();
        return this;
      }

      /**
       * <code>repeated string service_plan_number = 5;</code>
       */
      public Builder clearServicePlanNumber() {
        servicePlanNumber_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000010);
        onChanged();
        return this;
      }

      /**
       * <code>repeated string service_plan_number = 5;</code>
       */
      public Builder addServicePlanNumberBytes(com.google.protobuf.ByteString value) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureServicePlanNumberIsMutable();
        servicePlanNumber_.add(value);
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList generalOrderNumber_ = com.google.protobuf.LazyStringArrayList.EMPTY;

      private void ensureGeneralOrderNumberIsMutable() {
        if (!((bitField0_ & 0x00000020) == 0x00000020)) {
          generalOrderNumber_ = new com.google.protobuf.LazyStringArrayList(generalOrderNumber_);
          bitField0_ |= 0x00000020;
        }
      }

      /**
       * <code>repeated string general_order_number = 6;</code>
       */
      public com.google.protobuf.ProtocolStringList getGeneralOrderNumberList() {
        return generalOrderNumber_.getUnmodifiableView();
      }

      /**
       * <code>repeated string general_order_number = 6;</code>
       */
      public int getGeneralOrderNumberCount() {
        return generalOrderNumber_.size();
      }

      /**
       * <code>repeated string general_order_number = 6;</code>
       */
      public java.lang.String getGeneralOrderNumber(int index) {
        return generalOrderNumber_.get(index);
      }

      /**
       * <code>repeated string general_order_number = 6;</code>
       */
      public com.google.protobuf.ByteString getGeneralOrderNumberBytes(int index) {
        return generalOrderNumber_.getByteString(index);
      }

      /**
       * <code>repeated string general_order_number = 6;</code>
       */
      public Builder setGeneralOrderNumber(int index, java.lang.String value) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureGeneralOrderNumberIsMutable();
        generalOrderNumber_.set(index, value);
        onChanged();
        return this;
      }

      /**
       * <code>repeated string general_order_number = 6;</code>
       */
      public Builder addGeneralOrderNumber(java.lang.String value) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureGeneralOrderNumberIsMutable();
        generalOrderNumber_.add(value);
        onChanged();
        return this;
      }

      /**
       * <code>repeated string general_order_number = 6;</code>
       */
      public Builder addAllGeneralOrderNumber(java.lang.Iterable<java.lang.String> values) {
        ensureGeneralOrderNumberIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(values, generalOrderNumber_);
        onChanged();
        return this;
      }

      /**
       * <code>repeated string general_order_number = 6;</code>
       */
      public Builder clearGeneralOrderNumber() {
        generalOrderNumber_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000020);
        onChanged();
        return this;
      }

      /**
       * <code>repeated string general_order_number = 6;</code>
       */
      public Builder addGeneralOrderNumberBytes(com.google.protobuf.ByteString value) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureGeneralOrderNumberIsMutable();
        generalOrderNumber_.add(value);
        onChanged();
        return this;
      }

      private long displayBeforeActive_;

      /**
       * <code>optional uint64 display_before_active = 7;</code>
       *
       * <pre>
       * A time interval, in seconds, indicating how long before each active period
       * consumers should display this alert. A value of 3600 here, for example,
       * suggests that this alert should be displayed 1 hour before each active
       * period. Consumers may choose to ignore this recommendation and display (or
       * not display) alerts based on their own logic if they so choose.
       * </pre>
       */
      public boolean hasDisplayBeforeActive() {
        return ((bitField0_ & 0x00000040) == 0x00000040);
      }

      /**
       * <code>optional uint64 display_before_active = 7;</code>
       *
       * <pre>
       * A time interval, in seconds, indicating how long before each active period
       * consumers should display this alert. A value of 3600 here, for example,
       * suggests that this alert should be displayed 1 hour before each active
       * period. Consumers may choose to ignore this recommendation and display (or
       * not display) alerts based on their own logic if they so choose.
       * </pre>
       */
      public long getDisplayBeforeActive() {
        return displayBeforeActive_;
      }

      /**
       * <code>optional uint64 display_before_active = 7;</code>
       *
       * <pre>
       * A time interval, in seconds, indicating how long before each active period
       * consumers should display this alert. A value of 3600 here, for example,
       * suggests that this alert should be displayed 1 hour before each active
       * period. Consumers may choose to ignore this recommendation and display (or
       * not display) alerts based on their own logic if they so choose.
       * </pre>
       */
      public Builder setDisplayBeforeActive(long value) {
        bitField0_ |= 0x00000040;
        displayBeforeActive_ = value;
        onChanged();
        return this;
      }

      /**
       * <code>optional uint64 display_before_active = 7;</code>
       *
       * <pre>
       * A time interval, in seconds, indicating how long before each active period
       * consumers should display this alert. A value of 3600 here, for example,
       * suggests that this alert should be displayed 1 hour before each active
       * period. Consumers may choose to ignore this recommendation and display (or
       * not display) alerts based on their own logic if they so choose.
       * </pre>
       */
      public Builder clearDisplayBeforeActive() {
        bitField0_ = (bitField0_ & ~0x00000040);
        displayBeforeActive_ = 0L;
        onChanged();
        return this;
      }

      private com.google.transit.realtime.GtfsRealtime.TranslatedString humanReadableActivePeriod_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();

      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder> humanReadableActivePeriodBuilder_;

      /**
       * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
       *
       * <pre>
       * A human-friendly string that summarizes all active periods for this Alert,
       * i.e. "Sundays in May from 10:45pm to midnight"
       * </pre>
       */
      public boolean hasHumanReadableActivePeriod() {
        return ((bitField0_ & 0x00000080) == 0x00000080);
      }

      /**
       * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
       *
       * <pre>
       * A human-friendly string that summarizes all active periods for this Alert,
       * i.e. "Sundays in May from 10:45pm to midnight"
       * </pre>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedString getHumanReadableActivePeriod() {
        if (humanReadableActivePeriodBuilder_ == null) {
          return humanReadableActivePeriod_;
        } else {
          return humanReadableActivePeriodBuilder_.getMessage();
        }
      }

      /**
       * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
       *
       * <pre>
       * A human-friendly string that summarizes all active periods for this Alert,
       * i.e. "Sundays in May from 10:45pm to midnight"
       * </pre>
       */
      public Builder setHumanReadableActivePeriod(com.google.transit.realtime.GtfsRealtime.TranslatedString value) {
        if (humanReadableActivePeriodBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          humanReadableActivePeriod_ = value;
          onChanged();
        } else {
          humanReadableActivePeriodBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000080;
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
       *
       * <pre>
       * A human-friendly string that summarizes all active periods for this Alert,
       * i.e. "Sundays in May from 10:45pm to midnight"
       * </pre>
       */
      public Builder setHumanReadableActivePeriod(com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder builderForValue) {
        if (humanReadableActivePeriodBuilder_ == null) {
          humanReadableActivePeriod_ = builderForValue.build();
          onChanged();
        } else {
          humanReadableActivePeriodBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000080;
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
       *
       * <pre>
       * A human-friendly string that summarizes all active periods for this Alert,
       * i.e. "Sundays in May from 10:45pm to midnight"
       * </pre>
       */
      public Builder mergeHumanReadableActivePeriod(com.google.transit.realtime.GtfsRealtime.TranslatedString value) {
        if (humanReadableActivePeriodBuilder_ == null) {
          if (((bitField0_ & 0x00000080) == 0x00000080) && humanReadableActivePeriod_ != com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance()) {
            humanReadableActivePeriod_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.newBuilder(humanReadableActivePeriod_).mergeFrom(value).buildPartial();
          } else {
            humanReadableActivePeriod_ = value;
          }
          onChanged();
        } else {
          humanReadableActivePeriodBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000080;
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
       *
       * <pre>
       * A human-friendly string that summarizes all active periods for this Alert,
       * i.e. "Sundays in May from 10:45pm to midnight"
       * </pre>
       */
      public Builder clearHumanReadableActivePeriod() {
        if (humanReadableActivePeriodBuilder_ == null) {
          humanReadableActivePeriod_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
          onChanged();
        } else {
          humanReadableActivePeriodBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000080);
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
       *
       * <pre>
       * A human-friendly string that summarizes all active periods for this Alert,
       * i.e. "Sundays in May from 10:45pm to midnight"
       * </pre>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder getHumanReadableActivePeriodBuilder() {
        bitField0_ |= 0x00000080;
        onChanged();
        return getHumanReadableActivePeriodFieldBuilder().getBuilder();
      }

      /**
       * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
       *
       * <pre>
       * A human-friendly string that summarizes all active periods for this Alert,
       * i.e. "Sundays in May from 10:45pm to midnight"
       * </pre>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getHumanReadableActivePeriodOrBuilder() {
        if (humanReadableActivePeriodBuilder_ != null) {
          return humanReadableActivePeriodBuilder_.getMessageOrBuilder();
        } else {
          return humanReadableActivePeriod_;
        }
      }

      /**
       * <code>optional .transit_realtime.TranslatedString human_readable_active_period = 8;</code>
       *
       * <pre>
       * A human-friendly string that summarizes all active periods for this Alert,
       * i.e. "Sundays in May from 10:45pm to midnight"
       * </pre>
       */
      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder> getHumanReadableActivePeriodFieldBuilder() {
        if (humanReadableActivePeriodBuilder_ == null) {
          humanReadableActivePeriodBuilder_ = new com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder>(getHumanReadableActivePeriod(), getParentForChildren(), isClean());
          humanReadableActivePeriod_ = null;
        }
        return humanReadableActivePeriodBuilder_;
      }

      private com.google.transit.realtime.GtfsRealtime.TranslatedString additionalInformation_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();

      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder> additionalInformationBuilder_;

      /**
       * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
       */
      public boolean hasAdditionalInformation() {
        return ((bitField0_ & 0x00000100) == 0x00000100);
      }

      /**
       * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedString getAdditionalInformation() {
        if (additionalInformationBuilder_ == null) {
          return additionalInformation_;
        } else {
          return additionalInformationBuilder_.getMessage();
        }
      }

      /**
       * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
       */
      public Builder setAdditionalInformation(com.google.transit.realtime.GtfsRealtime.TranslatedString value) {
        if (additionalInformationBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          additionalInformation_ = value;
          onChanged();
        } else {
          additionalInformationBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000100;
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
       */
      public Builder setAdditionalInformation(com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder builderForValue) {
        if (additionalInformationBuilder_ == null) {
          additionalInformation_ = builderForValue.build();
          onChanged();
        } else {
          additionalInformationBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000100;
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
       */
      public Builder mergeAdditionalInformation(com.google.transit.realtime.GtfsRealtime.TranslatedString value) {
        if (additionalInformationBuilder_ == null) {
          if (((bitField0_ & 0x00000100) == 0x00000100) && additionalInformation_ != com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance()) {
            additionalInformation_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.newBuilder(additionalInformation_).mergeFrom(value).buildPartial();
          } else {
            additionalInformation_ = value;
          }
          onChanged();
        } else {
          additionalInformationBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000100;
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
       */
      public Builder clearAdditionalInformation() {
        if (additionalInformationBuilder_ == null) {
          additionalInformation_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
          onChanged();
        } else {
          additionalInformationBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000100);
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder getAdditionalInformationBuilder() {
        bitField0_ |= 0x00000100;
        onChanged();
        return getAdditionalInformationFieldBuilder().getBuilder();
      }

      /**
       * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getAdditionalInformationOrBuilder() {
        if (additionalInformationBuilder_ != null) {
          return additionalInformationBuilder_.getMessageOrBuilder();
        } else {
          return additionalInformation_;
        }
      }

      /**
       * <code>optional .transit_realtime.TranslatedString additional_information = 9;</code>
       */
      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder> getAdditionalInformationFieldBuilder() {
        if (additionalInformationBuilder_ == null) {
          additionalInformationBuilder_ = new com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder>(getAdditionalInformation(), getParentForChildren(), isClean());
          additionalInformation_ = null;
        }
        return additionalInformationBuilder_;
      }

      private long directionality_;

      /**
       * <code>optional uint64 directionality = 10;</code>
       */
      public boolean hasDirectionality() {
        return ((bitField0_ & 0x00000200) == 0x00000200);
      }

      /**
       * <code>optional uint64 directionality = 10;</code>
       */
      public long getDirectionality() {
        return directionality_;
      }

      /**
       * <code>optional uint64 directionality = 10;</code>
       */
      public Builder setDirectionality(long value) {
        bitField0_ |= 0x00000200;
        directionality_ = value;
        onChanged();
        return this;
      }

      /**
       * <code>optional uint64 directionality = 10;</code>
       */
      public Builder clearDirectionality() {
        bitField0_ = (bitField0_ & ~0x00000200);
        directionality_ = 0L;
        onChanged();
        return this;
      }

      private java.util.List<com.google.transit.realtime.GtfsRealtime.EntitySelector> affectedStations_ = java.util.Collections.emptyList();

      private void ensureAffectedStationsIsMutable() {
        if (!((bitField0_ & 0x00000400) == 0x00000400)) {
          affectedStations_ = new java.util.ArrayList<com.google.transit.realtime.GtfsRealtime.EntitySelector>(affectedStations_);
          bitField0_ |= 0x00000400;
        }
      }

      private com.google.protobuf.RepeatedFieldBuilder<com.google.transit.realtime.GtfsRealtime.EntitySelector, com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder, com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder> affectedStationsBuilder_;

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public java.util.List<com.google.transit.realtime.GtfsRealtime.EntitySelector> getAffectedStationsList() {
        if (affectedStationsBuilder_ == null) {
          return java.util.Collections.unmodifiableList(affectedStations_);
        } else {
          return affectedStationsBuilder_.getMessageList();
        }
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public int getAffectedStationsCount() {
        if (affectedStationsBuilder_ == null) {
          return affectedStations_.size();
        } else {
          return affectedStationsBuilder_.getCount();
        }
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.EntitySelector getAffectedStations(int index) {
        if (affectedStationsBuilder_ == null) {
          return affectedStations_.get(index);
        } else {
          return affectedStationsBuilder_.getMessage(index);
        }
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public Builder setAffectedStations(int index, com.google.transit.realtime.GtfsRealtime.EntitySelector value) {
        if (affectedStationsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureAffectedStationsIsMutable();
          affectedStations_.set(index, value);
          onChanged();
        } else {
          affectedStationsBuilder_.setMessage(index, value);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public Builder setAffectedStations(int index, com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder builderForValue) {
        if (affectedStationsBuilder_ == null) {
          ensureAffectedStationsIsMutable();
          affectedStations_.set(index, builderForValue.build());
          onChanged();
        } else {
          affectedStationsBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public Builder addAffectedStations(com.google.transit.realtime.GtfsRealtime.EntitySelector value) {
        if (affectedStationsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureAffectedStationsIsMutable();
          affectedStations_.add(value);
          onChanged();
        } else {
          affectedStationsBuilder_.addMessage(value);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public Builder addAffectedStations(int index, com.google.transit.realtime.GtfsRealtime.EntitySelector value) {
        if (affectedStationsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureAffectedStationsIsMutable();
          affectedStations_.add(index, value);
          onChanged();
        } else {
          affectedStationsBuilder_.addMessage(index, value);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public Builder addAffectedStations(com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder builderForValue) {
        if (affectedStationsBuilder_ == null) {
          ensureAffectedStationsIsMutable();
          affectedStations_.add(builderForValue.build());
          onChanged();
        } else {
          affectedStationsBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public Builder addAffectedStations(int index, com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder builderForValue) {
        if (affectedStationsBuilder_ == null) {
          ensureAffectedStationsIsMutable();
          affectedStations_.add(index, builderForValue.build());
          onChanged();
        } else {
          affectedStationsBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public Builder addAllAffectedStations(java.lang.Iterable<? extends com.google.transit.realtime.GtfsRealtime.EntitySelector> values) {
        if (affectedStationsBuilder_ == null) {
          ensureAffectedStationsIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(values, affectedStations_);
          onChanged();
        } else {
          affectedStationsBuilder_.addAllMessages(values);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public Builder clearAffectedStations() {
        if (affectedStationsBuilder_ == null) {
          affectedStations_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000400);
          onChanged();
        } else {
          affectedStationsBuilder_.clear();
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public Builder removeAffectedStations(int index) {
        if (affectedStationsBuilder_ == null) {
          ensureAffectedStationsIsMutable();
          affectedStations_.remove(index);
          onChanged();
        } else {
          affectedStationsBuilder_.remove(index);
        }
        return this;
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder getAffectedStationsBuilder(int index) {
        return getAffectedStationsFieldBuilder().getBuilder(index);
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder getAffectedStationsOrBuilder(int index) {
        if (affectedStationsBuilder_ == null) {
          return affectedStations_.get(index);
        } else {
          return affectedStationsBuilder_.getMessageOrBuilder(index);
        }
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public java.util.List<? extends com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder> getAffectedStationsOrBuilderList() {
        if (affectedStationsBuilder_ != null) {
          return affectedStationsBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(affectedStations_);
        }
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder addAffectedStationsBuilder() {
        return getAffectedStationsFieldBuilder().addBuilder(com.google.transit.realtime.GtfsRealtime.EntitySelector.getDefaultInstance());
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder addAffectedStationsBuilder(int index) {
        return getAffectedStationsFieldBuilder().addBuilder(index, com.google.transit.realtime.GtfsRealtime.EntitySelector.getDefaultInstance());
      }

      /**
       * <code>repeated .transit_realtime.EntitySelector affected_stations = 11;</code>
       */
      public java.util.List<com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder> getAffectedStationsBuilderList() {
        return getAffectedStationsFieldBuilder().getBuilderList();
      }

      private com.google.protobuf.RepeatedFieldBuilder<com.google.transit.realtime.GtfsRealtime.EntitySelector, com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder, com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder> getAffectedStationsFieldBuilder() {
        if (affectedStationsBuilder_ == null) {
          affectedStationsBuilder_ = new com.google.protobuf.RepeatedFieldBuilder<com.google.transit.realtime.GtfsRealtime.EntitySelector, com.google.transit.realtime.GtfsRealtime.EntitySelector.Builder, com.google.transit.realtime.GtfsRealtime.EntitySelectorOrBuilder>(affectedStations_, ((bitField0_ & 0x00000400) == 0x00000400), getParentForChildren(), isClean());
          affectedStations_ = null;
        }
        return affectedStationsBuilder_;
      }

      private com.google.transit.realtime.GtfsRealtime.TranslatedString screensSummary_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();

      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder> screensSummaryBuilder_;

      /**
       * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
       */
      public boolean hasScreensSummary() {
        return ((bitField0_ & 0x00000800) == 0x00000800);
      }

      /**
       * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedString getScreensSummary() {
        if (screensSummaryBuilder_ == null) {
          return screensSummary_;
        } else {
          return screensSummaryBuilder_.getMessage();
        }
      }

      /**
       * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
       */
      public Builder setScreensSummary(com.google.transit.realtime.GtfsRealtime.TranslatedString value) {
        if (screensSummaryBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          screensSummary_ = value;
          onChanged();
        } else {
          screensSummaryBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000800;
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
       */
      public Builder setScreensSummary(com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder builderForValue) {
        if (screensSummaryBuilder_ == null) {
          screensSummary_ = builderForValue.build();
          onChanged();
        } else {
          screensSummaryBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000800;
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
       */
      public Builder mergeScreensSummary(com.google.transit.realtime.GtfsRealtime.TranslatedString value) {
        if (screensSummaryBuilder_ == null) {
          if (((bitField0_ & 0x00000800) == 0x00000800) && screensSummary_ != com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance()) {
            screensSummary_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.newBuilder(screensSummary_).mergeFrom(value).buildPartial();
          } else {
            screensSummary_ = value;
          }
          onChanged();
        } else {
          screensSummaryBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000800;
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
       */
      public Builder clearScreensSummary() {
        if (screensSummaryBuilder_ == null) {
          screensSummary_ = com.google.transit.realtime.GtfsRealtime.TranslatedString.getDefaultInstance();
          onChanged();
        } else {
          screensSummaryBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000800);
        return this;
      }

      /**
       * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder getScreensSummaryBuilder() {
        bitField0_ |= 0x00000800;
        onChanged();
        return getScreensSummaryFieldBuilder().getBuilder();
      }

      /**
       * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
       */
      public com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder getScreensSummaryOrBuilder() {
        if (screensSummaryBuilder_ != null) {
          return screensSummaryBuilder_.getMessageOrBuilder();
        } else {
          return screensSummary_;
        }
      }

      /**
       * <code>optional .transit_realtime.TranslatedString screens_summary = 12;</code>
       */
      private com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder> getScreensSummaryFieldBuilder() {
        if (screensSummaryBuilder_ == null) {
          screensSummaryBuilder_ = new com.google.protobuf.SingleFieldBuilder<com.google.transit.realtime.GtfsRealtime.TranslatedString, com.google.transit.realtime.GtfsRealtime.TranslatedString.Builder, com.google.transit.realtime.GtfsRealtime.TranslatedStringOrBuilder>(getScreensSummary(), getParentForChildren(), isClean());
          screensSummary_ = null;
        }
        return screensSummaryBuilder_;
      }
    }

    static {
      DEFAULT_INSTANCE = new com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert();
    }

    public static com.google.protobuf.Parser<MercuryAlert> parser() {
      return PARSER;
    }
  }

  public interface MercuryEntitySelectorOrBuilder extends com.google.protobuf.MessageOrBuilder {
    /**
     * <code>required string sort_order = 1;</code>
     *
     * <pre>
     * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
     * </pre>
     */
    boolean hasSortOrder();

    /**
     * <code>required string sort_order = 1;</code>
     *
     * <pre>
     * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
     * </pre>
     */
    java.lang.String getSortOrder();

    /**
     * <code>required string sort_order = 1;</code>
     *
     * <pre>
     * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
     * </pre>
     */
    com.google.protobuf.ByteString getSortOrderBytes();
  }

  public static final class MercuryEntitySelector extends com.google.protobuf.GeneratedMessageV3 implements MercuryEntitySelectorOrBuilder {
    private MercuryEntitySelector(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }

    private static final long serialVersionUID = 0L;

    private MercuryEntitySelector() {
      sortOrder_ = "";
    }

    @java.lang.Override @SuppressWarnings(value = { "unused" }) protected java.lang.Object newInstance(UnusedPrivateParameter unused) {
      return new MercuryEntitySelector();
    }

    private static final com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector DEFAULT_INSTANCE;

    @java.lang.Override public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
      return this.unknownFields;
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private MercuryEntitySelector(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
            done = true;
            break;
            case 10:
            {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              sortOrder_ = bs;
              break;
            }
            default:
            {
              if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }

    @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryEntitySelector_descriptor;
    }

    @java.lang.Override protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryEntitySelector_fieldAccessorTable.ensureFieldAccessorsInitialized(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.Builder.class);
    }

    @java.lang.Deprecated public static final com.google.protobuf.Parser<MercuryEntitySelector> PARSER = new com.google.protobuf.AbstractParser<MercuryEntitySelector>() {
      @java.lang.Override public MercuryEntitySelector parsePartialFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
        return new MercuryEntitySelector(input, extensionRegistry);
      }
    };

    public enum Priority implements com.google.protobuf.ProtocolMessageEnum {
      PRIORITY_NO_SCHEDULED_SERVICE(0),
      PRIORITY_ESSENTIAL_SERVICE(1, 1),
      PRIORITY_WEEKEND_SERVICE(2, 2),
      PRIORITY_WEEKDAY_SERVICE(3, 3),
      PRIORITY_SUNDAY_SCHEDULE(4, 4),
      PRIORITY_SATURDAY_SCHEDULE(5, 5),
      PRIORITY_HOLIDAY_SERVICE(6, 6),
      PRIORITY_BUSING(7, 7),
      PRIORITY_EXTRA_SERVICE(9, 9),
      PRIORITY_PLANNED_DETOUR(8, 8),
      PRIORITY_PLANNED_WORK(10, 10),
      PRIORITY_ON_OR_CLOSE(11, 11),
      PRIORITY_SLOW_SPEEDS(12, 12),
      PRIORITY_SOME_DELAYS(13, 13),
      PRIORITY_SPECIAL_EVENT(14, 14),
      PRIORITY_CROWDING(15, 15),
      PRIORITY_STATIONS_SKIPPED(16, 16),
      PRIORITY_DELAYS(17, 17),
      PRIORITY_EXPRESS_TO_LOCAL(18, 18),
      PRIORITY_SOME_REROUTES(19, 19),
      PRIORITY_LOCAL_TO_EXPRESS(20, 20),
      PRIORITY_DETOURS(21, 21),
      PRIORITY_SERVICE_CHANGE(22, 22),
      PRIORITY_TRAINS_REROUTED(23, 23),
      PRIORITY_PART_SUSPENDED(24, 24),
      PRIORITY_MULTIPLE_IMPACTS(25, 25),
      PRIORITY_SUSPENDED(26, 26)
      ;

      /**
       * <code>PRIORITY_NO_SCHEDULED_SERVICE = 0;</code>
       */
      public static final int PRIORITY_NO_SCHEDULED_SERVICE_VALUE = 0;

      /**
       * <code>PRIORITY_ESSENTIAL_SERVICE = 1;</code>
       */
      public static final int PRIORITY_ESSENTIAL_SERVICE_VALUE = 1;

      /**
       * <code>PRIORITY_WEEKEND_SERVICE = 2;</code>
       */
      public static final int PRIORITY_WEEKEND_SERVICE_VALUE = 2;

      /**
       * <code>PRIORITY_WEEKDAY_SERVICE = 3;</code>
       */
      public static final int PRIORITY_WEEKDAY_SERVICE_VALUE = 3;

      /**
       * <code>PRIORITY_SUNDAY_SCHEDULE = 4;</code>
       */
      public static final int PRIORITY_SUNDAY_SCHEDULE_VALUE = 4;

      /**
       * <code>PRIORITY_SATURDAY_SCHEDULE = 5;</code>
       */
      public static final int PRIORITY_SATURDAY_SCHEDULE_VALUE = 5;

      /**
       * <code>PRIORITY_HOLIDAY_SERVICE = 6;</code>
       */
      public static final int PRIORITY_HOLIDAY_SERVICE_VALUE = 6;

      /**
       * <code>PRIORITY_BUSING = 7;</code>
       */
      public static final int PRIORITY_BUSING_VALUE = 7;

      /**
       * <code>PRIORITY_EXTRA_SERVICE = 5;</code>
       */
      public static final int PRIORITY_EXTRA_SERVICE_VALUE = 9;

      /**
       * <code>PRIORITY_PLANNED_DETOUR = 8;</code>
       */
      public static final int PRIORITY_PLANNED_DETOUR_VALUE = 8;

      /**
       * <code>PRIORITY_PLANNED_WORK = 10;</code>
       */
      public static final int PRIORITY_PLANNED_WORK_VALUE = 10;

      /**
       * <code>PRIORITY_ON_OR_CLOSE = 11;</code>
       */
      public static final int PRIORITY_ON_OR_CLOSE_VALUE = 11;

      /**
       * <code>PRIORITY_SLOW_SPEEDS = 12;</code>
       */
      public static final int PRIORITY_SLOW_SPEEDS_VALUE = 12;

      /**
       * <code>PRIORITY_SOME_DELAYS = 13;</code>
       */
      public static final int PRIORITY_SOME_DELAYS_VALUE = 13;

      /**
       * <code>PRIORITY_SPECIAL_EVENT = 14;</code>
       */
      public static final int PRIORITY_SPECIAL_EVENT_VALUE = 14;

      /**
       * <code>PRIORITY_CROWDING = 15;</code>
       */
      public static final int PRIORITY_CROWDING_VALUE = 15;

      /**
       * <code>PRIORITY_STATIONS_SKIPPED = 16;</code>
       */
      public static final int PRIORITY_STATIONS_SKIPPED_VALUE = 16;

      /**
       * <code>PRIORITY_DELAYS = 17;</code>
       */
      public static final int PRIORITY_DELAYS_VALUE = 17;

      /**
       * <code>PRIORITY_EXPRESS_TO_LOCAL = 18;</code>
       */
      public static final int PRIORITY_EXPRESS_TO_LOCAL_VALUE = 18;

      /**
       * <code>PRIORITY_SOME_REROUTES = 19;</code>
       */
      public static final int PRIORITY_SOME_REROUTES_VALUE = 19;

      /**
       * <code>PRIORITY_LOCAL_TO_EXPRESS = 20;</code>
       */
      public static final int PRIORITY_LOCAL_TO_EXPRESS_VALUE = 20;

      /**
       * <code>PRIORITY_DETOURS = 21;</code>
       */
      public static final int PRIORITY_DETOURS_VALUE = 21;

      /**
       * <code>PRIORITY_SERVICE_CHANGE = 22;</code>
       */
      public static final int PRIORITY_SERVICE_CHANGE_VALUE = 22;

      /**
       * <code>PRIORITY_TRAINS_REROUTED = 23;</code>
       */
      public static final int PRIORITY_TRAINS_REROUTED_VALUE = 23;

      /**
       * <code>PRIORITY_PART_SUSPENDED = 24;</code>
       */
      public static final int PRIORITY_PART_SUSPENDED_VALUE = 24;

      /**
       * <code>PRIORITY_MULTIPLE_IMPACTS = 25;</code>
       */
      public static final int PRIORITY_MULTIPLE_IMPACTS_VALUE = 25;

      /**
       * <code>PRIORITY_SUSPENDED = 26;</code>
       */
      public static final int PRIORITY_SUSPENDED_VALUE = 26;

      public final int getNumber() {
        return value;
      }

      @java.lang.Deprecated public static Priority valueOf(int value) {

<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
        switch (value) {
          case 0:
          return PRIORITY_NO_SCHEDULED_SERVICE;
          case 1:
          return PRIORITY_ESSENTIAL_SERVICE;
          case 2:
          return PRIORITY_WEEKEND_SERVICE;
          case 3:
          return PRIORITY_WEEKDAY_SERVICE;
          case 4:
          return PRIORITY_SUNDAY_SCHEDULE;
          case 5:
          return PRIORITY_SATURDAY_SCHEDULE;
          case 6:
          return PRIORITY_HOLIDAY_SERVICE;
          case 7:
          return PRIORITY_BUSING;
          case 8:
          return PRIORITY_PLANNED_DETOUR;
          case 9:
          return PRIORITY_EXTRA_SERVICE;
          case 10:
          return PRIORITY_PLANNED_WORK;
          case 11:
          return PRIORITY_ON_OR_CLOSE;
          case 12:
          return PRIORITY_SLOW_SPEEDS;
          case 13:
          return PRIORITY_SOME_DELAYS;
          case 14:
          return PRIORITY_SPECIAL_EVENT;
          case 15:
          return PRIORITY_CROWDING;
          case 16:
          return PRIORITY_STATIONS_SKIPPED;
          case 17:
          return PRIORITY_DELAYS;
          case 18:
          return PRIORITY_EXPRESS_TO_LOCAL;
          case 19:
          return PRIORITY_SOME_REROUTES;
          case 20:
          return PRIORITY_LOCAL_TO_EXPRESS;
          case 21:
          return PRIORITY_DETOURS;
          case 22:
          return PRIORITY_SERVICE_CHANGE;
          case 23:
          return PRIORITY_TRAINS_REROUTED;
          case 24:
          return PRIORITY_PART_SUSPENDED;
          case 25:
          return PRIORITY_MULTIPLE_IMPACTS;
          case 26:
          return PRIORITY_SUSPENDED;
          default:
          return null;
        }
=======
        return forNumber(value);
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
      }

      /**
       * @param value The numeric wire value of the corresponding enum entry.
       * @return The enum associated with the given numeric wire value.
       */
      public static Priority forNumber(int value) {
        switch (value) {
          case 1:
          return PRIORITY_NO_SCHEDULED_SERVICE;
          case 2:
          return PRIORITY_SUNDAY_SCHEDULE;
          case 3:
          return PRIORITY_SATURDAY_SCHEDULE;
          case 4:
          return PRIORITY_HOLIDAY_SERVICE;
          case 5:
          return PRIORITY_EXTRA_SERVICE;
          case 6:
          return PRIORITY_PLANNED_WORK;
          case 7:
          return PRIORITY_ON_OR_CLOSE;
          case 8:
          return PRIORITY_SLOW_SPEEDS;
          case 9:
          return PRIORITY_SOME_DELAYS;
          case 10:
          return PRIORITY_SPECIAL_EVENT;
          case 11:
          return PRIORITY_STATIONS_SKIPPED;
          case 12:
          return PRIORITY_DELAYS;
          case 13:
          return PRIORITY_EXPRESS_TO_LOCAL;
          case 14:
          return PRIORITY_SOME_REROUTES;
          case 15:
          return PRIORITY_LOCAL_TO_EXPRESS;
          case 16:
          return PRIORITY_SERVICE_CHANGE;
          case 17:
          return PRIORITY_TRAINS_REROUTED;
          case 18:
          return PRIORITY_PART_SUSPENDED;
          case 19:
          return PRIORITY_MULTIPLE_IMPACTS;
          case 20:
          return PRIORITY_SUSPENDED;
          case 21:
          return PRIORITY_BUSING;
          default:
          return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<Priority> internalGetValueMap() {
        return internalValueMap;
      }

      private static final com.google.protobuf.Internal.EnumLiteMap<Priority> internalValueMap = new com.google.protobuf.Internal.EnumLiteMap<Priority>() {
        public Priority findValueByNumber(int number) {
          return Priority.forNumber(number);
        }
      };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor getValueDescriptor() {
        return getDescriptor().getValues().get(ordinal());
      }

      public final com.google.protobuf.Descriptors.EnumDescriptor getDescriptorForType() {
        return getDescriptor();
      }

      public static final com.google.protobuf.Descriptors.EnumDescriptor getDescriptor() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.getDescriptor().getEnumTypes().get(0);
      }

      private static final Priority[] VALUES = values();

      public static Priority valueOf(com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException("EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int value;

      private Priority(int value) {
        this.value = value;
      }
    }


<<<<<<< Unknown file: This is a bug in JDime.
=======
    public enum NyctBusPriority implements com.google.protobuf.ProtocolMessageEnum {
      NYCT_BUS_PRIORITY_NO_SCHEDULED_SERVICE(1),
      NYCT_BUS_PRIORITY_SUNDAY_SCHEDULE(2),
      NYCT_BUS_PRIORITY_SATURDAY_SCHEDULE(3),
      NYCT_BUS_PRIORITY_HOLIDAY_SERVICE(4),
      NYCT_BUS_PRIORITY_PLANNED_DETOUR(5),
      NYCT_BUS_PRIORITY_EXTRA_SERVICE(6),
      NYCT_BUS_PRIORITY_PLANNED_WORK(7),
      NYCT_BUS_PRIORITY_SPECIAL_EVENT(11),
      NYCT_BUS_PRIORITY_DELAYS(13),
      NYCT_BUS_PRIORITY_DETOURS(16),
      NYCT_BUS_PRIORITY_SERVICE_CHANGE(18),
      NYCT_BUS_PRIORITY_SUSPENDED(22)
      ;

      /**
       * <code>NYCT_BUS_PRIORITY_NO_SCHEDULED_SERVICE = 1;</code>
       */
      public static final int NYCT_BUS_PRIORITY_NO_SCHEDULED_SERVICE_VALUE = 1;

      /**
       * <code>NYCT_BUS_PRIORITY_SUNDAY_SCHEDULE = 2;</code>
       */
      public static final int NYCT_BUS_PRIORITY_SUNDAY_SCHEDULE_VALUE = 2;

      /**
       * <code>NYCT_BUS_PRIORITY_SATURDAY_SCHEDULE = 3;</code>
       */
      public static final int NYCT_BUS_PRIORITY_SATURDAY_SCHEDULE_VALUE = 3;

      /**
       * <code>NYCT_BUS_PRIORITY_HOLIDAY_SERVICE = 4;</code>
       */
      public static final int NYCT_BUS_PRIORITY_HOLIDAY_SERVICE_VALUE = 4;

      /**
       * <code>NYCT_BUS_PRIORITY_PLANNED_DETOUR = 5;</code>
       */
      public static final int NYCT_BUS_PRIORITY_PLANNED_DETOUR_VALUE = 5;

      /**
       * <code>NYCT_BUS_PRIORITY_EXTRA_SERVICE = 6;</code>
       */
      public static final int NYCT_BUS_PRIORITY_EXTRA_SERVICE_VALUE = 6;

      /**
       * <code>NYCT_BUS_PRIORITY_PLANNED_WORK = 7;</code>
       */
      public static final int NYCT_BUS_PRIORITY_PLANNED_WORK_VALUE = 7;

      /**
       * <code>NYCT_BUS_PRIORITY_SPECIAL_EVENT = 11;</code>
       */
      public static final int NYCT_BUS_PRIORITY_SPECIAL_EVENT_VALUE = 11;

      /**
       * <code>NYCT_BUS_PRIORITY_DELAYS = 13;</code>
       */
      public static final int NYCT_BUS_PRIORITY_DELAYS_VALUE = 13;

      /**
       * <code>NYCT_BUS_PRIORITY_DETOURS = 16;</code>
       */
      public static final int NYCT_BUS_PRIORITY_DETOURS_VALUE = 16;

      /**
       * <code>NYCT_BUS_PRIORITY_SERVICE_CHANGE = 18;</code>
       */
      public static final int NYCT_BUS_PRIORITY_SERVICE_CHANGE_VALUE = 18;

      /**
       * <code>NYCT_BUS_PRIORITY_SUSPENDED = 22;</code>
       */
      public static final int NYCT_BUS_PRIORITY_SUSPENDED_VALUE = 22;

      public final int getNumber() {
        return value;
      }

      /**
       * @param value The numeric wire value of the corresponding enum entry.
       * @return The enum associated with the given numeric wire value.
       * @deprecated Use {@link #forNumber(int)} instead.
       */
      @java.lang.Deprecated public static NyctBusPriority valueOf(int value) {
        return forNumber(value);
      }

      /**
       * @param value The numeric wire value of the corresponding enum entry.
       * @return The enum associated with the given numeric wire value.
       */
      public static NyctBusPriority forNumber(int value) {
        switch (value) {
          case 1:
          return NYCT_BUS_PRIORITY_NO_SCHEDULED_SERVICE;
          case 2:
          return NYCT_BUS_PRIORITY_SUNDAY_SCHEDULE;
          case 3:
          return NYCT_BUS_PRIORITY_SATURDAY_SCHEDULE;
          case 4:
          return NYCT_BUS_PRIORITY_HOLIDAY_SERVICE;
          case 5:
          return NYCT_BUS_PRIORITY_PLANNED_DETOUR;
          case 6:
          return NYCT_BUS_PRIORITY_EXTRA_SERVICE;
          case 7:
          return NYCT_BUS_PRIORITY_PLANNED_WORK;
          case 11:
          return NYCT_BUS_PRIORITY_SPECIAL_EVENT;
          case 13:
          return NYCT_BUS_PRIORITY_DELAYS;
          case 16:
          return NYCT_BUS_PRIORITY_DETOURS;
          case 18:
          return NYCT_BUS_PRIORITY_SERVICE_CHANGE;
          case 22:
          return NYCT_BUS_PRIORITY_SUSPENDED;
          default:
          return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<NyctBusPriority> internalGetValueMap() {
        return internalValueMap;
      }

      private static final com.google.protobuf.Internal.EnumLiteMap<NyctBusPriority> internalValueMap = new com.google.protobuf.Internal.EnumLiteMap<NyctBusPriority>() {
        public NyctBusPriority findValueByNumber(int number) {
          return NyctBusPriority.forNumber(number);
        }
      };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor getValueDescriptor() {
        return getDescriptor().getValues().get(ordinal());
      }

      public final com.google.protobuf.Descriptors.EnumDescriptor getDescriptorForType() {
        return getDescriptor();
      }

      public static final com.google.protobuf.Descriptors.EnumDescriptor getDescriptor() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.getDescriptor().getEnumTypes().get(1);
      }

      private static final NyctBusPriority[] VALUES = values();

      public static NyctBusPriority valueOf(com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException("EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int value;

      private NyctBusPriority(int value) {
        this.value = value;
      }
    }
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java


    @java.lang.Override public com.google.protobuf.Parser<MercuryEntitySelector> getParserForType() {
      return PARSER;
    }

    private int bitField0_;

    public static final int SORT_ORDER_FIELD_NUMBER = 1;

    private volatile java.lang.Object sortOrder_;

    /**
     * <code>required string sort_order = 1;</code>
     *
     * <pre>
     * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
     * </pre>
     */
    public boolean hasSortOrder() {
      return ((bitField0_ & 0x00000001) != 0);
    }

    /**
     * <code>required string sort_order = 1;</code>
     *
     * <pre>
     * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
     * </pre>
     */
    public java.lang.String getSortOrder() {
      java.lang.Object ref = sortOrder_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          sortOrder_ = s;
        }
        return s;
      }
    }

    /**
     * <code>required string sort_order = 1;</code>
     *
     * <pre>
     * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
     * </pre>
     */
    public com.google.protobuf.ByteString getSortOrderBytes() {
      java.lang.Object ref = sortOrder_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        sortOrder_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;

    @java.lang.Override public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) {
        return true;
      }
      if (isInitialized == 0) {
        return false;
      }
      if (!hasSortOrder()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, sortOrder_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) {
        return size;
      }
      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, sortOrder_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector)) {
        return super.equals(obj);
      }
      com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector other = (com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector) obj;
      if (hasSortOrder() != other.hasSortOrder()) {
        return false;
      }
      if (hasSortOrder()) {
        if (!getSortOrder().equals(other.getSortOrder())) {
          return false;
        }
      }
      if (!unknownFields.equals(other.unknownFields)) {
        return false;
      }
      return true;
    }

    @java.lang.Override public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasSortOrder()) {
        hash = (37 * hash) + SORT_ORDER_FIELD_NUMBER;
        hash = (53 * hash) + getSortOrder().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(java.nio.ByteBuffer data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(java.io.InputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseDelimitedFrom(java.io.InputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseDelimitedFrom(java.io.InputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(com.google.protobuf.CodedInputStream input) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
    }

    public static com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parseFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }

    @java.lang.Override public Builder newBuilderForType() {
      return newBuilder();
    }

    public static Builder newBuilder(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }

    @java.lang.Override public Builder toBuilder() {
      return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }

    public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelectorOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryEntitySelector_descriptor;
      }

      @java.lang.Override protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryEntitySelector_fieldAccessorTable.ensureFieldAccessorsInitialized(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.Builder.class);
      }

      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }

      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
        }
      }

      @java.lang.Override public Builder clear() {
        super.clear();
        sortOrder_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override public Builder clone() {
        return super.clone();
      }

      @java.lang.Override public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.internal_static_transit_realtime_MercuryEntitySelector_descriptor;
      }

      @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector getDefaultInstanceForType() {
        return com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.getDefaultInstance();
      }

      @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector build() {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override public com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector buildPartial() {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector result = new com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.sortOrder_ = sortOrder_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
        return super.setField(field, value);
      }

      @java.lang.Override public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }

      @java.lang.Override public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }

      @java.lang.Override public Builder setRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }

      @java.lang.Override public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }

      @java.lang.Override public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector) {
          return mergeFrom((com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector) other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector other) {
        if (other == com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.getDefaultInstance()) {
          return this;
        }
        if (other.hasSortOrder()) {
          bitField0_ |= 0x00000001;
          sortOrder_ = other.sortOrder_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override public final boolean isInitialized() {
        if (!hasSortOrder()) {
          return false;
        }
        return true;
      }

      @java.lang.Override public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
        com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int bitField0_;

      private java.lang.Object sortOrder_ = "";

      /**
       * <code>required string sort_order = 1;</code>
       *
       * <pre>
       * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
       * </pre>
       */
      public boolean hasSortOrder() {
        return ((bitField0_ & 0x00000001) != 0);
      }

      /**
       * <code>required string sort_order = 1;</code>
       *
       * <pre>
       * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
       * </pre>
       */
      public java.lang.String getSortOrder() {
        java.lang.Object ref = sortOrder_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            sortOrder_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }

      /**
       * <code>required string sort_order = 1;</code>
       *
       * <pre>
       * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
       * </pre>
       */
      public com.google.protobuf.ByteString getSortOrderBytes() {
        java.lang.Object ref = sortOrder_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
          sortOrder_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }

      /**
       * <code>required string sort_order = 1;</code>
       *
       * <pre>
       * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
       * </pre>
       */
      public Builder setSortOrder(java.lang.String value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        sortOrder_ = value;
        onChanged();
        return this;
      }

      /**
       * <code>required string sort_order = 1;</code>
       *
       * <pre>
       * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
       * </pre>
       */
      public Builder clearSortOrder() {
        bitField0_ = (bitField0_ & ~0x00000001);
        sortOrder_ = getDefaultInstance().getSortOrder();
        onChanged();
        return this;
      }

      /**
       * <code>required string sort_order = 1;</code>
       *
       * <pre>
       * Format for sort_order is 'GTFS-ID:Priority', e.g. 'MTASBWY:G:16'
       * </pre>
       */
      public Builder setSortOrderBytes(com.google.protobuf.ByteString value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        sortOrder_ = value;
        onChanged();
        return this;
      }

      @java.lang.Override public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }
    }

    static {
      DEFAULT_INSTANCE = new com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector();
    }

    public static com.google.protobuf.Parser<MercuryEntitySelector> parser() {
      return PARSER;
    }
  }

  public static final int MERCURY_FEED_HEADER_FIELD_NUMBER = 1001;

  /**
   * <code>extend .transit_realtime.FeedHeader { ... }</code>
   */
  public static final com.google.protobuf.GeneratedMessage.GeneratedExtension<com.google.transit.realtime.GtfsRealtime.FeedHeader, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader> mercuryFeedHeader = com.google.protobuf.GeneratedMessage.newFileScopedGeneratedExtension(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryFeedHeader.getDefaultInstance());

  public static final int MERCURY_ALERT_FIELD_NUMBER = 1001;

  /**
   * <code>extend .transit_realtime.Alert { ... }</code>
   */
  public static final com.google.protobuf.GeneratedMessage.GeneratedExtension<com.google.transit.realtime.GtfsRealtime.Alert, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert> mercuryAlert = com.google.protobuf.GeneratedMessage.newFileScopedGeneratedExtension(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryAlert.getDefaultInstance());

  public static final int MERCURY_ENTITY_SELECTOR_FIELD_NUMBER = 1001;

  /**
   * <code>extend .transit_realtime.EntitySelector { ... }</code>
   */
  public static final com.google.protobuf.GeneratedMessage.GeneratedExtension<com.google.transit.realtime.GtfsRealtime.EntitySelector, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector> mercuryEntitySelector = com.google.protobuf.GeneratedMessage.newFileScopedGeneratedExtension(com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.class, com.google.transit.realtime.GtfsRealtimeServiceStatus.MercuryEntitySelector.getDefaultInstance());

  private static final com.google.protobuf.Descriptors.Descriptor internal_static_transit_realtime_MercuryFeedHeader_descriptor;

  private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_transit_realtime_MercuryFeedHeader_fieldAccessorTable;

  private static final com.google.protobuf.Descriptors.Descriptor internal_static_transit_realtime_MercuryStationAlternative_descriptor;

  private static com.google.protobuf.GeneratedMessage.FieldAccessorTable internal_static_transit_realtime_MercuryStationAlternative_fieldAccessorTable;

  private static final com.google.protobuf.Descriptors.Descriptor internal_static_transit_realtime_MercuryAlert_descriptor;

  private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_transit_realtime_MercuryAlert_fieldAccessorTable;

  private static final com.google.protobuf.Descriptors.Descriptor internal_static_transit_realtime_MercuryEntitySelector_descriptor;

  private static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internal_static_transit_realtime_MercuryEntitySelector_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = { 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "\n>com/google/transit/realtime/gtfs-realt" + "ime-service-status.proto\u0012\u0010transit_realti" + "me\u001a/com/google/transit/realtime/gtfs-rea" + "ltime.proto\",\n\u0011MercuryFeedHeader\u0012\u0017\n\u000fmerc" + "ury_version\u0018\u0001 \u0002(\t\"\u0089\u0001\n\u0019MercuryStationAlte" + "rnative\u00129\n\u000faffected_entity\u0018\u0001 \u0002(\u000b2 .trans" + "it_realtime.EntitySelector\u00121\n\u0005notes\u0018\u0002 \u0002(" + "\u000b2\".transit_realtime.TranslatedString\"\u008e\u0004" + "\n\fMercuryAlert\u0012\u0012\n\ncreated_at\u0018\u0001 \u0002(\u0004\u0012\u0012\n\nup" + "dated_at\u0018\u0002 \u0002(\u0004\u0012\u0012\n\nalert_type\u0018\u0003 \u0002(\t\u0012H\n\u0013st"
=======
>>>>>>> Unknown file: This is a bug in JDime.
    , 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "ation_alternative\u0018\u0004 \u0003(\u000b2+.transit_realti" + "me.MercuryStationAlternative\u0012\u001b\n\u0013service_" + "plan_number\u0018\u0005 \u0003(\t\u0012\u001c\n\u0014general_order_numbe" + "r\u0018\u0006 \u0003(\t\u0012\u001d\n\u0015display_before_active\u0018\u0007 \u0001(\u0004\u0012H" + "\n\u001chuman_readable_active_period\u0018\b \u0001(\u000b2\".t" + "ransit_realtime.TranslatedString\u0012B\n\u0016addi" + "tional_information\u0018\t \u0001(\u000b2\".transit_realt" + "ime.TranslatedString\u0012\u0016\n\u000edirectionality\u0018\n" + " \u0001(\u0004\u0012;\n\u0011affected_stations\u0018\u000b \u0003(\u000b2 .transi" + "t_realtime.EntitySelector\u0012;\n\u000fscreens_sum"
=======
>>>>>>> Unknown file: This is a bug in JDime.
    , 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "mary\u0018\f \u0001(\u000b2\".transit_realtime.Translated" + "String\"\u00b0\u0006\n\u0015MercuryEntitySelector\u0012\u0012\n\nsort" + "_order\u0018\u0001 \u0002(\t\"\u0082\u0006\n\bPriority\u0012!\n\u001dPRIORITY_NO" + "_SCHEDULED_SERVICE\u0010\u0000\u0012\u001e\n\u001aPRIORITY_ESSENTI" + "AL_SERVICE\u0010\u0001\u0012\u001c\n\u0018PRIORITY_WEEKEND_SERVICE" + "\u0010\u0002\u0012\u001c\n\u0018PRIORITY_WEEKDAY_SERVICE\u0010\u0003\u0012\u001c\n\u0018PRIO" + "RITY_SUNDAY_SCHEDULE\u0010\u0004\u0012\u001e\n\u001aPRIORITY_SATUR" + "DAY_SCHEDULE\u0010\u0005\u0012\u001c\n\u0018PRIORITY_HOLIDAY_SERVI" + "CE\u0010\u0006\u0012\u0013\n\u000fPRIORITY_BUSING\u0010\u0007\u0012\u001b\n\u0017PRIORITY_PL" + "ANNED_DETOUR\u0010\b\u0012\u001a\n\u0016PRIORITY_EXTRA_SERVICE"
=======
>>>>>>> Unknown file: This is a bug in JDime.
    , 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "\u0010\t\u0012\u0019\n\u0015PRIORITY_PLANNED_WORK\u0010\n\u0012\u0018\n\u0014PRIORIT" + "Y_ON_OR_CLOSE\u0010\u000b\u0012\u0018\n\u0014PRIORITY_SLOW_SPEEDS\u0010" + "\f\u0012\u0018\n\u0014PRIORITY_SOME_DELAYS\u0010\r\u0012\u001a\n\u0016PRIORITY_" + "SPECIAL_EVENT\u0010\u000e\u0012\u0015\n\u0011PRIORITY_CROWDING\u0010\u000f\u0012\u001d" + "\n\u0019PRIORITY_STATIONS_SKIPPED\u0010\u0010\u0012\u0013\n\u000fPRIORIT" + "Y_DELAYS\u0010\u0011\u0012\u001d\n\u0019PRIORITY_EXPRESS_TO_LOCAL\u0010" + "\u0012\u0012\u001a\n\u0016PRIORITY_SOME_REROUTES\u0010\u0013\u0012\u001d\n\u0019PRIORIT" + "Y_LOCAL_TO_EXPRESS\u0010\u0014\u0012\u0014\n\u0010PRIORITY_DETOURS" + "\u0010\u0015\u0012\u001b\n\u0017PRIORITY_SERVICE_CHANGE\u0010\u0016\u0012\u001c\n\u0018PRIOR" + "ITY_TRAINS_REROUTED\u0010\u0017\u0012\u001b\n\u0017PRIORITY_PART_S"
=======
>>>>>>> Unknown file: This is a bug in JDime.
    , 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "USPENDED\u0010\u0018\u0012\u001d\n\u0019PRIORITY_MULTIPLE_IMPACTS\u0010" + "\u0019\u0012\u0016\n\u0012PRIORITY_SUSPENDED\u0010\u001a:_\n\u0013mercury_fee"
=======
    "\n>com/google/transit/realtime/gtfs-realt" + "ime-service-status.proto\u0012\u0010transit_realti" + "me\u001a/com/google/transit/realtime/gtfs-rea" + "ltime.proto\",\n\u0011MercuryFeedHeader\u0012\u0017\n\u000fmerc" + "ury_version\u0018\u0001 \u0002(\t\"J\n\fMercuryAlert\u0012\u0012\n\ncre" + "ated_at\u0018\u0001 \u0002(\u0004\u0012\u0012\n\nupdated_at\u0018\u0002 \u0002(\u0004\u0012\u0012\n\nale" + "rt_type\u0018\u0003 \u0002(\t\"\u00d9\b\n\u0015MercuryEntitySelector\u0012" + "\u0012\n\nsort_order\u0018\u0001 \u0002(\t\"\u00dc\u0004\n\bPriority\u0012!\n\u001dPRIO" + "RITY_NO_SCHEDULED_SERVICE\u0010\u0001\u0012\u001c\n\u0018PRIORITY_" + "SUNDAY_SCHEDULE\u0010\u0002\u0012\u001e\n\u001aPRIORITY_SATURDAY_S" + "CHEDULE\u0010\u0003\u0012\u001c\n\u0018PRIORITY_HOLIDAY_SERVICE\u0010\u0004\u0012" + "\u001a\n\u0016PRIORITY_EXTRA_SERVICE\u0010\u0005\u0012\u0019\n\u0015PRIORITY_" + "PLANNED_WORK\u0010\u0006\u0012\u0018\n\u0014PRIORITY_ON_OR_CLOSE\u0010\u0007" + "\u0012\u0018\n\u0014PRIORITY_SLOW_SPEEDS\u0010\b\u0012\u0018\n\u0014PRIORITY_S" + "OME_DELAYS\u0010\t\u0012\u001a\n\u0016PRIORITY_SPECIAL_EVENT\u0010\n" + "\u0012\u001d\n\u0019PRIORITY_STATIONS_SKIPPED\u0010\u000b\u0012\u0013\n\u000fPRIOR" + "ITY_DELAYS\u0010\f\u0012\u001d\n\u0019PRIORITY_EXPRESS_TO_LOCA" + "L\u0010\r\u0012\u001a\n\u0016PRIORITY_SOME_REROUTES\u0010\u000e\u0012\u001d\n\u0019PRIOR" + "ITY_LOCAL_TO_EXPRESS\u0010\u000f\u0012\u001b\n\u0017PRIORITY_SERVI" + "CE_CHANGE\u0010\u0010\u0012\u001c\n\u0018PRIORITY_TRAINS_REROUTED\u0010" + "\u0011\u0012\u001b\n\u0017PRIORITY_PART_SUSPENDED\u0010\u0012\u0012\u001d\n\u0019PRIORI" + "TY_MULTIPLE_IMPACTS\u0010\u0013\u0012\u0016\n\u0012PRIORITY_SUSPEN" + "DED\u0010\u0014\u0012\u0013\n\u000fPRIORITY_BUSING\u0010\u0015\"\u00cc\u0003\n\u000fNyctBusPr" + "iority\u0012*\n&NYCT_BUS_PRIORITY_NO_SCHEDULED" + "_SERVICE\u0010\u0001\u0012%\n!NYCT_BUS_PRIORITY_SUNDAY_S" + "CHEDULE\u0010\u0002\u0012\'\n#NYCT_BUS_PRIORITY_SATURDAY_" + "SCHEDULE\u0010\u0003\u0012%\n!NYCT_BUS_PRIORITY_HOLIDAY_" + "SERVICE\u0010\u0004\u0012$\n NYCT_BUS_PRIORITY_PLANNED_D" + "ETOUR\u0010\u0005\u0012#\n\u001fNYCT_BUS_PRIORITY_EXTRA_SERVI" + "CE\u0010\u0006\u0012\"\n\u001eNYCT_BUS_PRIORITY_PLANNED_WORK\u0010\u0007" + "\u0012#\n\u001fNYCT_BUS_PRIORITY_SPECIAL_EVENT\u0010\u000b\u0012\u001c\n" + "\u0018NYCT_BUS_PRIORITY_DELAYS\u0010\r\u0012\u001d\n\u0019NYCT_BUS_" + "PRIORITY_DETOURS\u0010\u0010\u0012$\n NYCT_BUS_PRIORITY_" + "SERVICE_CHANGE\u0010\u0012\u0012\u001f\n\u001bNYCT_BUS_PRIORITY_SU" + "SPENDED\u0010\u0016:_\n\u0013mercury_feed_header\u0012\u001c.trans"
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
     + 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "d_header\u0012\u001c.transit_realtime.FeedHeader\u0018\u00e9"
=======
    "it_realtime.FeedHeader\u0018\u00e9\u0007 \u0001(\u000b2#.transit_"
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
     + 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "\u0007 \u0001(\u000b2#.transit_realtime.MercuryFeedHead"
=======
    "realtime.MercuryFeedHeader:O\n\rmercury_al"
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
     + 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "er:O\n\rmercury_alert\u0012\u0017.transit_realtime.A"
=======
    "ert\u0012\u0017.transit_realtime.Alert\u0018\u00e9\u0007 \u0001(\u000b2\u001e.tr"
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
     + 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "lert\u0018\u00e9\u0007 \u0001(\u000b2\u001e.transit_realtime.MercuryAl"
=======
    "ansit_realtime.MercuryAlert:k\n\u0017mercury_e"
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
     + 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "ert:k\n\u0017mercury_entity_selector\u0012 .transit"
=======
    "ntity_selector\u0012 .transit_realtime.Entity"
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
     + 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "_realtime.EntitySelector\u0018\u00e9\u0007 \u0001(\u000b2\'.transi"
=======
    "Selector\u0018\u00e9\u0007 \u0001(\u000b2\'.transit_realtime.Mercu"
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
     + 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "t_realtime.MercuryEntitySelectorB\u001d\n\u001bcom."
=======
    "ryEntitySelectorB\u001d\n\u001bcom.google.transit.r"
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
     + 
<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/left.java
    "google.transit.realtime"
=======
    "ealtime"
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/2c511739c7f1597917511dc3484d38de128c51c3/src/main/java/com/google/transit/realtime/GtfsRealtimeServiceStatus.java/right.java
     };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[] { com.google.transit.realtime.GtfsRealtime.getDescriptor() });
    internal_static_transit_realtime_MercuryFeedHeader_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_transit_realtime_MercuryFeedHeader_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(internal_static_transit_realtime_MercuryFeedHeader_descriptor, new java.lang.String[] { "MercuryVersion" });
    internal_static_transit_realtime_MercuryStationAlternative_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_transit_realtime_MercuryStationAlternative_fieldAccessorTable = new com.google.protobuf.GeneratedMessage.FieldAccessorTable(internal_static_transit_realtime_MercuryStationAlternative_descriptor, new java.lang.String[] { "AffectedEntity", "Notes" });
    internal_static_transit_realtime_MercuryAlert_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_transit_realtime_MercuryAlert_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(internal_static_transit_realtime_MercuryAlert_descriptor, new java.lang.String[] { "CreatedAt", "UpdatedAt", "AlertType", "StationAlternative", "ServicePlanNumber", "GeneralOrderNumber", "DisplayBeforeActive", "HumanReadableActivePeriod", "AdditionalInformation", "Directionality", "AffectedStations", "ScreensSummary" });
    internal_static_transit_realtime_MercuryEntitySelector_descriptor = getDescriptor().getMessageTypes().get(3);
    internal_static_transit_realtime_MercuryEntitySelector_fieldAccessorTable = new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(internal_static_transit_realtime_MercuryEntitySelector_descriptor, new java.lang.String[] { "SortOrder" });
    mercuryFeedHeader.internalInit(descriptor.getExtensions().get(0));
    mercuryAlert.internalInit(descriptor.getExtensions().get(1));
    mercuryEntitySelector.internalInit(descriptor.getExtensions().get(2));
    com.google.transit.realtime.GtfsRealtime.getDescriptor();
  }
}