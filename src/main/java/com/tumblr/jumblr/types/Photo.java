package com.tumblr.jumblr.types;
import java.io.File;
import java.util.List;

/**
 * This class represents a Photo in a PhotoPost
 * @author jc
 */
public class Photo {
  public enum PhotoType {
    SOURCE("source"),
    FILE("data")
    ;

    private final String prefix;

    private PhotoType(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return this.prefix;
    }
  }



  private String caption;

  private List<PhotoSize> alt_sizes;

  private 
<<<<<<< /usr/src/app/output/tumblr/jumblr/f6efe42bf1b29e52b72832b8c1754fc93cd3ec4b/src/main/java/com/tumblr/jumblr/types/Photo.java/left.java
  String
=======
  PhotoSize
>>>>>>> /usr/src/app/output/tumblr/jumblr/f6efe42bf1b29e52b72832b8c1754fc93cd3ec4b/src/main/java/com/tumblr/jumblr/types/Photo.java/right.java
   
<<<<<<< /usr/src/app/output/tumblr/jumblr/f6efe42bf1b29e52b72832b8c1754fc93cd3ec4b/src/main/java/com/tumblr/jumblr/types/Photo.java/left.java
  source
=======
  original_size
>>>>>>> /usr/src/app/output/tumblr/jumblr/f6efe42bf1b29e52b72832b8c1754fc93cd3ec4b/src/main/java/com/tumblr/jumblr/types/Photo.java/right.java
  ;

  private File file;

  /**
     * Create a new photo with a data
     * @param file the file for the photo
     */
  public Photo(File file) {
    this.file = file;
  }

  /**
     * Create a new photo with a source
     * @param source the source for the photo
     */
  public Photo(String source) {
    this.source = source;
  }

  /**
     * Get the type of this photo
     * @return PhotoType the type of photo
     */
  public PhotoType getType() {
    if (this.source != null) {
      return PhotoType.SOURCE;
    }
    if (this.file != null) {
      return PhotoType.FILE;
    }
    return null;
  }

  /**
     * Get the sizes of this Photo
     * @return PhotoSize[] sizes
     */
  public List<PhotoSize> getSizes() {
    return alt_sizes;
  }

  /**
     * Get the original sized photo
     * @return the original sized PhotoSize
     */
  public PhotoSize getOriginalSize() {
    return original_size;
  }

  /**
     * Get the caption of this photo
     * @return the caption
     */
  public String getCaption() {
    return this.caption;
  }

  /**
     * Get the detail for this photo
     * @return the detail (String or File)
     */
  protected Object getDetail() {
    if (this.source != null) {
      return source;
    } else {
      if (this.file != null) {
        return file;
      } else {
        return null;
      }
    }
  }
}