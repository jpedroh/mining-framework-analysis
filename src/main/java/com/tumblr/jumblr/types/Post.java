package com.tumblr.jumblr.types;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;

public class Post extends Resource {
  private Long id;

  private String author;

  private String reblog_key;

  private String blog_name;

  private String post_url;

  private String type;

  private Long timestamp;

  private String state;

  private String format;

  private String date;

  private List<String> tags;

  private Boolean bookmarklet, mobile;

  private String source_url;

  private String source_title;

  private Boolean liked;

  private String slug;

  private Long reblogged_from_id;

  private String reblogged_from_name;

  private Long note_count;

  public String getAuthorId() {
    return author;
  }

  public Boolean isLiked() {
    return liked;
  }

  public String getSourceTitle() {
    return source_title;
  }

  public String getSourceUrl() {
    return source_url;
  }

  public Boolean isMobile() {
    return mobile;
  }

  public Boolean isBookmarklet() {
    return bookmarklet;
  }

  public String getFormat() {
    return format;
  }

  public String getState() {
    return state;
  }

  public String getPostUrl() {
    return post_url;
  }

  public List<String> getTags() {
    return tags;
  }

  public Long getNoteCount() {
    return note_count;
  }

  public String getDateGMT() {
    return date;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public String getType() {
    return type;
  }

  public Long getId() {
    return id;
  }

  public String getBlogName() {
    return blog_name;
  }

  public String getReblogKey() {
    return this.reblog_key;
  }

  public String getSlug() {
    return this.slug;
  }

  public Long getRebloggedFromId() {
    return reblogged_from_id;
  }

  public String getRebloggedFromName() {
    return reblogged_from_name;
  }

  public void delete() {
    client.postDelete(blog_name, id);
  }

  public Post reblog(String blogName, Map<String, ?> options) {
    return client.postReblog(blogName, id, reblog_key, options);
  }

  public Post reblog(String blogName) {
    return this.reblog(blogName, null);
  }

  public void like() {
    client.like(this.id, this.reblog_key);
  }

  public void unlike() {
    client.unlike(this.id, this.reblog_key);
  }

  public void setBlogName(String blogName) {
    blog_name = blogName;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public void setDate(String dateString) {
    this.date = dateString;
  }

  public void setDate(Date date) {
    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    setDate(df.format(date));
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public void addTag(String tag) {
    if (this.tags == null) {
      tags = new ArrayList<String>();
    }
    this.tags.add(tag);
  }

  public void removeTag(String tag) {
    this.tags.remove(tag);
  }

  public void save() throws IOException {
    if (id == null) {
      this.id = client.postCreate(blog_name, detail());
    } else {
      client.postEdit(blog_name, id, detail());
    }
  }

  protected Map<String, Object> detail() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("state", state);
    map.put("tags", getTagString());
    map.put("format", format);
    map.put("slug", slug);
    map.put("date", date);
    return map;
  }

  private String getTagString() {
    return tags == null ? "" : StringUtils.join(tags.toArray(new String[0]), ",");
  }

  @Override public String toString() {
    return "[" + this.getClass().getName() + " (" + blog_name + ":" + id + ")]";
  }

  private Note[] notes;

  public Note[] getNotes() {
    if (notes == null) {
      return null;
    }
    Note[] result = new Note[notes.length];
    System.arraycopy(notes, 0, result, 0, notes.length);
    return result;
  }
}