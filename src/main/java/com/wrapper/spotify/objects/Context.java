<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/a34bc69082c58aafcb062b66f928d7035285cc5e/src/main/java/com/wrapper/spotify/objects/Context.java/left.java
fatal: path 'src/main/java/com/wrapper/spotify/objects/Context.java' does not exist in '57e229e23b58d6dca00779c45f91586f39a22499'
||||||| /usr/src/app/output/thelinmichael/spotify-web-api-java/a34bc69082c58aafcb062b66f928d7035285cc5e/src/main/java/com/wrapper/spotify/objects/Context.java/base.java
package com.wrapper.spotify.objects;

public class Context {

  private ObjectType type;
  private String href;
  private ExternalUrls externalUrls;
  private String uri;

  public ObjectType getType() {
    return type;
  }

  public void setType(ObjectType type) {
    this.type = type;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public ExternalUrls getExternalUrls() {
    return externalUrls;
  }

  public void setExternalUrls(ExternalUrls externalUrls) {
    this.externalUrls = externalUrls;
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

public class Context extends AbstractModelObject {
  private final ObjectType type;
  private final String href;
  private final ExternalUrls externalUrls;
  private final String uri;

  private Context(final Context.Builder builder) {
    super(builder);

    this.type = builder.type;
    this.href = builder.href;
    this.externalUrls = builder.externalUrls;
    this.uri = builder.uri;
  }

  public ObjectType getType() {
    return type;
  }

  public String getHref() {
    return href;
  }

  public ExternalUrls getExternalUrls() {
    return externalUrls;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public Builder builder() {
    return new Builder();
  }

  public static final class Builder extends AbstractModelObject.Builder {
    private ObjectType type;
    private String href;
    private ExternalUrls externalUrls;
    private String uri;

    public Builder setType(ObjectType type) {
      this.type = type;
      return this;
    }

    public Builder setHref(String href) {
      this.href = href;
      return this;
    }

    public Builder setExternalUrls(ExternalUrls externalUrls) {
      this.externalUrls = externalUrls;
      return this;
    }

    public Builder setUri(String uri) {
      this.uri = uri;
      return this;
    }

    @Override
    public Context build() {
      return new Context(this);
    }
  }

  public static final class JsonUtil extends AbstractModelObject.JsonUtil<Context> {
    public Context createModelObject(JsonObject jsonObject) {
      if (jsonObject == null || jsonObject.isJsonNull()) {
        return null;
      }

      return new Context.Builder()
              .setType(ObjectType.valueOf(jsonObject.get("type").getAsString().toUpperCase()))
              .setHref(jsonObject.get("href").getAsString())
              .setExternalUrls(new ExternalUrls.JsonUtil().createModelObject(jsonObject.getAsJsonObject("external_urls")))
              .setUri(jsonObject.get("uri").getAsString())
              .build();
    }
  }
}
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/a34bc69082c58aafcb062b66f928d7035285cc5e/src/main/java/com/wrapper/spotify/objects/Context.java/right.java
