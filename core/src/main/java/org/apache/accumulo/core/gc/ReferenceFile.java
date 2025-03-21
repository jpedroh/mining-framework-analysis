package org.apache.accumulo.core.gc;
import java.util.Objects;
import org.apache.accumulo.core.data.TableId;
import org.apache.accumulo.core.metadata.ScanServerRefTabletFile;
import org.apache.accumulo.core.metadata.StoredTabletFile;
import org.apache.hadoop.fs.Path;

/**
 * A GC reference used for streaming and delete markers. This type is a file. Subclass is a
 * directory.
 */
public class ReferenceFile implements Reference, Comparable<ReferenceFile> {
  public final TableId tableId;

  public final boolean isScan;

  protected final String metadataPath;

  protected ReferenceFile(TableId tableId, String metadataPath, boolean isScan) {
    this.tableId = Objects.requireNonNull(tableId);
    this.metadataPath = Objects.requireNonNull(metadataPath);
    this.isScan = isScan;
  }


<<<<<<< /usr/src/app/output/apache/accumulo/c4c8c572c8be14d882899f8f9370700227e87423/core/src/main/java/org/apache/accumulo/core/gc/ReferenceFile.java/left.java
  public ReferenceFile(TableId tableId, Path metadataPathPath) {
    this.tableId = Objects.requireNonNull(tableId);
    this.metadataPath = Objects.requireNonNull(metadataPathPath.toString());
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  public static ReferenceFile forFile(TableId tableId, String metadataEntry) {
    return new ReferenceFile(tableId, metadataEntry, false);
  }

  public ReferenceFile(TableId tableId, ScanServerRefTabletFile tabletFile) {
    this.tableId = Objects.requireNonNull(tableId);
    this.metadataPath = Objects.requireNonNull(tabletFile.getNormalizedPathStr());
  }

  public static ReferenceFile forScan(TableId tableId, String metadataEntry) {
    return new ReferenceFile(tableId, metadataEntry, true);
  }

  public ReferenceFile(TableId tableId, StoredTabletFile tabletFile) {
    this.tableId = Objects.requireNonNull(tableId);
    this.metadataPath = Objects.requireNonNull(tabletFile.getMetadataPath());
  }

  @Override public boolean isDirectory() {
    return false;
  }

  @Override public boolean isScan() {
    return isScan;
  }

  @Override public TableId getTableId() {
    return tableId;
  }

  @Override public String getMetadataPath() {
    return metadataPath;
  }

  @Override public int compareTo(ReferenceFile that) {
    if (equals(that)) {
      return 0;
    } else {
      return this.metadataPath.compareTo(that.metadataPath);
    }
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ReferenceFile other = (ReferenceFile) obj;
    return metadataPath.equals(other.metadataPath);
  }

  @Override public int hashCode() {
    return this.metadataPath.hashCode();
  }

  @Override public String toString() {
    return "Reference [id=" + tableId + ", ref=" + metadataPath + "]";
  }
}