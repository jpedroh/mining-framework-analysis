<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/a34bc69082c58aafcb062b66f928d7035285cc5e/src/main/java/com/wrapper/spotify/objects/TrackLink.java/left.java
fatal: path 'src/main/java/com/wrapper/spotify/objects/TrackLink.java' does not exist in '57e229e23b58d6dca00779c45f91586f39a22499'
||||||| /usr/src/app/output/thelinmichael/spotify-web-api-java/a34bc69082c58aafcb062b66f928d7035285cc5e/src/main/java/com/wrapper/spotify/objects/TrackLink.java/base.java
package com.wrapper.spotify.objects;

public class TrackLink {

  private ExternalUrls externalUrls;
  private String href;
  private String id;
  private ObjectType type = ObjectType.TRACK;
  private String uri;

  public ExternalUrls getExternalUrls() {
    return externalUrls;
  }

  public void setExternalUrls(ExternalUrls externalUrls) {
    this.externalUrls = externalUrls;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ObjectType getType() {
    return type;
  }

  public void setType(ObjectType type) {
    this.type = type;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
}
=======
package com.wrapper.spotify.objects;

import com.google.gson.JsonObject;

public class TrackLink extends AbstractModelObject {
  private final ExternalUrls externalUrls;
  private final String href;
  private final String id;
  private final ObjectType type;
  private final String uri;

  private TrackLink(final TrackLink.Builder builder) {
    super(builder);

    this.externalUrls = builder.externalUrls;
    this.href = builder.href;
    this.id = builder.id;
    this.type = builder.type;
    this.uri = builder.uri;
  }

  public ExternalUrls getExternalUrls() {
    return externalUrls;
  }

  public String getHref() {
    return href;
  }

  public String getId() {
    return id;
  }

  public ObjectType getType() {
    return type;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public Builder builder() {
    return new Builder();
  }

  public static final class Builder extends AbstractModelObject.Builder {
    private ExternalUrls externalUrls;
    private String href;
    private String id;
    private ObjectType type;
    private String uri;

    public Builder setExternalUrls(ExternalUrls externalUrls) {
      this.externalUrls = externalUrls;
      return this;
    }

    public Builder setHref(String href) {
      this.href = href;
      return this;
    }

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setType(ObjectType type) {
      this.type = type;
      return this;
    }

    public Builder setUri(String uri) {
      this.uri = uri;
      return this;
    }

    @Override
    public TrackLink build() {
      return new TrackLink(this);
    }
  }

  public static final class JsonUtil extends AbstractModelObject.JsonUtil<TrackLink> {
    public TrackLink createModelObject(JsonObject jsonObject) {
      if (jsonObject == null || jsonObject.isJsonNull()) {
        return null;
      }

      return new TrackLink.Builder()
              .setExternalUrls(new ExternalUrls.JsonUtil().createModelObject(jsonObject.getAsJsonObject("external_urls")))
              .setHref(jsonObject.get("href").getAsString())
              .setId(jsonObject.get("id").getAsString())
              .setType(ObjectType.valueOf(jsonObject.get("type").getAsString().toUpperCase()))
              .setUri(jsonObject.get("uri").getAsString())
              .build();
    }
  }
}
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/a34bc69082c58aafcb062b66f928d7035285cc5e/src/main/java/com/wrapper/spotify/objects/TrackLink.java/right.java
