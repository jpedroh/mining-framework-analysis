package com.keybox.manage.model;
import java.util.Date;

/**
 * public key value object
 */
public class PublicKey {
  Long id;

  Long userId;

  String keyNm;

  String 
<<<<<<< /usr/src/app/output/bastillion-io/bastillion/e6ae3604ccbbaf42827e6eb4749121a250b95571/src/main/java/com/keybox/manage/model/PublicKey.java/left.java
  keyTp
=======
  username
>>>>>>> /usr/src/app/output/bastillion-io/bastillion/e6ae3604ccbbaf42827e6eb4749121a250b95571/src/main/java/com/keybox/manage/model/PublicKey.java/right.java
  ;

  String publicKey;

  String 
<<<<<<< /usr/src/app/output/bastillion-io/bastillion/e6ae3604ccbbaf42827e6eb4749121a250b95571/src/main/java/com/keybox/manage/model/PublicKey.java/left.java
  keyFp
=======
  type
>>>>>>> /usr/src/app/output/bastillion-io/bastillion/e6ae3604ccbbaf42827e6eb4749121a250b95571/src/main/java/com/keybox/manage/model/PublicKey.java/right.java
  ;

  String fingerprint;

  boolean enabled;

  Date createDt;

  Profile profile;

  public String getKeyNm() {
    return keyNm;
  }

  public void setKeyNm(String keyNm) {
    this.keyNm = keyNm;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getKeyTp() {
    return keyTp;
  }

  public void setKeyTp(String keyTp) {
    this.keyTp = keyTp;
  }

  public String getKeyFp() {
    return keyFp;
  }

  public void setKeyFp(String keyFp) {
    this.keyFp = keyFp;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Profile getProfile() {
    return profile;
  }

  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFingerprint() {
    return fingerprint;
  }

  public void setFingerprint(String fingerprint) {
    this.fingerprint = fingerprint;
  }

  public Date getCreateDt() {
    return createDt;
  }

  public void setCreateDt(Date createDt) {
    this.createDt = createDt;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}